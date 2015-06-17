package ch.derlin.ivibrate;

import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.*;

/**
 * Created by lucy on 16/06/15.
 */

public class SendToWearableThread extends Thread{

    String path;
    DataMap dataMap;
    GoogleApiClient googleClient;
    ActionCallbacks callbacks;


    // Constructor for sending data objects to the data layer
    SendToWearableThread( GoogleApiClient client, String p, DataMap data ){
        googleClient = client;
        path = p;
        dataMap = data;
    }


    public SendToWearableThread( GoogleApiClient googleClient, String path, DataMap dataMap, ActionCallbacks
            callbacks ){
        this.path = path;
        this.dataMap = dataMap;
        this.googleClient = googleClient;
        this.callbacks = callbacks;
    }


    public void run(){
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( googleClient ).await();
        for( Node node : nodes.getNodes() ){

            // Construct a DataRequest and send over the data layer
            PutDataMapRequest putDMR = PutDataMapRequest.create( path );
            putDMR.getDataMap().putAll( dataMap );
            PutDataRequest request = putDMR.asPutDataRequest();
            DataApi.DataItemResult result = Wearable.DataApi.putDataItem( googleClient, request ).await();
            if( result.getStatus().isSuccess() ){
                if( callbacks != null ) callbacks.onSuccess( node.getDisplayName() );
                Log.v( "myTag", "DataMap: " + dataMap + " sent to: " + node.getDisplayName() );
            }else{
                if( callbacks != null ) callbacks.onFail( node.getDisplayName() + ": error sending pattern." );
                // Log an error
                Log.v( "myTag", "ERROR: failed to send DataMap" );
            }
        }
    }


}
