package ch.derlin.ivibrate;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.*;

import java.util.Date;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SendToWearableService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public static final String WEARABLE_DATA_PATH = "/derlin/ivibrate/";
    public static final String SWSERVICE_INTENT_FILTER = "SendToWearableService";

    public static final String EXTRA_EVT_TYPE = "evt_type", FAIL_EVT_TYPE = "fail", SUCCESS_EVT_TYPE = "success",
            EXTRA_STRING = "msg";

    private static SendToWearableService INSTANCE;
    private LocalBroadcastManager mBroadcastManager;

    // ----------------------------------------------------

    public class SendToWearableBinder extends Binder{
        SendToWearableService getService(){
            // Return this instance of LocalService so clients can call public methods
            return SendToWearableService.this;
        }
    }

    private final IBinder mBinder = new SendToWearableBinder();

    // ----------------------------------------------------

    private GoogleApiClient mGoogleClient = null;


    public static SendToWearableService getInstance(){
        return INSTANCE;
    }


    @Override
    public void onCreate(){
        super.onCreate();
        mGoogleClient = getGoogleClient();
        mBroadcastManager = LocalBroadcastManager.getInstance( this );
        INSTANCE = this;
    }


    @Override
    public void onDestroy(){
        INSTANCE = null;
        if( mGoogleClient != null ){
            mGoogleClient.disconnect();
            mGoogleClient = null;
        }
        super.onDestroy();
    }


    @Override
    public IBinder onBind( Intent intent ){
        return mBinder;
    }


    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    public void sendPattern( long[] pattern ){
        DataMap dataMap = new DataMap();
        dataMap.putLongArray( "pattern", pattern );
        dataMap.putLong( "time", new Date().getTime() );
        broadcastDatamapToWearableNodes( dataMap );
    }


    private void broadcastDatamapToWearableNodes( final DataMap dataMap ){
        // we need a thread to avoid exceptions calling await
        new Thread(){

            @Override
            public void run(){

                List<Node> nodes = Wearable.NodeApi.getConnectedNodes( mGoogleClient ).await().getNodes();

                // check that at least one wearable is connected
//                if(nodes.size() == 0){
//                    mBroadcastManager.sendBroadcast( getIntent( FAIL_EVT_TYPE, "No wearable connected" ) );
//                    // Log an error
//                    Log.v( "myTag", "ERROR: failed to send DataMap" );
//                    return;
//                }

                // send datamap
                for( Node node : nodes ){

                    // Construct a DataRequest and send over the data layer
                    PutDataMapRequest putDMR = PutDataMapRequest.create( WEARABLE_DATA_PATH );
                    putDMR.getDataMap().putAll( dataMap );
                    PutDataRequest request = putDMR.asPutDataRequest();
                    DataApi.DataItemResult result = Wearable.DataApi.putDataItem( mGoogleClient, request ).await();
                    if( result.getStatus().isSuccess() ){
                        Log.v( "myTag", "DataMap: " + dataMap + " sent to: " + node.getDisplayName() );
                        mBroadcastManager.sendBroadcast( getIntent( SUCCESS_EVT_TYPE, node.getDisplayName() ) );

                    }else{
                        mBroadcastManager.sendBroadcast( getIntent( FAIL_EVT_TYPE, "Failed to send pattern to " +
                                node.getDisplayName() ) );
                        // Log an error
                        Log.v( "myTag", "ERROR: failed to send DataMap" );
                    }
                }
            }
        }.start();
    }


    public GoogleApiClient getGoogleClient(){

        if( mGoogleClient == null ){
            mGoogleClient = new GoogleApiClient.Builder( this ) //
                    .addApi( Wearable.API )  //
                    .addConnectionCallbacks( this ) //
                    .addOnConnectionFailedListener( this ) //
                    .build();
            mGoogleClient.connect();
        }
        return mGoogleClient;
    }


    public boolean isConnected(){
        return mGoogleClient.isConnected();
    }


    protected Intent getIntent( String evtType ){
        Intent i = new Intent( SWSERVICE_INTENT_FILTER );
        i.putExtra( EXTRA_EVT_TYPE, evtType );
        return i;
    }


    protected Intent getIntent( String evtType, String msg ){
        Intent i = getIntent( evtType );
        i.putExtra( EXTRA_STRING, msg );
        return i;
    }

    // ----------------------------------------------------
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d( getPackageName(), "connected" );
    }

    @Override
    public void onConnectionSuspended(int cause){
        Log.d( getPackageName(), "suspended" );
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d( getPackageName(), "failed" );
    }


}
