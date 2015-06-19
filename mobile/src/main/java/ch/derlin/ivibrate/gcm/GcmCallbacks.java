package ch.derlin.ivibrate.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import static ch.derlin.ivibrate.gcm.GcmConstants.*;

/**
 * Created by lucy on 17/06/15.
 */
public class GcmCallbacks extends BroadcastReceiver{
    private static final IntentFilter INTENT_FILTER = new IntentFilter( GcmIntentService.GCM_SERVICE_INTENT_FILTER );


    public void onNackReceived(){}


    public void onAckReceived(){}


    public void onAccountsReceived( String[] accounts ){}


    public void onMessageReceived( String from, String message ){}


    public void onEchoReceived( String message ){}

    public void onNewRegistration(String account){}

    public void onUnregistration(String account){}


    // ----------------------------------------------------


    public void registerSelf( Context context ){
        LocalBroadcastManager.getInstance( context ).registerReceiver( this, INTENT_FILTER );
    }


    public void unregisterSelf( Context context ){
        LocalBroadcastManager.getInstance( context ).unregisterReceiver( this );
    }

    // ----------------------------------------------------


    @Override
    public void onReceive( Context context, Intent intent ){

        switch( intent.getStringExtra( EXTRA_EVT_TYPE ) ){
            case MESG_TYPE_ACK:
                onAckReceived();
                break;

            case MESG_TYPE_NACK:
                onNackReceived();
                break;

            case ACTION_GET_ACCOUNTS:
                String string = intent.getStringExtra( ACCOUNTS_KEY );
                onAccountsReceived( string.split( "," ) );
                break;

            case ACTION_ECHO:
                onEchoReceived( intent.getStringExtra( MESSAGE_KEY ) );
                break;

            case ACTION_MESSAGE:
                onMessageReceived( intent.getStringExtra( FROM_KEY ), intent.getStringExtra( MESSAGE_KEY ) );
                break;

            case ACTION_REGISTER:
                onNewRegistration( intent.getStringExtra( ACCOUNTS_KEY ) );
                break;

            case ACTION_UNREGISTER:
                onUnregistration( intent.getStringExtra( ACCOUNTS_KEY ) );
                break;

            default:
                break;
        }
    }


}
