package ch.derlin.ivibrate.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import ch.derlin.ivibrate.app.App;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import static ch.derlin.ivibrate.gcm.GcmConstants.*;

/**
 * The service in charge of dealing with GCM message.
 * Will determine if the message should be treated
 * and broadcast a message caught by all the registered
 * GCMCallbacks.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class GcmIntentService extends IntentService{


    private LocalBroadcastManager mBroadcastManager;
    private GcmCallbacks mCallbacks = new IntentServiceCallbacks();


    public GcmIntentService(){
        super( "GcmMessageHandler" );
    }


    @Override
    public void onCreate(){
        super.onCreate();
        mCallbacks.registerSelf( App.getAppContext() );
        mBroadcastManager = LocalBroadcastManager.getInstance( this );
    }


    @Override
    public void onDestroy(){
        mCallbacks.unregisterSelf( App.getAppContext() );
        super.onDestroy();
    }


    @Override
    protected void onHandleIntent( Intent intent ){
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance( this );
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType( intent );
        Log.d( getPackageName(), "NEW MESSGAGE: " + messageType + " " + extras.toString() );

        // the action is the type of message (message, registration, ack...)
        String action = extras.getString( MESG_TYPE_KEY );
        if( action == null || !action.matches( PACKAGE + ".*" ) ) return; // TODO

        if( ACTION_MESSAGE_RECEIVED.equals( action ) ){
            // send ack
            Bundle data = new Bundle();
            data.putString( ACTION_KEY, ACTION_ACK );
            data.putString( MESSAGE_ID_KEY, extras.getString( MESSAGE_ID_KEY ) );
            data.putString( TO_KEY, extras.getString( FROM_KEY ) );
            GcmSenderService.getInstance().sendData( data );
        }

        mBroadcastManager.sendBroadcast( getIntent( action, extras ) );


        Log.i( "GCM", "Received : (" + messageType + ")  " + extras.toString() );

        GcmListener.completeWakefulIntent( intent );

    }


    protected Intent getIntent( String evtType ){
        Intent i = new Intent( GCM_SERVICE_INTENT_FILTER );
        i.putExtra( EXTRA_EVT_TYPE, evtType );
        //        mCallbacks.onReceive( App.getAppContext(), i );
        return i;
    }


    protected Intent getIntent( String evtType, Bundle bundle ){
        Intent i = getIntent( evtType );
        i.putExtras( bundle );
        //        mCallbacks.onReceive( App.getAppContext(), i );
        return i;
    }

}



