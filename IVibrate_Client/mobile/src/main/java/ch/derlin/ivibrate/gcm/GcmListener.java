package ch.derlin.ivibrate.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * The listener on GCM events. Will just wake up a service
 * to treat the incoming message.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class GcmListener extends WakefulBroadcastReceiver{


    @Override
    public void onReceive( Context context, Intent intent ){

        // Explicitly specify that GcmMessageHandler will handle the intent.
        ComponentName comp = new ComponentName( context.getPackageName(), GcmIntentService.class.getName() );

        // Start the service, keeping the device awake while it is launching.
        startWakefulService( context, ( intent.setComponent( comp ) ) );
        setResultCode( Activity.RESULT_OK );
    }
}
