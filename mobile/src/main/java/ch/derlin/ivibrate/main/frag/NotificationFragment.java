package ch.derlin.ivibrate.main.frag;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import ch.derlin.ivibrate.PatternActivity;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.wear.ActionCallbacks;
import ch.derlin.ivibrate.wear.SendToWearableService;

import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment implements View.OnClickListener{

    Button mSosButton, mPatternButton;

    public static final int PATTERN_REQUEST_CODE = 1984;

    private ActionCallbacks mCallbacks = new ActionCallbacks(){

        @Override
        public void onFail( String errorMsg ){
            Toast.makeText( getActivity(), errorMsg, Toast.LENGTH_LONG ).show();
            mCallbacks.unregisterSelf( getActivity() );
        }


        @Override
        public void onSuccess( String details ){
            Toast.makeText( getActivity(), "Pattern sent to " + details, Toast.LENGTH_SHORT ).show();
            mCallbacks.unregisterSelf( getActivity() );
        }
    };

    public NotificationFragment(){
        // Required empty public constructor
    }


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
        View view = inflater.inflate( R.layout.fragment_send_vibe, container, false );

        mSosButton = ( Button ) view.findViewById( R.id.button_sos_notification );
        mPatternButton = ( Button ) view.findViewById( R.id.button_pattern );
        mSosButton.setOnClickListener( this );
        mPatternButton.setOnClickListener( this );
        return view;
    }




    @Override
    public void onClick( View v ){
        if( v == mSosButton ){
            mCallbacks.registerSelf( getActivity() );
            SendToWearableService.getInstance().sendPattern( getSosPattern() );

        }else if( v == mPatternButton ){
            Intent i = new Intent( getActivity(), PatternActivity.class );
            startActivityForResult( i, PATTERN_REQUEST_CODE );
        }
    }


    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data ){
        if( requestCode == PATTERN_REQUEST_CODE ){
            if( resultCode == Activity.RESULT_OK ){
                long[] pattern = data.getLongArrayExtra( "pattern" );
                Toast.makeText( getActivity(), "PATTERN RESULT : " + Arrays.toString( pattern ), Toast.LENGTH_LONG )
                        .show();
                mCallbacks.registerSelf( getActivity() );
                SendToWearableService.getInstance().sendPattern( pattern );
            }else{
                Toast.makeText( getActivity(), "PATTERN ACTIVITY CANCELED.", Toast.LENGTH_SHORT ).show();
            }

        }else{
            super.onActivityResult( requestCode, resultCode, data );
        }
    }

    /* *****************************************************************
     * sos pattern
     * ****************************************************************/

    public static long[] sosPattern;


    public static long[] getSosPattern(){
        if( sosPattern == null ){
            int dot = 200;      // Length of a Morse Code "dot" in milliseconds

            int dash = 500;     // Length of a Morse Code "dash" in milliseconds
            int short_gap = 200;    // Length of Gap Between dots/dashes
            int medium_gap = 500;   // Length of Gap Between Letters
            int long_gap = 1000;    // Length of Gap Between Words
            sosPattern = new long[]{ 0,  // Start immediately
                    dot, short_gap, dot, short_gap, dot,    // s
                    medium_gap, dash, short_gap, dash, short_gap, dash, // o
                    medium_gap, dot, short_gap, dot, short_gap, dot,    // s
                    long_gap };
        }
        return sosPattern;

    }
}
