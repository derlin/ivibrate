package ch.derlin.ivibrate.wear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import static ch.derlin.ivibrate.wear.SendToWearableService.*;

/**
 * Created by lucy on 17/06/15.
 */
public abstract class ActionCallbacks extends BroadcastReceiver{

    private static final IntentFilter INTENT_FILTER = new IntentFilter( SWSERVICE_INTENT_FILTER );


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
        String msg = intent.getStringExtra( EXTRA_STRING );

        switch( intent.getStringExtra( EXTRA_EVT_TYPE ) ){
            case FAIL_EVT_TYPE:
                onFail( msg );
                break;
            case SUCCESS_EVT_TYPE:
                onSuccess( msg );
                break;
            default:
                break;
        }
    }
}
