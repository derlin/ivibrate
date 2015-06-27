package ch.derlin.ivibrate.comm;

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
 * A singleton service to send data to the handheld device
 * through the data layer.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class SendToPhoneService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient
        .OnConnectionFailedListener{

    private static SendToPhoneService INSTANCE;
    private GoogleApiClient mGoogleClient = null;

    // ----------------------------------------------------

    public class SendToWearableBinder extends Binder{
        SendToPhoneService getService(){
            // Return this instance of LocalService so clients can call public methods
            return SendToPhoneService.this;
        }
    }

    private final IBinder mBinder = new SendToWearableBinder();



    @Override
    public IBinder onBind( Intent intent ){
        return mBinder;
    }

    // ----------------------------------------------------



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

    // ----------------------------------------------------


    /**
     * Get the Google API client.
     * @return  the google API client.
     */
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


    /**
     * Check if the google API client is connected.
     * @return true or false.
     */
    public boolean isConnected(){
        return mGoogleClient.isConnected();
    }


    /**
     * Send a confirmation to the phone, for example
     * to notify a pattern was successfully played.
     * @param success  the status.
     */
    public void sendStatus( boolean success ){
        DataMap dataMap = new DataMap();
        dataMap.putBoolean( "result", success );
        broadcastDatamapToPhoneNodes( dataMap );
    }


    /**
     * Ask the phone to send the list of existing
     * contacts.
     */
    public void askForContacts(){
        DataMap dataMap = new DataMap();
        dataMap.putString( "action", "getContacts" );
        broadcastDatamapToPhoneNodes( dataMap );
    }


    /**
     * Ask the phone to send a message.
     * @param phone the target receiver.
     * @param pattern  the pattern.
     * @param text an optional text.
     */
    public void send( String phone, long[] pattern, String text ){
        DataMap dataMap = new DataMap();
        dataMap.putString( "action", "send" );
        dataMap.putString( "phone", phone );
        dataMap.putLongArray( "pattern", pattern );
        dataMap.putString( "text", text );
        broadcastDatamapToPhoneNodes( dataMap, "IVibrate, vibe sent" );

    }


    /**
     * Ask the phone to open the app.
     * @param phone an optional phone to show a
     *              given conversation.
     */
    public void askOpenApp( String phone ){
        DataMap dataMap = new DataMap();
        dataMap.putString( "action", "open" );
        dataMap.putString( "phone", phone );
        broadcastDatamapToPhoneNodes( dataMap );
    }


    /* Send data to all connected nodes. */
    private void broadcastDatamapToPhoneNodes( final DataMap dataMap ){
        broadcastDatamapToPhoneNodes( dataMap, null );
    }

    /* Send data to all connected nodes.
     * If toast is not null, an animation will be played upon success. */
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

                }
                Log.i( getPackageName(), "Data sent to phone. Status => " + status );
            }
        }.execute();
    }

    /* Show a success or fail animation */
    private void showAnimation( boolean success, String message ){
        Intent intent = new Intent( this, ConfirmationActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        intent.putExtra( ConfirmationActivity.EXTRA_ANIMATION_TYPE, success ? ConfirmationActivity.SUCCESS_ANIMATION
                : ConfirmationActivity.FAILURE_ANIMATION );
        intent.putExtra( ConfirmationActivity.EXTRA_MESSAGE, message );
        startActivity( intent );
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
