package ch.derlin.ivibrate;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;

import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SendToWearableService extends Service{

    public static final String WEARABLE_DATA_PATH = "/derlin/ivibrate/";
    public static final String SWSERVICE_INTENT_FILTER = "SendToWearableService";

    public static final String EXTRA_EVT_TYPE = "evt_type", FAIL_EVT_TYPE = "fail", SUCCESS_EVT_TYPE = "success",
    EXTRA_STRING =
            "msg";

    private static SendToWearableService INSTANCE;
    private LocalBroadcastManager mBroadcastManager;

    // ----------------------------------------------------
    private ActionCallbacks mCallbacks = new ActionCallbacks(){
        @Override
        public void onFail( String errorMsg ){
            mBroadcastManager.sendBroadcast( getIntent( FAIL_EVT_TYPE, errorMsg ) );
        }


        @Override
        public void onSuccess( String details ){
            mBroadcastManager.sendBroadcast( getIntent( SUCCESS_EVT_TYPE, details ) );
        }
    };
    // ----------------------------------------------------

    public class SendToWearableBinder extends Binder{
        SendToWearableService getService(){
            // Return this instance of LocalService so clients can call public methods
            return SendToWearableService.this;
        }
    }

    private final IBinder mBinder = new SendToWearableBinder();

    // ----------------------------------------------------
    public interface SendToWearableCallback{
        public void onFail(String errorMsg);
        public void onSuccess(String nodeName);
    }
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
        if(mGoogleClient != null){
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
       new SendToWearableThread( mGoogleClient, WEARABLE_DATA_PATH, dataMap, mCallbacks ).start();
    }


    public GoogleApiClient getGoogleClient(){

        if( mGoogleClient == null ){
            mGoogleClient = new GoogleApiClient.Builder( this )
                    .addApi( Wearable.API )  //
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

}
