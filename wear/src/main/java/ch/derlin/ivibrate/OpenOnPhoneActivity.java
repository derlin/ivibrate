package ch.derlin.ivibrate;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import ch.derlin.ivibrate.comm.SendToPhoneService;


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
