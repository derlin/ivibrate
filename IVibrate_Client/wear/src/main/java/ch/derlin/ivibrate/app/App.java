package ch.derlin.ivibrate.app;

import android.app.Application;
import android.content.Context;

/**
 * The application class, which starts the Service on app launch.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class App extends Application{
    static Context appContext;

    public static Context getAppContext(){
        return appContext;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        appContext = this.getApplicationContext();
    }
}
