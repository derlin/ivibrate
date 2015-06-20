package ch.derlin.ivibrate.main.frag.listconv;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.sql.entities.Friend;
import ch.derlin.ivibrate.sql.SqlDataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by lucy on 19/06/15.
 */
public class ListConversationsFragment extends Fragment implements AdapterView.OnItemClickListener{

    ListView mList;
    ListConvAdapter mAdapter;
    ConversationFragmentCallbacks mListener;


    public void notifyNewFriend( Friend f ){
        mAdapter.add( f );
    }

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
        setHasOptionsMenu( true );
        loadData();

        return view;
    }


    private void loadData(){
        new AsyncTask<Void, Void, Map<String, Friend>>(){

            @Override
            protected Map<String, Friend> doInBackground( Void... params ){

                try( SqlDataSource src = new SqlDataSource( getActivity(), true ) ){
                    return src.getFriends();
                }catch( SQLException e ){
                    Log.d( getActivity().getPackageName(), "Error retrieving data: " + e );
                }

                return new TreeMap<>();
            }


            @Override
            protected void onPostExecute( Map<String, Friend> friends ){
                mAdapter = new ListConvAdapter( getActivity(), new ArrayList<>( friends.values() ) );
                mList.setAdapter( mAdapter );
            }
        }.execute();
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
        if( mListener != null)
            mListener.onConversationSelected( ( Friend ) mAdapter.getItem( position ) );
    }
}
