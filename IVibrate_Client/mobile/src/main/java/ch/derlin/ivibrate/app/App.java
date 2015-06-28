package ch.derlin.ivibrate.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import ch.derlin.ivibrate.wear.SendToWearableService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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
    static Random random = new Random();
    static Set<Integer> msgIdSet = new HashSet<>();
    public static String TAG = "IVibrate";


    public static Context getAppContext(){
        return appContext;
    }


    public static Gson getGson(){ return gson; }

    //-------------------------------------------------------------


    /* Generate a unique message id. */
    public static String getMessageId(){
        int id;
        do{
            id = random.nextInt();
        }while( msgIdSet.contains( id ) );

        msgIdSet.add( id );
        return Integer.toString( id );
    }


    @Override
    public void onCreate(){
        super.onCreate();

        appContext = this.getApplicationContext();
        this.startService( new Intent( this, SendToWearableService.class ) );

    }


    @Override
    public void onTerminate(){
        this.stopService( new Intent( this, SendToWearableService.class ) );
        super.onTerminate();
    }
}
