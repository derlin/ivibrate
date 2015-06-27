package ch.derlin.ivibrate.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.app.App;
import ch.derlin.ivibrate.app.AppUtils;
import ch.derlin.ivibrate.sql.entities.Message;

import static ch.derlin.ivibrate.gcm.GcmConstants.*;

/**
 * This class is meant to be overriden to ease the process of
 * responding to GCM events.
 * <p/>
 * Each broadcasted event by the GCM service has a
 * method associated.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class GcmCallbacks extends BroadcastReceiver{

    private static final IntentFilter INTENT_FILTER = new IntentFilter( GCM_SERVICE_INTENT_FILTER );


    public void onNackReceived(){}


    public void onAckReceived( String from, Long messageId ){}


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

            case ACTION_NACK:
                String error = intent.getStringExtra( ERROR_KEY);

                if(TO_KEY.equals( error )){
                    // the friend's regids are not up to date...
                    onNackReceived();

                }else if(FROM_KEY.equals( error )){
                    // error: the registration was unsuccessful. Try again
                    String phone = PreferenceManager.getDefaultSharedPreferences( context ) //
                            .getString( context.getString( R.string.pref_phone ), null );

                    if( phone != null ) GcmSenderService.getInstance().registerToServer( phone );
                    Toast.makeText(App.getAppContext(), "registration failed. Trying again...", Toast.LENGTH_SHORT).show();
                }

                break;

            case ACTION_ACK:
                try{
                    Long mesgId = Long.parseLong( intent.getStringExtra( MESSAGE_ID_KEY ) );
                    if( mesgId != null ) onAckReceived( intent.getStringExtra( FROM_KEY ), mesgId );
                }catch( NumberFormatException e ){
                    Log.d( App.TAG, "GCMCallbacks - Could not parse message id for action ack." );
                }
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
                long[] pattern = AppUtils.getPatternFromString( intent.getStringExtra( PATTERN_KEY ) );
                String text = intent.getStringExtra( MESSAGE_KEY );
                if( from != null && pattern != null ){
                    Message m = Message.createReceivedInstance( from, pattern, text );
                    onMessageReceived( from, m );
                }
                break;

            case ACTION_MESSAGE_SENT:
                String to = intent.getStringExtra( TO_KEY );
                long[] p = AppUtils.getPatternFromString( intent.getStringExtra( PATTERN_KEY ) );
                String t = intent.getStringExtra( MESSAGE_KEY );
                Long id = Long.parseLong( intent.getStringExtra( MESSAGE_ID_KEY ) );
                if( to != null && p != null ){
                    Message m = Message.createSentInstance( to, p, t );
                    m.setId( id );
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
