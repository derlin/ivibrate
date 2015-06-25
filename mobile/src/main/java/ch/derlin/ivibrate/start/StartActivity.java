package ch.derlin.ivibrate.start;

import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.gcm.GcmSenderService;
import ch.derlin.ivibrate.main.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

 /**
  * This class checks that the application is registered
  * -------------------------------------------------  <br />
  * context      Advanced Interface - IVibrate project <br />
  * date         June 2015                             <br />
  * -------------------------------------------------  <br />
  *
  * @author Lucy Linder
  */
public class StartActivity extends FragmentActivity{

    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 12;

    private String phone;

    // ----------------------------------------------------

    private ServiceConnection mConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected( ComponentName name, IBinder service ){
            setInterface();
        }


        @Override
        public void onServiceDisconnected( ComponentName name ){

        }
    };

    // ----------------------------------------------------

    @Override
    protected void onDestroy(){
        unbindService( mConnection );
        super.onDestroy();
    }


    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = new Intent(this, GcmSenderService.class);
        bindService( intent, mConnection, Context.BIND_AUTO_CREATE );
    }

    // ----------------------------------------------------


    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );

        if( !checkPlayServices() ){
            showErrorDialog( "Be sure you have google play services installed and try again.", true );
        }

        phone = PreferenceManager.getDefaultSharedPreferences( this ).getString( getString( R.string.pref_phone ), null );
        setContentView( R.layout.activity_start );
        getSupportFragmentManager().beginTransaction().replace( R.id.fragment, new DummyFragment() ).commit();

    }

    protected void setInterface(){

        if( phone != null ){
            while(GcmSenderService.getInstance() == null){
                try{
                    Thread.sleep( 100 );
                }catch( InterruptedException e ){
                    e.printStackTrace();
                }
            }; // wait for the app to start TODO
            GcmSenderService.getInstance().registerToServer( phone );
            launchMainActivity();

        }else{

            getSupportFragmentManager().beginTransaction().replace( R.id.fragment, new PhoneFragment() ).commit();
        }
    }


    private void showErrorDialog( final String errorText, final boolean quitApp ){
        AlertDialog alertDialog = new AlertDialog.Builder( this ).create();
        alertDialog.setTitle( "Error" );
        alertDialog.setMessage( errorText );
        alertDialog.setButton( AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener(){
            public void onClick( DialogInterface dialog, int which ){
                dialog.dismiss();
                if( quitApp ){
                    finish();
                    System.exit( 0 );
                }
            }
        } );
        alertDialog.show();
    }



    private void launchMainActivity(){
        startActivity( new Intent( this, MainActivity.class ) );
        finish();
    }


    private boolean checkPlayServices(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable( this );
        if( resultCode != ConnectionResult.SUCCESS ){
            if( GooglePlayServicesUtil.
                    isUserRecoverableError( resultCode ) ){
                GooglePlayServicesUtil.getErrorDialog( resultCode, this, REQUEST_GOOGLE_PLAY_SERVICES ).show();
            }else{
                Log.i( "GCM", "This device is not supported." );
            }
            return false;
        }
        return true;
    }


    /* *****************************************************************
     * first fragment: enter phone
     * ****************************************************************/
    class PhoneFragment extends Fragment implements View.OnClickListener{

        EditText et;

        @Override
        public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
            View view = inflater.inflate( R.layout.fragment_start_enter_phone, container, false );
            et = ( EditText ) view.findViewById( R.id.edittext_phone );
            Button btn = ( Button ) view.findViewById( R.id.btn_next );
            btn.setOnClickListener( this );

            return view;
        }


        @Override
        public void onClick( View v ){
            phone = et.getText().toString();
            if( phone.matches( "07[0-9]{8}" ) ){
                GcmSenderService.getInstance().registerToServer( phone );
                PreferenceManager.getDefaultSharedPreferences( getActivity() ).edit() //
                .putString( getActivity().getString( R.string.pref_phone ), phone ). commit();
                launchMainActivity();

            }else{
                Toast.makeText( getActivity(), "Please, enter a valid phone number.", Toast.LENGTH_SHORT ).show();
            }
        }
    }

    /* *****************************************************************
     * dummy fragment
     * ****************************************************************/
    class DummyFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
            return inflater.inflate( R.layout.fragment_start_progressbar, container, false );
        }
    }
}
