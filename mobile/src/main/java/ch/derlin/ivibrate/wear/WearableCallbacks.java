package ch.derlin.ivibrate.wear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * This class handles the local broadcast made by the
 * {@link SendToWearableService}.
 * It is meant to be overriden. To use it, create a child
 * class overriding the methods you need and call {@link #registerSelf(Context)}
 * when the activity/fragment starts. Don't forget to call
 * {@link #unregisterSelf(Context)} upon destroy.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public abstract class WearableCallbacks extends BroadcastReceiver{

    private static final IntentFilter INTENT_FILTER = //
            new IntentFilter( WearableConstants.SEND_TO_WEARABLE_SERVICE_INTENT_FILTER );


    public abstract void onFail( String errorMsg );

    public abstract void onSuccess( String details );

    // ----------------------------------------------------


    public void registerSelf( Context context ){
        LocalBroadcastManager.getInstance( context ).registerReceiver( this, INTENT_FILTER );
    }


    public void unregisterSelf( Context context ){
        LocalBroadcastManager.getInstance( context ).unregisterReceiver( this );
    }

    // ----------------------------------------------------


    @Override
    public void onReceive( Context context, Intent intent ){
        String msg = intent.getStringExtra( WearableConstants.EXTRA_STRING );

        switch( intent.getStringExtra( WearableConstants.EXTRA_EVT_TYPE ) ){
            case WearableConstants.FAIL_EVT_TYPE:
                onFail( msg );
                break;
            case WearableConstants.SUCCESS_EVT_TYPE:
                onSuccess( msg );
                break;
            default:
                break;
        }
    }
}
