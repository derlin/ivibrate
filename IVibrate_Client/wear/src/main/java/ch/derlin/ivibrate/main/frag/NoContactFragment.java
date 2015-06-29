package ch.derlin.ivibrate.main.frag;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.comm.SendToPhoneService;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoContactFragment extends Fragment implements View.OnClickListener{


    public NoContactFragment(){
        // Required empty public constructor
    }


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
        // Inflate the layout for this fragment
        View view =  inflater.inflate( R.layout.fragment_main_no_contact, container, false );

        (( ImageButton)view.findViewById( R.id.open_on_phone )).setOnClickListener( this );
        return view;
    }


    @Override
    public void onClick( View v ){
        SendToPhoneService.askOpenApp( null );
        getActivity().finish();
    }
}
