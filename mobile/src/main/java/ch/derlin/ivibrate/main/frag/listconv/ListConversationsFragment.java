package ch.derlin.ivibrate.main.frag.listconv;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.gcm.GcmCallbacks;
import ch.derlin.ivibrate.sql.entities.Friend;
import ch.derlin.ivibrate.sql.entities.Message;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by lucy on 19/06/15.
 */
public class ListConversationsFragment extends Fragment implements AdapterView.OnItemClickListener{

    ListView mList;
    ListConvAdapter mAdapter;
    Map<String, Friend> mFriends;
    ConversationFragmentCallbacks mListener;

    private GcmCallbacks mCallbacks = new GcmCallbacks(){
        @Override
        public void onMessageReceived( String from, Message message ){
            mAdapter.notifyDataSetChanged();
        }
    };

    // ----------------------------------------------------

    public interface ConversationFragmentCallbacks{
        void onAddConversation();

        void onConversationSelected( Friend friend );

        Map<String, Friend> getFriends();
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

        mFriends = mListener.getFriends();
        mList = ( ListView ) view.findViewById( R.id.listView );
        mAdapter = new ListConvAdapter( getActivity(), new ArrayList<>( mFriends.values() ) );
        mList.setAdapter( mAdapter );
        mList.setOnItemClickListener( this );
        setHasOptionsMenu( true );
        ( ( ActionBarActivity ) getActivity() ).getSupportActionBar().setDisplayHomeAsUpEnabled( false );
        getActivity().setTitle( "Conversations" );

        return view;
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


    @Override
    public void onItemClick( AdapterView<?> parent, View view, int position, long id ){
        if( mListener != null ) mListener.onConversationSelected( ( Friend ) mAdapter.getItem( position ) );
    }
}
