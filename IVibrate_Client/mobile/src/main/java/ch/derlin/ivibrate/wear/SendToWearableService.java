package ch.derlin.ivibrate.wear;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import ch.derlin.ivibrate.app.App;
import ch.derlin.ivibrate.sql.entities.Friend;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ch.derlin.ivibrate.wear.WearableConstants.*;

/**
 * An Intent Service used to send data/message to all
 * connected devices. Static methods are used to send
 * the correct intent to the service.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class SendToWearableService extends IntentService{

    public SendToWearableService(){
        super( "SendToWearableService" );
    }

    /* *****************************************************************
     * static methods to start the service
     * ****************************************************************/

    public static void sendPattern( long[] pattern ){
        Bundle bundle = new Bundle();
        bundle.putLongArray( EXTRA_PATTERN, pattern );
        wakeUpService( ACTION_PLAY_PATTERN, bundle );
    }


    public static void sendPattern( long[] pattern, Friend from ){
        Bundle bundle = new Bundle();
        bundle.putLongArray( EXTRA_PATTERN, pattern );
        bundle.putString( EXTRA_PHONE, from.getPhone() );
        bundle.putString( EXTRA_CONTACT_NAME, from.getDisplayName() );
        wakeUpService( ACTION_PLAY_PATTERN, bundle );
    }


    public static void sendContacts( ArrayList<Bundle> details ){
        Bundle dataMap = new Bundle();
        dataMap.putParcelableArrayList( EXTRA_CONTACTS_LIST, details );
        wakeUpService( ACTION_GET_CONTACTS, dataMap );
    }

    // ----------------------------------------------------

    private static void wakeUpService( String action, Bundle data ){
        data.putString( ACTION_KEY, action );
        Intent intent = new Intent( App.getAppContext(), SendToWearableService.class );
        intent.putExtras( data );
        App.getAppContext().startService( intent );
    }

    /* *****************************************************************
     * intent handling and private utils
     * ****************************************************************/


    @Override
    protected void onHandleIntent( Intent intent ){
        broadcastDatamapToWearableNodes( intent.getExtras() );
    }


    // ----------------------------------------------------

    private void broadcastDatamapToWearableNodes( Bundle bundle ){
        DataMap dataMap = DataMap.fromBundle( bundle );
        dataMap.putLong( "time", new Date().getTime() );

        // connect to google API
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder( getApplicationContext() ) //
                .addApi( Wearable.API ) //
                .build();
        googleApiClient.connect();

        // get all connected devices
        List<Node> nodes = Wearable.NodeApi.getConnectedNodes( googleApiClient ).await().getNodes();

        // send datamap
        for( Node node : nodes ){

            // Construct a DataRequest and send over the data layer
            PutDataMapRequest putDMR = PutDataMapRequest.create( PHONE_TO_WEARABLE_DATA_PATH );
            putDMR.getDataMap().putAll( dataMap );
            PutDataRequest request = putDMR.asPutDataRequest();
            DataApi.DataItemResult result = Wearable.DataApi.putDataItem( googleApiClient, request ).await();

            // result feedback
            if( result.getStatus().isSuccess() ){
                Log.v( getPackageName(), "DataMap: " + dataMap + " sent to: " + node.getDisplayName() );
                LocalBroadcastManager.getInstance( getApplicationContext() )  //
                        .sendBroadcast( getIntent( SUCCESS_EVT_TYPE, node.getDisplayName() ) );

            }else{
                LocalBroadcastManager.getInstance( getApplicationContext() )  //
                        .sendBroadcast( getIntent( FAIL_EVT_TYPE, "Failed to send pattern to " + node.getDisplayName
                                () ) );
                // Log an error
                Log.v( getPackageName(), "ERROR: failed to send DataMap" );
            }
        }
    }

    // ----------------------------------------------------

    /* intent to broadcast */
    protected Intent getIntent( String evtType ){
        Intent i = new Intent( SEND_TO_WEARABLE_SERVICE_INTENT_FILTER );
        i.putExtra( EXTRA_EVT_TYPE, evtType );
        return i;
    }


    /* intent to broadcast */
    protected Intent getIntent( String evtType, String msg ){
        Intent i = getIntent( evtType );
        i.putExtra( EXTRA_STRING, msg );
        return i;
    }

}
