package ch.derlin.ivibrate.main;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.app.App;
import ch.derlin.ivibrate.gcm.GcmCallbacks;
import ch.derlin.ivibrate.gcm.GcmConstants;
import ch.derlin.ivibrate.gcm.GcmSenderService;
import ch.derlin.ivibrate.main.frag.listconv.ListConversationsFragment;
import ch.derlin.ivibrate.main.frag.oneconv.OneConvFragment;
import ch.derlin.ivibrate.pattern.PatternActivity;
import ch.derlin.ivibrate.sql.LocalContactsManager;
import ch.derlin.ivibrate.sql.SqlDataSource;
import ch.derlin.ivibrate.sql.entities.Friend;
import ch.derlin.ivibrate.sql.entities.LocalContactDetails;
import ch.derlin.ivibrate.sql.entities.Message;
import ch.derlin.ivibrate.wear.SendToWearableService;
import ch.derlin.ivibrate.wear.WearableCallbacks;

import java.sql.SQLException;
import java.util.List;

import static ch.derlin.ivibrate.sql.LocalContactsManager.getAvailableContacts;

/**
 * The main activity. On start, shows the {@link ListConversationsFragment}.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class MainActivity extends ActionBarActivity implements OneConvFragment.OneConvFragmentCallbacks,
        ListConversationsFragment.ConversationFragmentCallbacks{

    private static final int PATTERN_REQUEST_CODE = 7834;

    private ListConversationsFragment mListConvFragment;
    private OneConvFragment mOneConvFragment;

    private List<LocalContactDetails> mAvailableContacts;

    private boolean mNewConvPending = false;

    /* *****************************************************************
     * activity
     * ****************************************************************/


    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_main );
        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar_actionbar );
        setSupportActionBar( toolbar );

        ActionBar actionBar = getSupportActionBar();
        if( actionBar != null ){
            actionBar.setDisplayUseLogoEnabled( true );
            actionBar.setLogo( R.mipmap.ic_launcher );
        }

        setTitle( "IVibrate" );

        Friend f = getFriendExtra();
        if( f != null ){
            onConversationSelected( f );
        }else{
            // first load, launch the conversation fragment
            if( mListConvFragment == null ){
                mListConvFragment = ListConversationsFragment.newInstance();
            }
            setFragment( mListConvFragment );
        }

    }


    private void setFragment( Fragment f ){
        getSupportFragmentManager().beginTransaction() //
                .replace( R.id.container, f )  //
                        // avoid "can not perform this action after
                        // OnSaveInstanceState" error
                .commitAllowingStateLoss();
    }


    @Override
    public boolean onOptionsItemSelected( MenuItem item ){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if( id == R.id.action_settings ){
            return true;
        }

        return super.onOptionsItemSelected( item );
    }


    @Override
    protected void onStart(){
        super.onStart();
        mGcmCallbacks.registerSelf( this );
        mWearableCallbacks.registerSelf( this );
    }


    @Override
    protected void onStop(){
        mGcmCallbacks.unregisterSelf( this );
        mWearableCallbacks.unregisterSelf( this );
        super.onStop();
    }


    @Override
    protected void onNewIntent( Intent intent ){
        setIntent( intent );
        Friend f = getFriendExtra();
        if( f != null ){
            onConversationSelected( f );
        }
    }


    @Override
    public void onBackPressed(){
        if(mOneConvFragment != null){
            onBackToFriendsList();
        }else{
            super.onBackPressed();
        }
    }

    /* *****************************************************************
     * Wearable callbacks
     * ****************************************************************/

    private WearableCallbacks mWearableCallbacks = new WearableCallbacks(){
        @Override
        public void onFail( String errorMsg ){
            Toast.makeText( MainActivity.this, "Error sending pattern to your watch: " + errorMsg, Toast.LENGTH_LONG
            ).show();
        }


        @Override
        public void onSuccess( String details ){
            Toast.makeText( MainActivity.this, "Pattern sent to: " + details, Toast.LENGTH_SHORT ).show();

        }
    };

    /* *****************************************************************
     * gcm callbacks
     * ****************************************************************/

    private GcmCallbacks mGcmCallbacks = new GcmCallbacks(){

        @Override
        public void onAccountsReceived( String[] accounts ){
            mAvailableContacts = getAvailableContacts( accounts );
            if( mNewConvPending ){
                Log.d( getPackageName(), App.getGson().toJson( mAvailableContacts ) );
                mNewConvPending = false;
                addConversation();
            }
        }


        @Override
        public void onNewRegistration( String phone ){
            if( mAvailableContacts != null ){
                LocalContactDetails details = LocalContactsManager.getContactDetails( phone );
                if( details != null ) mAvailableContacts.add( details );
            }
            Toast.makeText( MainActivity.this, "New registration: " + phone, Toast.LENGTH_SHORT ).show();
        }


        @Override
        public void onUnregistration( String account ){
            // TODO
            Toast.makeText( MainActivity.this, "Unregistration: " + account, Toast.LENGTH_SHORT ).show();
        }
    };

    /* *****************************************************************
     * activity result
     * ****************************************************************/


    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data ){
        if( requestCode == PATTERN_REQUEST_CODE ){
            if( resultCode == Activity.RESULT_OK ){
                long[] pattern = data.getLongArrayExtra( "pattern" );
                String text = data.getStringExtra( "message" );
                Friend friend = data.getParcelableExtra( "friend" );

                GcmSenderService.sendMessage( friend.getPhone(), pattern, text );
                Toast.makeText( this, "Message sent to " + friend.getPhone() + ".", Toast.LENGTH_SHORT ).show();

            }else{
                Toast.makeText( this, "Send process canceled.", Toast.LENGTH_SHORT ).show();
            }

        }else{
            super.onActivityResult( requestCode, resultCode, data );
        }
    }

    /* *****************************************************************
     * fragment callbacks
     * ****************************************************************/


    @Override
    public void onAddConversation(){
        if( mAvailableContacts == null ){
            mNewConvPending = true;
            GcmSenderService.askForAccounts();
        }else{
            addConversation();
        }
    }


    @Override
    public void onConversationSelected( Friend friend ){
        mOneConvFragment = OneConvFragment.newInstance( friend );
        setFragment( mOneConvFragment );
    }


    @Override
    public void onSendMessageTo( Friend friend, Message... message ){
        if( message.length == 0 ){
            // no pattern, show the screen to tap one
            Intent i = new Intent( this, PatternActivity.class );
            Bundle bundle = new Bundle();
            bundle.putParcelable( "friend", friend );
            i.putExtras( bundle );
            startActivityForResult( i, PATTERN_REQUEST_CODE );

        }else{
            Message m = message[ 0 ];
            // one pattern, just send it
            GcmSenderService.sendMessage( friend.getPhone(), m.getPatternObject(), m.getText() );
        }
    }


    @Override
    public void onBackToFriendsList(){
        mOneConvFragment = null;
        setFragment( mListConvFragment );
    }


    @Override
    public void onReplayPattern( long[] pattern ){
        SendToWearableService.sendPattern( pattern );
    }

    /* *****************************************************************
     * friends management
     * ****************************************************************/


    private Friend getFriendExtra(){
        Bundle extras = getIntent().getExtras();
        // if a notification was pressed, show the contact
        if( extras != null && extras.containsKey( GcmConstants.FROM_KEY ) ){
            String from = extras.getString( GcmConstants.FROM_KEY );
            if( from == null ) return null;

            try( SqlDataSource src = new SqlDataSource( this, true ) ){
                return src.getFriend( from );

            }catch( SQLException e ){
                Log.d( getPackageName(), "Error retrieving friend " + from );
            }
        }

        return null;
    }


    public void addConversation(){
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle( "Select a friend:" );
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>( this, android.R.layout
                .select_dialog_singlechoice );

        for( LocalContactDetails details : mAvailableContacts ){
            arrayAdapter.add( details.getName() );
        }//end for

        builder.setNegativeButton( "cancel", new DialogInterface.OnClickListener(){

            @Override
            public void onClick( DialogInterface dialog, int which ){
                dialog.dismiss();
            }
        } );

        builder.setAdapter( arrayAdapter, new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick( DialogInterface dialog, int which ){
                        String strName = arrayAdapter.getItem( which );
                        LocalContactDetails details = mAvailableContacts.get( which );
                        dialog.dismiss();

                        if( details.getName().equals( strName ) ){

                            Friend f = new Friend();
                            f.setPhone( details.getPhone() );
                            f.setDetails( details );

                            try( SqlDataSource src = new SqlDataSource( MainActivity.this, true ) ){
                                // add the new friend and launch the conversation view
                                src.addFriend( f );

                            }catch( SQLException e ){
                                // the friend already existed
                                Log.d( getPackageName(), "Error adding friend. Already exists ? " );
                            }
                            onConversationSelected( f );
                        }
                    }
                }


        );
        builder.show();
    }
}
