package ch.derlin.ivibrate.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.app.App;
import ch.derlin.ivibrate.main.MainActivity;
import ch.derlin.ivibrate.sql.SqlDataSource;
import ch.derlin.ivibrate.sql.entities.Friend;
import ch.derlin.ivibrate.sql.entities.LocalContactDetails;
import ch.derlin.ivibrate.sql.entities.Message;
import ch.derlin.ivibrate.sql.LocalContactsManager;
import ch.derlin.ivibrate.wear.SendToWearableService;

import java.sql.SQLException;

/**
 * The listener which will be called upon EVERY
 * GCM message.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class GcmReceiverServiceCallbacks extends GcmCallbacks{

    Context context = App.getAppContext();
    NotificationManager nManager = ( NotificationManager ) context.getSystemService( Context.NOTIFICATION_SERVICE );


    @Override
    public void onMessageReceived( String from, Message message ){
        // send pattern to the watch
        Friend f = new Friend( from );
        SendToWearableService.getInstance().sendPattern( message.getPatternObject(), f );

        // add message to db
        // add message to db
        try( SqlDataSource src = new SqlDataSource( context, true ) ){
            if(!src.friendExists( f.getPhone() )){
                src.addFriend( f );
            }
            src.addMessage( message );
        }catch( SQLException e ){
            Log.d( context.getPackageName(), "error adding message " + e );
        }

        // notify new message
        LocalContactDetails details = LocalContactsManager.getContactDetails( from );
        String m = String.format( "New message from %s", details == null ? from : details.getName() );
        notify( from, "IVibrate", m );

    }


    @Override
    public void onAckReceived( String from, Long id ){
        try( SqlDataSource src = new SqlDataSource( App.getAppContext(), true ) ){
            if( src.setMessageAcked( id ) ){
                Log.i( "ACK", "ACK => id " + id + " from " + from );
            }
        }catch( Exception e ){
            Log.d( "ACK", "could not retrieve acked message from db" );
        }
    }


    @Override
    public void onUnregistration( String account ){
        // TODO update db
    }

    // ----------------------------------------------------


    private void notify( String from, String notificationTitle, String notificationMessage ){
        // build a notification for phone only
        NotificationCompat.Builder builder = new NotificationCompat.Builder( context )  //
                .setAutoCancel( true ) //
                .setSmallIcon( R.mipmap.ic_launcher )  //
                .setContentTitle( notificationTitle )  //
                .setContentText( notificationMessage ) //
                .setLocalOnly( true ); // don't show it in the watch

        Intent targetIntent = new Intent( context, MainActivity.class );
        targetIntent.putExtra( GcmConstants.FROM_KEY, from );
        PendingIntent contentIntent = PendingIntent.getActivity( context, 0, targetIntent, PendingIntent
                .FLAG_UPDATE_CURRENT );
        builder.setContentIntent( contentIntent );

        nManager.notify( Integer.parseInt( from ), builder.build() );
    }

}
