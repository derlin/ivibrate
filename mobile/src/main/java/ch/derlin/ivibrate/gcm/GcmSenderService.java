package ch.derlin.ivibrate.gcm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
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

public class GcmSenderService extends Service{

    GoogleCloudMessaging gcm;
    private static GcmSenderService INSTANCE;
    private String regid;
    private Gson gson = new GsonBuilder().create();
    private static Random random = new Random();
    private Set<Integer> msgIdSet = new HashSet<>();
    private LocalBroadcastManager mBroadcastManager;

    // ----------------------------------------------------
    public class GcmSenderBinder extends Binder{
        GcmSenderService getService(){
            // Return this instance of LocalService so clients can call public methods
            return GcmSenderService.this;
        }
    }

    private final IBinder mBinder = new GcmSenderBinder();

    // ----------------------------------------------------


    public static GcmSenderService getInstance(){
        return INSTANCE;
    }


    public GcmSenderService(){
    }


    @Override
    public void onCreate(){
        super.onCreate();
        gcm = GoogleCloudMessaging.getInstance( getApplicationContext() );
        regid = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() ) //
                .getString( getString( R.string.pref_regid ), null );
        if( regid == null ) loadRegIdAsync();
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


    @Override
    public IBinder onBind( Intent intent ){
        return mBinder;
    }

    // ----------------------------------------------------


    public String getRegId(){
        return regid;
    }


    public void registerToServer( String phone ){
        Bundle data = new Bundle();
        data.putString( ACTION_KEY, GcmConstants.ACTION_REGISTER );
        data.putString( MESSAGE_KEY, phone );
        sendData( data );
    }


    public void unregisterFromServer(){
        String phone = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() ) //
                .getString( getString( R.string.pref_phone ), null );

        if( phone == null ) return;

        Bundle data = new Bundle();
        data.putString( ACTION_KEY, ACTION_REGISTER );
        data.putString( MESSAGE_KEY, phone );
        sendData( data );
    }


    public void askForAccounts(){
        Bundle data = new Bundle();
        data.putString( ACTION_KEY, ACTION_GET_ACCOUNTS );
        sendData( data );
    }


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

    public void sendMessage(String to, long[] pattern){
        sendMessage( to, pattern, null );
    }


    public void sendEcho( long[] pattern ){
        sendEcho( gson.toJson( pattern ) );
    }


    public void sendEcho( String message ){
        Bundle data = new Bundle();
        data.putString( ACTION_KEY, GcmConstants.ACTION_ECHO );
        data.putString( MESSAGE_KEY, message );
        sendData( data );
    }


    public void sendData( Bundle data){
        data.putString( REGID_KEY, regid ); // always put regid
        try{
            String id = getMessageId();
            gcm.send( PROJECT_ID + "@gcm.googleapis.com", id, data );
        }catch( IOException e ){
            Log.e( "GCM", "IOException while sending registration id", e );
        }
    }

    // ----------------------------------------------------

    private void notify(Bundle data){
        Intent i = new Intent( GCM_SERVICE_INTENT_FILTER );
        i.putExtra( EXTRA_EVT_TYPE, ACTION_MESSAGE_SENT );
        i.putExtras( data );
        mBroadcastManager.sendBroadcast( i );
    }


    private void saveMessage(Message message){
        // add message to db
        Context context = App.getAppContext();
        try( SqlDataSource src = new SqlDataSource( context, true ) ){
            src.addMessage( message );
        }catch( SQLException e ){
            Log.d( context.getPackageName(), "error adding message " + e );
        }
    }


    private String getMessageId(){
        int id;
        do{
            id = random.nextInt();
        }while( msgIdSet.contains( id ) );

        msgIdSet.add( id );
        return Integer.toString( id );
    }


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
