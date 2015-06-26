package ch.derlin.ivibrate.main.frag;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.utils.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucy on 24/06/15.
 */
public class ContactsFragment extends Fragment implements WearableListView.ClickListener{

    private ContactsFragmentCallbacks mCallbacks;
    private ContactsAdapter mAdapter;

    public interface ContactsFragmentCallbacks{
        void onPhoneSelected( String phone );
    }


    @Override
    public void onAttach( Activity activity ){
        super.onAttach( activity );
        mCallbacks = ( ContactsFragmentCallbacks ) activity;
    }


    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
        View view = inflater.inflate( R.layout.fragment_main_contacts, container, false );
        List<Friend> contacts = ( ArrayList<Friend> ) getArguments().getSerializable( "contacts" );

        WearableListView listView = ( WearableListView ) view.findViewById( R.id.wearable_list );
        mAdapter = new ContactsAdapter( getActivity(), contacts );
        listView.setAdapter( mAdapter );
        listView.setClickListener( this );
        return view;
    }


    @Override
    public void onClick( WearableListView.ViewHolder viewHolder ){
        Integer position = ( Integer ) viewHolder.itemView.getTag();
        mCallbacks.onPhoneSelected( mAdapter.getPhone( position ) );
    }


    @Override
    public void onTopEmptyRegionClick(){

    }
}
