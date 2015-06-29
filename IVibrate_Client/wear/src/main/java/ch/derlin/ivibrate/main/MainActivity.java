package ch.derlin.ivibrate.main;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.wearable.view.WatchViewStub;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.comm.ListenerService;
import ch.derlin.ivibrate.comm.SendToPhoneService;
import ch.derlin.ivibrate.main.frag.*;
import ch.derlin.ivibrate.utils.Friend;

import java.util.ArrayList;
import java.util.List;

 /**
  * The main activity to send a message.
  * -------------------------------------------------  <br />
  * context      Advanced Interface - IVibrate project <br />
  * date         June 2015                             <br />
  * -------------------------------------------------  <br />
  *
  * @author Lucy Linder
  */
public class MainActivity extends Activity implements PatternFragment.PatternFragmentCallbacks, //
        TextFragment.TextFragmentCallbacks, //
        ContactsFragment.ContactsFragmentCallbacks{

    private long[] pattern;
    private String text;
    private String phone;

    private WatchViewStub mStub;

    // ----------------------------------------------------


    @Override
    protected void onDestroy(){
        ListenerService.isWaitingForContact( false );
        super.onDestroy();
    }


    @Override
    protected void onNewIntent( Intent intent ){
        super.onNewIntent( intent );
        if( intent.hasExtra( "contacts" ) ){
            // we received the list of contacts !
            // TODO: if no contacts ?
            setInterface( intent );
        }
    }


     @Override
     protected void onCreate( Bundle savedInstanceState ){
         super.onCreate( savedInstanceState );
         setInterface( getIntent() );
     }

     // ----------------------------------------------------

    /* set the interface */
    private void setInterface( Intent intent ){
        Bundle extras = intent.getExtras();

        final Fragment f;
        if( extras == null ){
            // no phone => ask for the contact's list and show a progressbar
            SendToPhoneService.askForContacts();
            f = WaitFragment.getInstance( "Retrieving contact's list..." );
            ListenerService.isWaitingForContact( true );

        }else if( extras.containsKey( "phone" ) ){
            // a reply: show the pattern fragment
            phone = extras.getString( "phone" );
            NotificationManagerCompat.from( getApplicationContext() ).cancel( Integer.parseInt( phone ) );
            f = new PatternFragment();

        }else if( extras.containsKey( "contacts" ) ){
            List<Friend> contacts = ( ArrayList<Friend> ) extras.getSerializable( "contacts" );

            if(contacts == null || contacts.size() == 0){
                // no contacts...
                f = new NoContactFragment();

            }else{
                // the list of contacts has been received: show it
                f = new ContactsFragment();
                f.setArguments( extras );
            }

        }else{
            // should never happen...
            finish();
            return;
        }

        if( mStub == null ){
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
            // simply show the right fragment
            setFrag( f );
        }
    }

    /* show a fragment */
    private void setFrag( Fragment f ){
        getFragmentManager().beginTransaction().replace( R.id.fragment, f ).commit();
    }

    /* ask the phone to send a new message and quit
    * (a feedback animation will be played by the service itself) */
    private void send(){
        SendToPhoneService.send( phone, pattern, text );
        finish();
    }


    /* *****************************************************************
     * fragments callback
     * ****************************************************************/


    @Override
    public void onPatternValidated( long[] pattern ){
        this.pattern = pattern;
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
