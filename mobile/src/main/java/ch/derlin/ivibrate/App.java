package ch.derlin.ivibrate;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

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
        Intent i = new Intent( this, SendToWearableService.class );
        this.startService( i );

    }


    @Override
    public void onTerminate(){
        this.stopService( new Intent( this, SendToWearableService.class ) );
        super.onTerminate();
    }
}
