package ch.derlin.ivibrate.wear;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import ch.derlin.ivibrate.sql.entities.Friend;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ch.derlin.ivibrate.wear.WearableConstants.*;

/**
 * A singleton service used to send data/message to all
 * connected devices. Is started with the application
 * (see {@link ch.derlin.ivibrate.app.App})
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class SendToWearableService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient
        .OnConnectionFailedListener{

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

    // ----------------------------------------------------


    @Override
    public IBinder onBind( Intent intent ){
        return mBinder;
    }


    public void sendPattern( long[] pattern ){
        DataMap dataMap = new DataMap();
        dataMap.putLongArray( "pattern", pattern );
        broadcastDatamapToWearableNodes( dataMap );
    }


    public void sendPattern( long[] pattern, Friend from ){
        DataMap dataMap = new DataMap();
        dataMap.putLongArray( "pattern", pattern );
        dataMap.putString( "phone", from.getPhone() );
        dataMap.putString( "name", from.getDisplayName() );
        broadcastDatamapToWearableNodes( dataMap );
    }


    public void sendContacts( ArrayList<DataMap> details ){
        DataMap dataMap = new DataMap();
        dataMap.putDataMapArrayList( "contacts", details );
        broadcastDatamapToWearableNodes( dataMap );
    }


    private void broadcastDatamapToWearableNodes( final DataMap dataMap ){
        dataMap.putLong( "time", new Date().getTime() );
        // we need a thread to avoid exceptions calling await
        new Thread(){

            @Override
            public void run(){

                List<Node> nodes = Wearable.NodeApi.getConnectedNodes( mGoogleClient ).await().getNodes();

                // send datamap
                for( Node node : nodes ){

                    // Construct a DataRequest and send over the data layer
                    PutDataMapRequest putDMR = PutDataMapRequest.create( PHONE_TO_WEARABLE_DATA_PATH );
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

    // ----------------------------------------------------


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

    // ----------------------------------------------------


    protected Intent getIntent( String evtType ){
        Intent i = new Intent( SEND_TO_WEARABLE_SERVICE_INTENT_FILTER );
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
    public void onConnected( Bundle connectionHint ){
        Log.d( getPackageName(), "connected" );
    }


    @Override
    public void onConnectionSuspended( int cause ){
        Log.d( getPackageName(), "suspended" );
    }


    @Override
    public void onConnectionFailed( ConnectionResult connectionResult ){
        Log.d( getPackageName(), "failed" );
    }


}
