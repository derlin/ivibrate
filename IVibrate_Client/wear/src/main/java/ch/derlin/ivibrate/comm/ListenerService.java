package ch.derlin.ivibrate.comm;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import ch.derlin.ivibrate.OpenOnPhoneActivity;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.main.MainActivity;
import ch.derlin.ivibrate.utils.Friend;
import com.google.android.gms.wearable.*;

import java.util.ArrayList;

import static ch.derlin.ivibrate.comm.WearableConstants.*;

/**
 * Listener service to communicate with the handheld device.
 * The communication is made through data events on the data layer.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class ListenerService extends WearableListenerService{

    private static final int INDEX_IN_PATTERN_TO_REPEAT = -1; // -1: don't repeat
    // needed to avoid launching the activity if the user dismissed it while
    // waiting for the list of contacts
    private static boolean mShouldMainActivityBeCalled;


    @Override
    public void onDataChanged( DataEventBuffer dataEvents ){

        DataMap dataMap;

        for( DataEvent event : dataEvents ){

            // Check the data type
            if( event.getType() == DataEvent.TYPE_CHANGED ){
                // Check the data path
                dataMap = DataMapItem.fromDataItem( event.getDataItem() ).getDataMap();
                String path = event.getDataItem().getUri().getPath();

                if( path.equals( WearableConstants.PHONE_TO_WEARABLE_DATA_PATH ) ){
                    // this is for us

                    String action = dataMap.getString( ACTION_KEY );

                    if( action != null ){
                        doAction( action, dataMap );

                    }else{
                        SendToPhoneService.sendStatus( false );
                        Log.d( "wearable", "wearable - no action specified - event type " + event.getType() );
                    }

                    Log.v( "wearable", "DataMap received on watch: " + dataMap );
                }
            }
        }
    }

    // ----------------------------------------------------


    private void doAction( String action, DataMap dataMap ){

        switch( action ){
            case ACTION_PLAY_PATTERN:
                playPattern( dataMap.getLongArray( EXTRA_PATTERN ), //
                        dataMap.getString( EXTRA_PHONE ),   //
                        dataMap.getString( EXTRA_CONTACT_NAME )   //
                );
                break;

            case ACTION_GET_CONTACTS:
                // a query of the list of contacts
                if( mShouldMainActivityBeCalled ){
                    mShouldMainActivityBeCalled = false;
                    handleContactsList( dataMap );
                } // else, main activity was dismissed by the user => do nothing
                break;

            default:
                break;
        }

    }


    private void handleContactsList( DataMap dataMap ){

        // construct the list of contacts
        ArrayList<DataMap> maps = dataMap.getDataMapArrayList( "contacts" );
        ArrayList<Friend> contacts = new ArrayList<>();
        for( DataMap map : maps ){
            contacts.add( new Friend( map ) );
        }//end for

        // pass the list to the main activity
        Bundle bundle = new Bundle();
        bundle.putSerializable( EXTRA_CONTACTS_LIST, contacts );
        Intent intent = new Intent( this, MainActivity.class );
        intent.putExtras( bundle );
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity( intent );
    }


    private void playPattern( long[] pattern, String phone, String name ){
        if( pattern == null ) return;

        // play the pattern
        Vibrator vibrator = ( Vibrator ) getSystemService( VIBRATOR_SERVICE );
        vibrator.vibrate( pattern, INDEX_IN_PATTERN_TO_REPEAT );
        Log.d( "wearable", "pattern received and played" );

        SendToPhoneService.sendStatus( true );

        if( phone == null ) return; // do nothing if it is a replay

        // it was a new message: show a notification with reply and
        // open on phone actions

        // Create the reply action
        Intent replyActionIntent = new Intent( this, MainActivity.class );
        replyActionIntent.putExtra( "phone", phone );
        PendingIntent replyActionPendingIntent = PendingIntent.getActivity( this, 0, replyActionIntent, PendingIntent
                .FLAG_UPDATE_CURRENT );
        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder( R.drawable.reply_action,
                "Reply", replyActionPendingIntent ).build();

        // Create the open on phone intent
        Intent openActionIntent = new Intent( this, OpenOnPhoneActivity.class );
        openActionIntent.putExtra( "phone", phone );
        PendingIntent openActionPendingIntent = PendingIntent.getActivity( this, 0, openActionIntent, PendingIntent
                .FLAG_UPDATE_CURRENT );
        NotificationCompat.Action openOnPhoneAction = new NotificationCompat.Action.Builder( R.drawable.open_action,
                "Open on phone", openActionPendingIntent ).build();


        // Build the notification and add the action via WearableExtender
        Bitmap bg = BitmapFactory.decodeResource( getApplicationContext().getResources(), R.drawable.background );
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender();
        wearableExtender.addAction( replyAction );
        wearableExtender.addAction( openOnPhoneAction );
        wearableExtender.setBackground( bg );


        Notification notification = new NotificationCompat.Builder( getApplicationContext() ) //
                .setSmallIcon( R.mipmap.ic_launcher ) //
                .setContentTitle( "IVibrate" )   //
                .setContentText( "New message from " + name )  //
                .setAutoCancel( true )   //
                .extend( wearableExtender )  //
                .build();

        // show the notification
        NotificationManagerCompat.from( getApplicationContext() ).notify( Integer.parseInt( phone ), notification );
    }

    // ----------------------------------------------------


    /**
     * This method should be called with a parameter set to true when the main activity
     * asks for contacts. If the latter is dismissed by the user, it should set the
     * parameter to false before finishing. This way, we don't wake up the main activity
     * after it has been stopped.
     *
     * @param b a boolean.
     */
    public static void isWaitingForContact( boolean b ){
        mShouldMainActivityBeCalled = b;
    }
}
