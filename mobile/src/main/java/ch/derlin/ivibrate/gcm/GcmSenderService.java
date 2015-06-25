package ch.derlin.ivibrate.gcm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.app.App;
import ch.derlin.ivibrate.sql.SqlDataSource;
import ch.derlin.ivibrate.sql.entities.Message;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static ch.derlin.ivibrate.gcm.GcmConstants.*;

/**
 * Singleton service in charge of sending message
 * to the GCM server. Can also be used as a bound service.
 * Started with the application.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class GcmSenderService extends Service{

    private static GcmSenderService INSTANCE;

    private GoogleCloudMessaging gcm;
    private String regid; // current regid of the user
    private Gson gson = new GsonBuilder().create();
    // to generate unique message ids
    private static Random random = new Random();
    private Set<Integer> msgIdSet = new HashSet<>();
    // to notify a message has been sent
    private LocalBroadcastManager mBroadcastManager;

    // ----------------------------------------------------
    public class GcmSenderBinder extends Binder{
        GcmSenderService getService(){
            // Return this instance of LocalService so clients can call public methods
            return GcmSenderService.this;
        }
    }

    private final IBinder mBinder = new GcmSenderBinder();


    @Override
    public IBinder onBind( Intent intent ){
        return mBinder;
    }
    // ----------------------------------------------------


    public static GcmSenderService getInstance(){
        return INSTANCE;
    }

    // ----------------------------------------------------


    public GcmSenderService(){
    }


    @Override
    public void onCreate(){
        super.onCreate();
        gcm = GoogleCloudMessaging.getInstance( getApplicationContext() );
        loadRegIdAsync();
        INSTANCE = this;
        mBroadcastManager = LocalBroadcastManager.getInstance( this );
    }


    @Override
    public void onDestroy(){
        INSTANCE = null;
        if( gcm != null ){
            gcm.close();
            gcm = null;
        }
        super.onDestroy();
    }


    // ----------------------------------------------------


    public String getRegId(){
        return regid;
    }


    /**
     * Register to the IVibrate server, sending a phone with a regid.
     * Note that if the current regid is already valid, no message will be sent.
     * A regid can be updated by google (never happens in practice) or everytime the
     * application is installed or the system is updated.
     *
     * @param phone the phone number of this app, in swiss format (07XXXXXXXX)
     */
    public void registerToServer( String phone ){
        String regidKey = getApplicationContext().getString( R.string.pref_regid );
        String versionKey = getApplicationContext().getString( R.string.pref_app_version );

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
        String prefRegId = prefs.getString( regidKey, null );
        String prefsVersion = prefs.getString( versionKey, "" );

        if( prefRegId == null || !prefRegId.equals( regid ) || !prefsVersion.equals( Build.VERSION.RELEASE ) ){
            // refresh regid
            prefs.edit() //
                    .putString( regidKey, regid ) //
                    .putString( versionKey, Build.VERSION.RELEASE ) //
                    .apply();
            // send new regid to server
            Bundle data = new Bundle();
            data.putString( ACTION_KEY, GcmConstants.ACTION_REGISTER );
            data.putString( MESSAGE_KEY, phone );
            sendData( data );
            Log.d( getPackageName(), "NEW REGISTRATION " + Build.VERSION.RELEASE );
        }
    }


    /**
     * Unregister to the IVibrate server.
     */
    public void unregisterFromServer(){
        String phone = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() ) //
                .getString( getString( R.string.pref_phone ), null );

        if( phone == null ) return;

        Bundle data = new Bundle();
        data.putString( ACTION_KEY, ACTION_REGISTER );
        data.putString( MESSAGE_KEY, phone );
        sendData( data );
    }


    /**
     * Ask the server for all the phone numbers currently registered.
     */
    public void askForAccounts(){
        Bundle data = new Bundle();
        data.putString( ACTION_KEY, ACTION_GET_ACCOUNTS );
        sendData( data );
    }


    /**
     * Send a message.
     *
     * @param to      the phone of the receiver.
     * @param pattern the vibration pattern.
     * @param text    the optional text.
     */
    public void sendMessage( String to, long[] pattern, String text ){
        // save it to local db
        Message m = Message.createSentInstance( to, pattern, text );
        saveMessage( m );

        // create data bundle
        Bundle data = new Bundle();
        data.putString( ACTION_KEY, ACTION_MESSAGE_RECEIVED );
        data.putString( TO_KEY, to );
        data.putString( PATTERN_KEY, gson.toJson( pattern ) );
        data.putString( MESSAGE_KEY, text );
        data.putString( MESSAGE_ID_KEY, "" + m.getId() );
        // send message
        sendData( data );
        // notify the GcmCallbacks (local broadcast)
        notify( data );
    }


    /**
     * Send a message.
     *
     * @param to      the phone number of the receiver.
     * @param pattern the vibration pattern.
     */
    public void sendMessage( String to, long[] pattern ){
        sendMessage( to, pattern, null );
    }


    /**
     * Send an echo message. Used mainly to test the server.
     *
     * @param pattern the vibration pattern.
     */
    public void sendEcho( long[] pattern ){
        sendEcho( gson.toJson( pattern ) );
    }


    /**
     * Send an echo message. Used mainly to test the server.
     *
     * @param message the text message to echo.
     */
    public void sendEcho( String message ){
        Bundle data = new Bundle();
        data.putString( ACTION_KEY, GcmConstants.ACTION_ECHO );
        data.putString( MESSAGE_KEY, message );
        sendData( data );
    }


    /**
     * Send data to the server. Will add the regid to the data and send
     * it as is.
     *
     * @param data the data bundle.
     */
    public void sendData( Bundle data ){
        data.putString( REGID_KEY, regid ); // always put regid
        try{
            String id = getMessageId();
            gcm.send( PROJECT_ID + "@gcm.googleapis.com", id, data );
        }catch( IOException e ){
            Log.e( "GCM", "IOException while sending registration id", e );
        }
    }

    // ----------------------------------------------------


    /* notify a message has been sent. */
    private void notify( Bundle data ){
        Intent i = new Intent( GCM_SERVICE_INTENT_FILTER );
        i.putExtra( EXTRA_EVT_TYPE, ACTION_MESSAGE_SENT );
        i.putExtras( data );
        mBroadcastManager.sendBroadcast( i );
    }


    /* save a message after it has been sent. */
    private void saveMessage( Message message ){
        // add message to db
        Context context = App.getAppContext();
        try( SqlDataSource src = new SqlDataSource( context, true ) ){
            src.addMessage( message );
        }catch( SQLException e ){
            Log.d( context.getPackageName(), "error adding message " + e );
        }
    }


    /* Generate a unique message id. */
    private String getMessageId(){
        int id;
        do{
            id = random.nextInt();
        }while( msgIdSet.contains( id ) );

        msgIdSet.add( id );
        return Integer.toString( id );
    }


    /* Get the regid. Ask the regid to Google API and verify it is the same as the one
    * stored in the shared preferences. If not, update it. */
    private void loadRegIdAsync(){
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground( Void... params ){
                String msg;
                try{
                    String authorizedEntity = PROJECT_ID; // Project id from Google Developers Console
                    String scope = "GCM"; // e.g. communicating using GCM, but you can use any
                    // URL-safe characters up to a maximum of 1000, or
                    // you can also leave it blank.
                    regid = InstanceID.getInstance( App.getAppContext() ).getToken( authorizedEntity, scope );

                    //                    String regid = gcm.register( GcmConstants.PROJECT_ID );
                    PreferenceManager.getDefaultSharedPreferences( getApplicationContext() ).edit() //
                            .putString( getString( R.string.pref_regid ), regid ).commit();
                    msg = "Device registered to GCM server, registration ID=" + regid;
                    Log.i( "GCM", msg );

                }catch( IOException ex ){
                    Log.d( getPackageName(), "Error :" + ex.getMessage() );

                }
                return null;
            }

        }.execute();
    }

}
