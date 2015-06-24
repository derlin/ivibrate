package ch.derlin.ivibrate.main;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.widget.Toast;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.SendToPhoneService;
import ch.derlin.ivibrate.main.frag.ContactsFragment;
import ch.derlin.ivibrate.main.frag.PatternFragment;
import ch.derlin.ivibrate.main.frag.TextFragment;
import ch.derlin.ivibrate.main.frag.WaitFragment;

import java.util.List;

public class MainActivity extends Activity implements PatternFragment.PatternFragmentCallbacks, //
        TextFragment.TextFragmentCallbacks, //
        ContactsFragment.ContactsFragmentCallbacks{

    long[] pattern;
    String text;
    String phone;


    // ----------------------------------------------------

    private ServiceConnection mConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected( ComponentName name, IBinder service ){
            setInterface( getIntent() );
        }


        @Override
        public void onServiceDisconnected( ComponentName name ){

        }
    };
    private WatchViewStub mStub;

    // ----------------------------------------------------


    @Override
    protected void onDestroy(){
        unbindService( mConnection );
        super.onDestroy();
    }


    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = new Intent( this, SendToPhoneService.class );
        bindService( intent, mConnection, Context.BIND_AUTO_CREATE );
    }


    @Override
    protected void onNewIntent( Intent intent ){
        super.onNewIntent( intent );
        if( intent.hasExtra( "contacts" ) ){
            setInterface( intent );
        }else if( intent.hasExtra( "feedback" ) ){
            Toast.makeText( this, "Message sent.", Toast.LENGTH_SHORT ).show();
            finish();
        }

    }

    // ----------------------------------------------------


    private void setInterface( Intent intent ){
        Bundle extras = intent.getExtras();

        final Fragment f;
        if( extras == null ){
            SendToPhoneService.getInstance().askForContacts();
            f = WaitFragment.getInstance( "Retrieving contact's list..." );

        }else if( extras.containsKey( "phone" ) ){
            phone = extras.getString( phone );
            f = new PatternFragment();

        }else if( extras.containsKey( "contacts" ) ){
            f = new ContactsFragment();
            f.setArguments( extras );

        }else{
            finish();
            return;
        }

        if(mStub == null){
            // inflate only if necessary
            setContentView( R.layout.activity_main_stub );
            mStub = ( WatchViewStub ) findViewById( R.id.watch_view_stub );
            mStub.setOnLayoutInflatedListener( new WatchViewStub.OnLayoutInflatedListener(){
                @Override
                public void onLayoutInflated( WatchViewStub stub ){
                    setFrag( f );
                }
            } );

        }else{
            setFrag( f );
        }
    }


    private void setFrag( Fragment f ){
        getFragmentManager().beginTransaction().replace( R.id.fragment, f ).commit();
    }


    private void send(){
//        setFrag( WaitFragment.getInstance( "Sending..." ) );
        SendToPhoneService.getInstance().send( phone, pattern, text );
        finish();
    }
    /* *****************************************************************
     * fragments callback
     * ****************************************************************/


    @Override
    public void onPatternValidated( long[] pattern ){
        this.pattern = pattern;
        Toast.makeText( this, "Yeah, pattern validated!", Toast.LENGTH_SHORT ).show();
        setFrag( new TextFragment() );
    }


    @Override
    public void onPatternCanceled(){
        finish();
    }


    @Override
    public void onNoText(){
        send();
        finish();
    }


    @Override
    public void onGetText(){

        Intent intent = new Intent( RecognizerIntent.ACTION_RECOGNIZE_SPEECH );
        intent.putExtra( RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM );
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult( intent, MainActivity.SPEECH_REQUEST_CODE );


    }


    @Override
    public void onTextCanceled(){
        finish();
    }


    @Override
    public void onPhoneSelected( String phone ){
        this.phone = phone;
        setFrag( new PatternFragment() );
    }

    /* *****************************************************************
     * speech result
     * ****************************************************************/

    public static final int SPEECH_REQUEST_CODE = 0;


    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ){
        if( requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK ){
            setFrag( new WaitFragment() );
            List<String> results = data.getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS );
            text = results.get( 0 );
            send();
            // Do something with spokenText

        }
        super.onActivityResult( requestCode, resultCode, data );
    }


}
