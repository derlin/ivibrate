package ch.derlin.ivibrate.main.frag;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ch.derlin.ivibrate.R;

/**
 * Created by lucy on 24/06/15.
 */
public class WaitFragment extends Fragment{

    public static WaitFragment getInstance(String text){
        WaitFragment f = new WaitFragment();
        if(text != null){
            Bundle bundle = new Bundle(  );
            bundle.putString( "text", text );
            f.setArguments( bundle );
        }
        return f;
    }


    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
        View view = inflater.inflate( R.layout.fragment_main_wait, container, false );
        Bundle extras = getArguments();
        if( extras != null && extras.containsKey( "text" ) ){
            ( ( TextView ) view.findViewById( R.id.textView ) ).setText( extras.getString( "text" ) );
        }
        return view;
    }
}
