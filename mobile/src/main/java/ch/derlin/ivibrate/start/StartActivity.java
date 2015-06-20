package ch.derlin.ivibrate.start;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import ch.derlin.ivibrate.main.MainActivity;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.wear.SendToWearableService;
import ch.derlin.ivibrate.gcm.GcmCallbacks;
import ch.derlin.ivibrate.gcm.GcmSenderService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class StartActivity extends FragmentActivity{

    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 12;

    private String phone;

    // ----------------------------------------------------

    private GcmCallbacks mCallbacks = new GcmCallbacks(){

        @Override
        public void onNewRegistration( String account ){
            if( account.equals( phone ) ){

                PreferenceManager.getDefaultSharedPreferences( getApplicationContext() ).edit() //
                        .putString( getString( R.string.pref_phone ), phone ) //
                        .commit();

                Toast.makeText( getApplicationContext(), "Successfully registered with phone " + phone, Toast
                        .LENGTH_LONG ).show();

                launchMainActivity();
            }
        }
    };


    @Override
    protected void onDestroy(){
        mCallbacks.unregisterSelf( this );
        super.onDestroy();
    }


    @Override
    protected void onStart(){
        super.onStart();
        mCallbacks.registerSelf( this );
    }

    // ----------------------------------------------------


    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );

        if( !checkPlayServices() ){
            showErrorDialog( "Be sure you have google play services installed and try again.", true );
        }
        //        if( !checkWearable() ){
        //            showErrorDialog( "Be sure you have android wear installed and a connected device and try again
        // .", true );
        //        }

        phone = PreferenceManager.getDefaultSharedPreferences( this ).getString( getString( R.string.pref_phone ),
                null );


        if( phone != null ){
            GcmSenderService.getInstance().registerToServer( phone );
            launchMainActivity();
        }

        if( !checkNetwork() ){
            showErrorDialog( "You need an internet connection to register.", true );
        }

        setContentView( R.layout.activity_start );
        getSupportFragmentManager().beginTransaction().replace( R.id.fragment, new PhoneFragment() ).commit();
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


    private boolean checkNetwork(){
        ConnectivityManager connectivityManager = ( ConnectivityManager ) getSystemService( Context
                .CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private boolean checkWearable(){
        return SendToWearableService.getInstance().isConnected();
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

            }else{
                Toast.makeText( getActivity(), "Please, enter a valid phone number.", Toast.LENGTH_SHORT ).show();
            }
        }
    }
}
