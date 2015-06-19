package ch.derlin.ivibrate.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import ch.derlin.ivibrate.wear.SendToWearableService;
import ch.derlin.ivibrate.gcm.GcmSenderService;

/**
 * Created by lucy on 17/06/15.
 */
public class App extends Application{

    static Context appContext;

    public static Context getAppContext(){
        return appContext;
    }

    //-------------------------------------------------------------

    @Override
    public void onCreate(){
        super.onCreate();

        appContext = this.getApplicationContext();
        this.startService( new Intent( this, SendToWearableService.class ) );
        this.startService( new Intent( this, GcmSenderService.class ) );

    }


    @Override
    public void onTerminate(){
        this.stopService( new Intent( this, SendToWearableService.class ) );
        this.stopService( new Intent( this, GcmSenderService.class ) );
        super.onTerminate();
    }
}
