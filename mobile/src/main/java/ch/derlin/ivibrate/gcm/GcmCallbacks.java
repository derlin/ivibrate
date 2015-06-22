package ch.derlin.ivibrate.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import ch.derlin.ivibrate.app.AppUtils;
import ch.derlin.ivibrate.sql.entities.Message;

import static ch.derlin.ivibrate.gcm.GcmConstants.*;

/**
 * Created by lucy on 17/06/15.
 */
public class GcmCallbacks extends BroadcastReceiver{

    private static final IntentFilter INTENT_FILTER = new IntentFilter( GCM_SERVICE_INTENT_FILTER );


    public void onNackReceived(){}


    public void onAckReceived(){}


    public void onAccountsReceived( String[] accounts ){}


    public void onMessageReceived( String from, Message message ){}

    public void onMessageSent( String to, Message message ){ }

    public void onEchoReceived( String message ){}


    public void onNewRegistration( String account ){}


    public void onUnregistration( String account ){}


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

            case ACTION_MESSAGE_RECEIVED:
                String from = intent.getStringExtra( FROM_KEY );
                long[] pattern = AppUtils.getPatternFromString( intent.getStringExtra( MESSAGE_KEY ) );
                if(from != null && pattern != null){
                    Message m = Message.createReceivedInstance(from, pattern);
                    onMessageReceived(from, m);
                }
                break;

            case ACTION_MESSAGE_SENT:
                String to = intent.getStringExtra( TO_KEY );
                long[] p = AppUtils.getPatternFromString( intent.getStringExtra( MESSAGE_KEY ) );
                if(to != null && p != null){
                    Message m = Message.createSentInstance( to, p );
                    onMessageSent( to, m );
                }
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
