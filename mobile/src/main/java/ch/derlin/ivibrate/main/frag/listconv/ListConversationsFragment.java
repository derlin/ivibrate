package ch.derlin.ivibrate.main.frag.listconv;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.sql.Friend;
import ch.derlin.ivibrate.sql.SqlDataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by lucy on 19/06/15.
 */
public class ListConversationsFragment extends Fragment{

    ListView mList;
    ListConvAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
        View view = inflater.inflate( R.layout.fragment_main_listconv, container, false );

        mList = ( ListView ) view.findViewById( R.id.listView );

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
}
