package ch.derlin.ivibrate.app;

import android.app.Application;
import android.content.Intent;
import ch.derlin.ivibrate.comm.SendToPhoneService;

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

    @Override
    public void onCreate(){
        super.onCreate();
        this.startService( new Intent( this, SendToPhoneService.class ) );

    }



    @Override
    public void onTerminate(){
        this.stopService( new Intent( this, SendToPhoneService.class ) );
        super.onTerminate();
    }
}
