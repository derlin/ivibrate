package ch.derlin.ivibrate.main.frag.oneconv;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.app.App;
import ch.derlin.ivibrate.sql.entities.Friend;
import ch.derlin.ivibrate.sql.entities.Message;
import ch.derlin.ivibrate.sql.SqlDataSource;
import com.google.gson.reflect.TypeToken;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OneConvFragmentCallbacks}
 * interface.
 */
public class OneConvFragment extends Fragment implements AbsListView.OnItemClickListener{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FRIEND = "param1";

    private Friend mFriend;
    private OneConvFragmentCallbacks mListener;
    private AbsListView mListView;
    private ListAdapter mAdapter;

    // ----------------------------------------------------

    public interface OneConvFragmentCallbacks{
        public void onSendMessageTo( Friend friend );

        public void onReplayPattern( long[] pattern );

        public void onBackToFriendsList();
    }

    // ----------------------------------------------------

    public static OneConvFragment newInstance( Friend friend ){
        OneConvFragment fragment = new OneConvFragment();
        Bundle args = new Bundle();
        args.putParcelable( ARG_FRIEND, friend );
        fragment.setArguments( args );
        return fragment;
    }


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OneConvFragment(){
    }


    @Override
    public void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );

        if( getArguments() != null ){
            mFriend = getArguments().getParcelable( ARG_FRIEND );
            getActivity().setTitle(mFriend.getDetails().getName());
        }

        (( ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        setHasOptionsMenu( true );
        loadData();
    }


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
        View view = inflater.inflate( R.layout.fragment_oneconv, container, false );

        // Set the adapter
        mListView = ( AbsListView ) view.findViewById( android.R.id.list );
        ( ( AdapterView<ListAdapter> ) mListView ).setAdapter( mAdapter );

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener( this );

        return view;
    }


    @Override
    public void onAttach( Activity activity ){
        super.onAttach( activity );
        try{
            mListener = ( OneConvFragmentCallbacks ) activity;
        }catch( ClassCastException e ){
            throw new ClassCastException( activity.toString() + " must implement OnFragmentInteractionListener" );
        }
    }


    @Override
    public void onDetach(){
        super.onDetach();
        mListener = null;
    }



    // ----------------------------------------------------


    private void loadData(){
        new AsyncTask<Void, Void, List<Message>>(){

            @Override
            protected List<Message> doInBackground( Void... params ){

                try( SqlDataSource src = new SqlDataSource( getActivity(), true ) ){
                    return src.getMessagesWith( mFriend.getPhone() );

                }catch( SQLException e ){
                    Log.d( getActivity().getPackageName(), "Error retrieving messages: " + e );
                }

                return new ArrayList<>();
            }


            @Override
            protected void onPostExecute( List<Message> message ){
                mAdapter = new OneConvAdapter( getActivity(), new ArrayList<>( message ) );
                mListView.setAdapter( mAdapter );
            }
        }.execute();
    }
    // ----------------------------------------------------


    @Override
    public void onItemClick( AdapterView<?> parent, View view, int position, long id ){
        if( null != mListener ){
            Message m = ( Message ) mAdapter.getItem( position );
            mListener.onReplayPattern( ( long[] ) //
                    App.getGson().fromJson( m.getPattern(),  //
                            new TypeToken<long[]>(){}.getType() ) );
        }
    }


    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText( CharSequence emptyText ){
        View emptyView = mListView.getEmptyView();

        if( emptyView instanceof TextView ){
            ( ( TextView ) emptyView ).setText( emptyText );
        }
    }


    // ----------------------------------------------------


    @Override
    public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ){
        inflater.inflate( R.menu.conversations_menu, menu );
        super.onCreateOptionsMenu( menu, inflater );
    }


    @Override
    public boolean onOptionsItemSelected( MenuItem item ){

        switch( item.getItemId() ){
            case R.id.menu_add:
                if( mListener != null ) mListener.onSendMessageTo( mFriend );
                return true;

            case android.R.id.home:
                if( mListener != null ) mListener.onBackToFriendsList();
                return true;

        }

        return super.onOptionsItemSelected( item );
    }

}
