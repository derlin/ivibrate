package ch.derlin.ivibrate;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import ch.derlin.ivibrate.comm.SendToPhoneService;


 /**
  * Activity launched by the "open on phone" action of
  * a notification. Send a request to the phone and quits.
  * -------------------------------------------------  <br />
  * context      Advanced Interface - IVibrate project <br />
  * date         June 2015                             <br />
  * -------------------------------------------------  <br />
  *
  * @author Lucy Linder
  */
public class OpenOnPhoneActivity extends Activity{

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        String phone = getIntent().getExtras().getString( "phone" );
        SendToPhoneService.getInstance().askOpenApp(phone);
        NotificationManagerCompat.from( getApplicationContext() ).cancel( Integer.parseInt( phone ) );
        finish();
    }
}
