package ch.derlin.ivibrate.main.frag.listconv;


import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.app.AppUtils;
import ch.derlin.ivibrate.gcm.GcmCallbacks;
import ch.derlin.ivibrate.sql.SqlDataSource;
import ch.derlin.ivibrate.sql.entities.Friend;
import ch.derlin.ivibrate.sql.entities.Message;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Fragment displaying the list of contacts/conversations.
 * The user can select, add and delete one.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class ListConversationsFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView
        .OnItemLongClickListener{

    ListView mList;
    ListConvAdapter mAdapter;
    Map<String, Friend> mFriends;
    ConversationFragmentCallbacks mListener;

    private GcmCallbacks mCallbacks = new GcmCallbacks(){
        @Override
        public void onMessageReceived( String from, Message message ){
            if( !mFriends.containsKey( from ) ){
                Friend f = new Friend( from );
                mFriends.put( from, f );
                mAdapter.add( f );
            }
            mAdapter.notifyDataSetChanged();
        }
    };


    // ----------------------------------------------------

    public interface ConversationFragmentCallbacks{
        void onAddConversation();

        void onConversationSelected( Friend friend );
    }

    // ----------------------------------------------------


    public static ListConversationsFragment newInstance(){
        return new ListConversationsFragment();
    }


    @Override
    public void onAttach( Activity activity ){
        super.onAttach( activity );
        mCallbacks.registerSelf( getActivity() );
        if( activity instanceof ConversationFragmentCallbacks ){
            mListener = ( ConversationFragmentCallbacks ) activity;
        }
    }


    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
        View view = inflater.inflate( R.layout.fragment_main_listconv, container, false );
        mList = ( ListView ) view.findViewById( R.id.listView );
        mList.setOnItemClickListener( this );
        mList.setOnItemLongClickListener( this );
        setHasOptionsMenu( true );

        ActionBar toolbar = ( ( ActionBarActivity ) getActivity() ).getSupportActionBar();
        if( toolbar != null ){
            toolbar.setDisplayUseLogoEnabled( true );
            toolbar.setDisplayHomeAsUpEnabled( false );
        }

        getActivity().setTitle( "Conversations" );

        loadFriendAsync();

        return view;
    }


    private void loadFriendAsync(){
        new AppUtils.LoadFriendAsyncTask( getActivity() ){

            @Override
            protected void onPostExecute( Map<String, Friend> friends ){
                mFriends = friends;
                mAdapter = new ListConvAdapter( getActivity(), new ArrayList<>( mFriends.values() ) );
                mList.setAdapter( mAdapter );
            }
        }.execute();
    }


    @Override
    public void onDetach(){
        super.onDetach();
        mCallbacks.unregisterSelf( getActivity() );
    }


    @Override
    public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ){
        inflater.inflate( R.menu.conversations_menu, menu );
        super.onCreateOptionsMenu( menu, inflater );
    }


    @Override
    public boolean onOptionsItemSelected( MenuItem item ){

        switch( item.getItemId() ){
            case R.id.menu_add:
                if( mListener != null ) mListener.onAddConversation();
                break;
        }

        return super.onOptionsItemSelected( item );
    }

    // ----------------------------------------------------


    @Override
    public void onItemClick( AdapterView<?> parent, View view, int position, long id ){
        if( mListener != null ) mListener.onConversationSelected( ( Friend ) mAdapter.getItem( position ) );
    }


    @Override
    public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id ){
        // delete selected friend, asking for confirmation first
        final Friend friend = ( Friend ) mAdapter.getItem( position );
        final String name = friend.getDetails() != null ? friend.getDetails().getName() : friend.getPhone();
        new AlertDialog.Builder( getActivity() ).setIcon( android.R.drawable.ic_dialog_alert ).setTitle( "Delete " +
                name ).setMessage( "Are you sure you want to remove this friend? The whole conversation will be lost"
                + "." ).setPositiveButton( "Yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick( DialogInterface dialog, int which ){
                try( SqlDataSource src = new SqlDataSource( getActivity(), true ) ){
                    if( src.deleteFriend( friend.getPhone() ) ){
                        mFriends.remove( friend.getPhone() );
                        mAdapter.remove( friend );
                    }
                }catch( SQLException e ){
                    Log.d( getActivity().getPackageName(), "Error deleting " + name );
                }
            }

        } ).setNegativeButton( "No", null ).show();
        return true;
    }

    // ----------------------------------------------------


    public void notifyNewFriend( Friend f ){
        mFriends.put( f.getPhone(), f );
        if( mAdapter != null ) mAdapter.add( f );
    }


}
