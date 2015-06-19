package ch.derlin.ivibrate.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import static ch.derlin.ivibrate.gcm.GcmConstants.*;

public class GcmIntentService extends IntentService{

    public static final String GCM_SERVICE_INTENT_FILTER = "GCM_SERVICE";
    String mes;
    private Handler handler;
    private LocalBroadcastManager mBroadcastManager;


    public GcmIntentService(){
        super( "GcmMessageHandler" );
    }


    @Override
    public void onCreate(){
        super.onCreate();
        mBroadcastManager = LocalBroadcastManager.getInstance( this );
        handler = new Handler();
    }


    @Override
    protected void onHandleIntent( Intent intent ){
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance( this );
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType( intent );

        if( messageType.equals( MESG_TYPE_ACK ) ){
            mBroadcastManager.sendBroadcast( getIntent( MESG_TYPE_ACK ) );

        }else if( messageType.equals( MESG_TYPE_NACK ) ){
            mBroadcastManager.sendBroadcast( getIntent( MESG_TYPE_NACK ) );

        }else{
            Log.d( getPackageName(), "NEW MESSGAGE: " + messageType + " " + extras.toString() );
            // this is a real message
            String action = extras.getString( MESG_TYPE_KEY );
            if(action == null) return; // TODO
            mBroadcastManager.sendBroadcast( getIntent( action, extras ) );
        }

        Log.i( "GCM", "Received : (" + messageType + ")  " + extras.toString() );

        GcmListener.completeWakefulIntent( intent );

    }


    protected Intent getIntent( String evtType ){
        Intent i = new Intent( GCM_SERVICE_INTENT_FILTER );
        i.putExtra( EXTRA_EVT_TYPE, evtType );
        return i;
    }


    protected Intent getIntent( String evtType, Bundle bundle ){
        Intent i = getIntent( evtType );
        i.putExtras( bundle );
        return i;
    }

    public void showToast(){
        handler.post( new Runnable(){
            public void run(){
                Toast.makeText( getApplicationContext(), mes, Toast.LENGTH_LONG ).show();
            }
        } );

    }


}
