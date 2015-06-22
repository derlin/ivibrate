package ch.derlin.ivibrate.main;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import ch.derlin.ivibrate.PatternActivity;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.app.App;
import ch.derlin.ivibrate.gcm.GcmCallbacks;
import ch.derlin.ivibrate.gcm.GcmSenderService;
import ch.derlin.ivibrate.main.frag.listconv.ListConversationsFragment;
import ch.derlin.ivibrate.main.frag.oneconv.OneConvFragment;
import ch.derlin.ivibrate.sql.SqlDataSource;
import ch.derlin.ivibrate.sql.entities.Friend;
import ch.derlin.ivibrate.sql.entities.LocalContactDetails;
import ch.derlin.ivibrate.wear.SendToWearableService;
import ch.derlin.ivibrate.wear.WearableCallbacks;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static ch.derlin.ivibrate.utils.LocalContactsManager.getAvailableContacts;


public class MainActivity extends ActionBarActivity implements OneConvFragment.OneConvFragmentCallbacks,
        ListConversationsFragment.ConversationFragmentCallbacks{

    private static final int PATTERN_REQUEST_CODE = 7834;
    private ListConversationsFragment mListConvFragment;
    private OneConvFragment mOneConvFragment;
    private List<LocalContactDetails> mAvailableContacts;
    private Map<String, Friend> mFriends;
    private boolean mNewConvPending = false;

    /* *****************************************************************
     * activity
     * ****************************************************************/


    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_main );
        Toolbar mToolbar = ( Toolbar ) findViewById( R.id.toolbar_actionbar );
        setSupportActionBar( mToolbar );

        loadFriends();

    }


    private void setFragment( Fragment f ){
        getSupportFragmentManager().beginTransaction() //
                .replace( R.id.container, f )  //
                .commit();
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
                // TODO
                Toast.makeText( MainActivity.this, "Available contacts\n", Toast.LENGTH_SHORT ).show();
                Log.d( getPackageName(), App.getGson().toJson( mAvailableContacts ) );
                mNewConvPending = false;
                addConversation();
            }
        }

        @Override
        public void onNewRegistration( String account ){
            // TODO
            Toast.makeText( MainActivity.this, "New reg: " + account, Toast.LENGTH_LONG ).show();
        }


        @Override
        public void onUnregistration( String account ){
            // TODO
            Toast.makeText( MainActivity.this, "New unreg: " + account, Toast.LENGTH_LONG ).show();
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
                Friend friend = data.getParcelableExtra( "friend" );

                Toast.makeText( this, "PATTERN RESULT : " + Arrays.toString( pattern ), Toast.LENGTH_LONG ).show();
                GcmSenderService.getInstance().sendMessage( friend.getPhone(), pattern );
                Toast.makeText( this, "Message sent to " + friend.getPhone() + ".", Toast.LENGTH_SHORT ).show();

            }else{
                Toast.makeText( this, "PATTERN ACTIVITY CANCELED.", Toast.LENGTH_SHORT ).show();
            }

        }else{
            super.onActivityResult( requestCode, resultCode, data );
        }
    }

    /* *****************************************************************
     * fragment callbacks
     * ****************************************************************/


    @Override
    public Map<String, Friend> getFriends(){
        return mFriends;
    }


    @Override
    public void onAddConversation(){
        if( mAvailableContacts == null ){
            mNewConvPending = true;
            GcmSenderService.getInstance().askForAccounts();
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
    public void onSendMessageTo( Friend friend ){
        Intent i = new Intent( this, PatternActivity.class );
        Bundle bundle = new Bundle();
        bundle.putParcelable( "friend", friend );
        i.putExtras( bundle );
        startActivityForResult( i, PATTERN_REQUEST_CODE );
        Toast.makeText( this, "send message to ", Toast.LENGTH_LONG ).show();
    }


    @Override
    public void onBackToFriendsList(){
        mOneConvFragment = null;
        setFragment( mListConvFragment );
    }


    @Override
    public void onReplayPattern( long[] pattern ){
        SendToWearableService.getInstance().sendPattern( pattern );
    }

    // ----------------------------------------------------


    private void loadFriends(){
        final Context context = MainActivity.this;
        new AsyncTask<Void, Void, Map<String, Friend>>(){

            @Override
            protected Map<String, Friend> doInBackground( Void... params ){

                try( SqlDataSource src = new SqlDataSource( context, true ) ){
                    return src.getFriends();
                }catch( SQLException e ){
                    Log.d( context.getPackageName(), "Error retrieving data: " + e );
                }

                return new TreeMap<>();
            }


            @Override
            protected void onPostExecute( Map<String, Friend> friends ){
                mFriends = friends;
                // first load, launch the conversation fragment
                if( mListConvFragment == null ){
                    mListConvFragment = ListConversationsFragment.newInstance();
                    setFragment( mListConvFragment );
                }
            }
        }.execute();
    }
    // ----------------------------------------------------


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
                        // TODO: simplify
//                        if( !details.getName().equals( strName ) ){
//                            for( LocalContactDetails lcd : mAvailableContacts ){
//                                if( lcd.getName().equals( strName ) ){
//                                    details = lcd;
//                                    break;
//                                }
//                            }//end for
//                        }

                        dialog.dismiss();

                        if( details.getName().equals( strName ) ){
                            String phone = details.getPhone();

                            if( mFriends.containsKey( phone ) ){
                                // friend already exists,
                                onConversationSelected( mFriends.get( phone ) );
                                return;

                            }

                            try( SqlDataSource src = new SqlDataSource( MainActivity.this, true ) ){
                                // add the new friend and launch the conversation view
                                Friend f = new Friend();
                                f.setPhone( details.getPhone() );
                                f.setDetails( details );
                                src.addFriend( f );
                                mFriends.put( phone, f );
                                //                                mListConvFragment.notifyNewFriend( f );
                                onConversationSelected( f );
                            }catch( SQLException e ){
                                Log.d( getPackageName(), "Error adding friend " + e );
                            }
                        }
                    }
                }


        );
        builder.show();
    }
}
