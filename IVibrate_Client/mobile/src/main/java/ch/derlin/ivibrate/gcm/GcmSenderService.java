package ch.derlin.ivibrate.gcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.app.App;
import ch.derlin.ivibrate.app.AppUtils;
import ch.derlin.ivibrate.sql.SqlDataSource;
import ch.derlin.ivibrate.sql.entities.Message;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.sql.SQLException;

import static ch.derlin.ivibrate.gcm.GcmConstants.*;

/**
 * Intent service in charge of sending message
 * to the GCM server.
 * Static methods are available to wake up the service
 * with the proper arguments.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class GcmSenderService extends IntentService{

    public GcmSenderService(){
        super( "GcmSenderService" );
    }

    /* *****************************************************************
     * static methods to launch the intent service
     * ****************************************************************/


    /**
     * Register to the IVibrate server upon a regid change.
     */
    public static void register(){
        wakeUpService( ACTION_REGISTER, new Bundle() );
    }


    /**
     * Register to the IVibrate server upon a regid change
     * or a first registration. The phone will be saved to
     * preferences.
     *
     * @param phone the phone number, in swiss format: 07XXXXXXXX.
     */
    public static void register( String phone ){
        PreferenceManager.getDefaultSharedPreferences( App.getAppContext() ) //
                .edit().putString( App.getAppContext().getString( R.string.pref_phone ), phone ).commit();
        register();
    }


    /**
     * Send a ack.
     *
     * @param to        the receiver's phone.
     * @param messageId the message id to ack.
     */
    public static void sendAck( String to, String messageId ){
        Bundle data = new Bundle();
        data.putString( TO_KEY, to );
        data.putString( MESSAGE_ID_KEY, messageId );
        wakeUpService( ACTION_ACK, data );
    }


    /**
     * Ask the server for all the phone numbers currently registered.
     */
    public static void askForAccounts(){
        wakeUpService( ACTION_GET_ACCOUNTS, new Bundle() );
    }


    /**
     * Send a message.
     *
     * @param to      the phone of the receiver.
     * @param pattern the vibration pattern.
     * @param text    the optional text.
     */
    public static void sendMessage( String to, long[] pattern, String text ){

        // create data bundle
        Bundle data = new Bundle();
        data.putString( TO_KEY, to );
        data.putString( PATTERN_KEY, App.getGson().toJson( pattern ) );
        data.putString( MESSAGE_KEY, text );

        wakeUpService( ACTION_MESSAGE_RECEIVED, data );
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
     * @param message the text message to echo.
     */
    public static void sendEcho( String message ){
        Bundle data = new Bundle();
        data.putString( MESSAGE_KEY, message );

        wakeUpService( ACTION_ECHO, data );
    }

    // ----------------------------------------------------


    private static void wakeUpService( String action, Bundle data ){
        data.putString( ACTION_KEY, action );
        Intent intent = new Intent( App.getAppContext(), GcmSenderService.class );
        intent.putExtras( data );

        App.getAppContext().startService( intent );
    }

    /* *****************************************************************
     * handling intents
     * ****************************************************************/


    @Override
    protected void onHandleIntent( Intent intent ){
        Bundle extra = intent.getExtras();

        if( extra == null ){
            Log.e( getPackageName(), "Error: SenderIntentService called without extra" );
            return;
        }

        String action = extra.getString( ACTION_KEY, null );

        if( ACTION_REGISTER.equals( action ) ){
            loadRegIdAsync();

        }else if( ACTION_UNREGISTER.equals( action ) ){
            unregisterFromServer();

        }else{
            if( ACTION_MESSAGE_RECEIVED.equals( action ) ){
                Long id = saveMessage( extra );

                if( id == null ){
                    Toast.makeText( getApplicationContext(), "Error: the message could not be saved (not sent)", //
                            Toast.LENGTH_SHORT ).show();
                    return;
                }
                extra.putString( MESSAGE_ID_KEY, "" + id );
                sendData( extra );
                notify( extra );
            }else{
                sendData( extra );
            }

        }
    }




    /* *****************************************************************
     * private utils
     * ****************************************************************/


    /*
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


    /*
   * Register to the IVibrate server, sending a phone with a regid.
   * Note that if the current regid is already valid, no message will be sent.
   * A regid can be updated by google (never happens in practice) or everytime the
   * application is installed or the system is updated.
   */
    private void registerToServer( String phone, String regid ){

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
                    String regid = InstanceID.getInstance( App.getAppContext() ).getToken( authorizedEntity, scope );

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
                    prefs.edit().putString( getString( R.string.pref_regid ), regid ).apply();

                    msg = "Device registered to GCM server, registration ID=" + regid;
                    Log.i( "GCM", msg );

                    registerToServer( prefs.getString( getString( R.string.pref_phone ), null ), regid );
                }catch( IOException ex ){
                    Log.d( getPackageName(), "Error :" + ex.getMessage() );

                }
                return null;
            }

        }.execute();
    }


    /* save a message and return its id, or null if an error occurs. */
    private Long saveMessage( Bundle data ){
        // add message to db
        String to = data.getString( TO_KEY );
        long[] pattern = AppUtils.getPatternFromString( data.getString( PATTERN_KEY ) );
        String text = data.getString( MESSAGE_KEY );

        if( to == null || pattern == null ) return null;

        Message message = Message.createSentInstance( to, pattern, text );
        Context context = App.getAppContext();

        try( SqlDataSource src = new SqlDataSource( context, true ) ){
            src.addMessage( message );
            return message.getId();

        }catch( SQLException e ){
            Log.d( context.getPackageName(), "error adding message " + e );
        }

        return null;
    }


    /* add phone and regid to the message's data */
    private Bundle addRequiredInfos( Bundle data ){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
        data.putString( PHONE_KEY, prefs.getString( getString( R.string.pref_phone ), null ) );
        data.putString( REGID_KEY, prefs.getString( getString( R.string.pref_regid ), null ) );
        return data;
    }


    /*
     * Send data to the server. Will add the regid + phone to the data and send
     * it as is.
     */
    private void sendData( Bundle data ){

        GoogleCloudMessaging.getInstance( getApplicationContext() );
        data = addRequiredInfos( data );

        try{
            String id = App.getMessageId();

            GoogleCloudMessaging.getInstance( getApplicationContext() ) //
                    .send( PROJECT_ID + "@gcm.googleapis.com", id, data );

        }catch( IOException e ){
            Log.e( "GCM", "IOException while sending registration id", e );
        }
    }


    /* notify a message has been sent. */
    private void notify( Bundle data ){
        Intent i = new Intent( GCM_SERVICE_INTENT_FILTER );
        i.putExtra( EXTRA_EVT_TYPE, ACTION_MESSAGE_SENT );
        i.putExtras( data );
        LocalBroadcastManager.getInstance( getApplicationContext() ).sendBroadcast( i );
    }


}
