package ch.derlin.ivibrate.comm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.util.Log;
import ch.derlin.ivibrate.app.App;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.*;

import java.util.Date;

import static ch.derlin.ivibrate.comm.WearableConstants.WEARABLE_TO_PHONE_DATA_PATH;

/**
 * A singleton service to send data to the handheld device
 * through the data layer.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class SendToPhoneService extends IntentService {

    public SendToPhoneService(){
        super("SendToPhoneService");
    }

    /**
     * Send a confirmation to the phone, for example
     * to notify a pattern was successfully played.
     *
     * @param success the status.
     */
    public static void sendStatus( boolean success ){
        Bundle bundle = new Bundle();
        bundle.putBoolean( "result", success );
        wakeUpService( bundle );
    }


    /**
     * Ask the phone to send the list of existing
     * contacts.
     */
    public static void askForContacts(){
        Bundle bundle = new Bundle();
        bundle.putString( "action", "getContacts" );
        wakeUpService( bundle );
    }


    /**
     * Ask the phone to send a message.
     *
     * @param phone   the target receiver.
     * @param pattern the pattern.
     * @param text    an optional text.
     */
    public static void send( String phone, long[] pattern, String text ){
        Bundle bundle = new Bundle();
        bundle.putString( "action", "send" );
        bundle.putString( "phone", phone );
        bundle.putLongArray( "pattern", pattern );
        bundle.putString( "text", text );

        bundle.putString( "toast", "IVibrate, vibe sent"  );

        wakeUpService( bundle);

    }


    /**
     * Ask the phone to open the app.
     *
     * @param phone an optional phone to show a
     *              given conversation.
     */
    public static void askOpenApp( String phone ){
        Bundle bundle = new Bundle();
        bundle.putString( "action", "open" );
        bundle.putString( "phone", phone );
        wakeUpService( bundle );
    }

    // ----------------------------------------------------

    private static void wakeUpService( Bundle data ){
        Intent intent = new Intent( App.getAppContext(), SendToPhoneService.class );
        intent.putExtras( data );
        App.getAppContext().startService( intent );
    }

    /* *****************************************************************
     * intent handling and private utils
     * ****************************************************************/


    @Override
    protected void onHandleIntent( Intent intent ){
        Bundle extras = intent.getExtras();
        broadcastDatamapToPhoneNodes( extras, extras.getString( "toast", null ) );
    }


    /* Send data to all connected nodes.
     * If toast is not null, an animation will be played upon success. */
    private void broadcastDatamapToPhoneNodes( Bundle bundle, final String toast ){
        DataMap dataMap = DataMap.fromBundle( bundle );
        dataMap.putLong( "time", new Date().getTime() );

        // connect to google API
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder( getApplicationContext() ) //
                .addApi( Wearable.API ) //
                .build();
        googleApiClient.connect();

        // Construct a DataRequest and send over the data layer
        PutDataMapRequest putDMR = PutDataMapRequest.create( WEARABLE_TO_PHONE_DATA_PATH );
        putDMR.getDataMap().putAll( dataMap );
        PutDataRequest request = putDMR.asPutDataRequest();
        DataApi.DataItemResult result = Wearable.DataApi.putDataItem( googleApiClient, request ).await();

        Status status = result.getStatus();

        if( toast != null ){
            showAnimation( status.isSuccess(), status.isSuccess() ? "Message sent." : "Error sending " + "message..." );

        }

        Log.i( getPackageName(), "Data sent to phone. Status => " + status );

    }

    // ----------------------------------------------------

    /* Show a success or fail animation */
    private void showAnimation( boolean success, String message ){
        Intent intent = new Intent( this, ConfirmationActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        intent.putExtra( ConfirmationActivity.EXTRA_ANIMATION_TYPE, success ? ConfirmationActivity.SUCCESS_ANIMATION
                : ConfirmationActivity.FAILURE_ANIMATION );
        intent.putExtra( ConfirmationActivity.EXTRA_MESSAGE, message );
        startActivity( intent );
    }



}
