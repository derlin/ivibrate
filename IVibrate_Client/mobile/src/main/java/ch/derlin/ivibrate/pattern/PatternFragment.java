package ch.derlin.ivibrate.pattern;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.app.App;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used prompt the user for a pattern.
 * The activity should implement {@link ch.derlin.ivibrate.pattern.PatternFragment.PatternFragmentCallbacks}
 * to communicate.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class PatternFragment extends Fragment implements View.OnTouchListener, View.OnClickListener{

    View patternView;
    Button cancelButton, nextButton;
    List<Long> pattern = new ArrayList<>();
    long lastTouch;
    PatternFragmentCallbacks mCallbacks;

    // ----------------------------------------------------

    interface PatternFragmentCallbacks{
        void onPatternValidated( long[] pattern );

        void onPatternCanceled();
    }

    // ----------------------------------------------------


    @Override
    public void onAttach( Activity activity ){
        super.onAttach( activity );
        mCallbacks = ( PatternFragmentCallbacks ) getActivity();
    }


    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
        View view = inflater.inflate( R.layout.fragment_pattern, container, false );
        super.onCreate( savedInstanceState );
        patternView = view.findViewById( R.id.pattern_view );
        cancelButton = ( Button ) view.findViewById( R.id.buttonLeft );
        nextButton = ( Button ) view.findViewById( R.id.buttonRight );

        patternView.setOnTouchListener( this );
        cancelButton.setOnClickListener( this );
        nextButton.setOnClickListener( this );

        return view;
    }

    /* *****************************************************************
     * listeners
     * ****************************************************************/


    @Override
    public void onClick( View v ){
        if( v == cancelButton || pattern.isEmpty() ){
            mCallbacks.onPatternCanceled();

        }else{
            mCallbacks.onPatternValidated( patternToPrimitiveArray() );
        }
    }


    @Override
    public boolean onTouch( View v, MotionEvent event ){

        switch( event.getAction() ){
            case MotionEvent.ACTION_DOWN:
                setPatternBg( android.R.color.holo_orange_light );
                addPattern();
                break;
            case MotionEvent.ACTION_UP:
                setPatternBg( R.color.myTextPrimaryColor );
                addPattern();
                break;
            default:
                break;
        }

        return true;
    }

    /* *****************************************************************
     * private utils
     * ****************************************************************/


    private void setPatternBg( int colorId ){
        patternView.setBackgroundColor( getResources().getColor( colorId ) );
    }


    private void addPattern(){
        long time = System.currentTimeMillis();
        if( lastTouch > 0 ){
            pattern.add( time - lastTouch );
        }
        lastTouch = time;
    }


    private long[] patternToPrimitiveArray(){

        long[] p = new long[ pattern.size() + 1 ];

        p[ 0 ] = 0; // start immediately
        for( int i = 1; i < p.length; i++ ){
            p[ i ] = pattern.get( i - 1 );
        }//end for

        Log.d( getActivity().getPackageName(), App.getGson().toJson( p ) + "." );

        return p;
    }

}

