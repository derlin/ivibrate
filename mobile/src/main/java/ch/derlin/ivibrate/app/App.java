package ch.derlin.ivibrate.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import ch.derlin.ivibrate.gcm.GcmSenderService;
import ch.derlin.ivibrate.wear.SendToWearableService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * The application class.
 * Contains some useful static fields and handle the
 * start/stop of the two services, GCM and wearable.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class App extends Application{

    static Context appContext;
    static Gson gson = new GsonBuilder().create();

    public static String TAG = "IVibrate";


    public static Context getAppContext(){
        return appContext;
    }


    public static Gson getGson(){ return gson; }

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
