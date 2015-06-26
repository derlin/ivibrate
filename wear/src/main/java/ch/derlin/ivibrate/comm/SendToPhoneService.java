package ch.derlin.ivibrate.comm;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.wearable.activity.ConfirmationActivity;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.*;

import java.util.Date;

import static ch.derlin.ivibrate.comm.WearableConstants.WEARABLE_TO_PHONE_DATA_PATH;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SendToPhoneService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient
        .OnConnectionFailedListener{

    private static SendToPhoneService INSTANCE;

    // ----------------------------------------------------

    public class SendToWearableBinder extends Binder{
        SendToPhoneService getService(){
            // Return this instance of LocalService so clients can call public methods
            return SendToPhoneService.this;
        }
    }

    private final IBinder mBinder = new SendToWearableBinder();

    // ----------------------------------------------------

    private GoogleApiClient mGoogleClient = null;


    public static SendToPhoneService getInstance(){
        return INSTANCE;
    }


    @Override
    public void onCreate(){
        super.onCreate();
        mGoogleClient = getGoogleClient();
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
    public void sendStatus( boolean success ){
        DataMap dataMap = new DataMap();
        dataMap.putBoolean( "result", success );
        broadcastDatamapToPhoneNodes( dataMap );
    }


    public void askForContacts(){
        DataMap dataMap = new DataMap();
        dataMap.putString( "action", "getContacts" );
        broadcastDatamapToPhoneNodes( dataMap );
    }


    public void send( String phone, long[] pattern, String text ){
        DataMap dataMap = new DataMap();
        dataMap.putString( "action", "send" );
        dataMap.putString( "phone", phone );
        dataMap.putLongArray( "pattern", pattern );
        dataMap.putString( "text", text );
        broadcastDatamapToPhoneNodes( dataMap, "IVibrate, vibe sent" );

    }


    private void broadcastDatamapToPhoneNodes( final DataMap dataMap, final String toast ){
        dataMap.putLong( "time", new Date().getTime() );
        // we need a thread to avoid exceptions calling await
        new AsyncTask<Void, Void, Status>(){

            @Override
            public com.google.android.gms.common.api.Status doInBackground( Void... params ){
                // Construct a DataRequest and send over the data layer
                PutDataMapRequest putDMR = PutDataMapRequest.create( WEARABLE_TO_PHONE_DATA_PATH );
                putDMR.getDataMap().putAll( dataMap );
                PutDataRequest request = putDMR.asPutDataRequest();
                DataApi.DataItemResult result = Wearable.DataApi.putDataItem( mGoogleClient, request ).await();

                return result.getStatus();
            }


            @Override
            protected void onPostExecute( com.google.android.gms.common.api.Status status ){
                if( toast != null ){
                    showAnimation( status.isSuccess(), status.isSuccess() ? "Message sent." : "Error sending " +
                            "message..." );
                    //                    Toast.makeText( getApplicationContext(), toast + ": "  //
                    //                                    + ( status.isSuccess() ? "success" : "error" ),//
                    //                            Toast.LENGTH_SHORT ).show();
                }
                Log.i( getPackageName(), "Data sent to phone. Status => " + status );
            }
        }.execute();
    }


    private void showAnimation( boolean success, String message ){
        Intent intent = new Intent( this, ConfirmationActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        intent.putExtra( ConfirmationActivity.EXTRA_ANIMATION_TYPE, success ? ConfirmationActivity.SUCCESS_ANIMATION
                : ConfirmationActivity.FAILURE_ANIMATION );
        intent.putExtra( ConfirmationActivity.EXTRA_MESSAGE, message );
        startActivity( intent );
    }


    private void broadcastDatamapToPhoneNodes( final DataMap dataMap ){
        broadcastDatamapToPhoneNodes( dataMap, null );
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
