package ch.derlin.ivibrate.main.frag;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import ch.derlin.ivibrate.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucy on 24/06/15.
 */
public class PatternFragment extends Fragment implements View.OnTouchListener, View.OnClickListener{

    private ImageButton mCancelButton, mNextButton;
    private ImageView mImageView;
    private List<Long> pattern = new ArrayList<>();
    private long lastTouch;
    private PatternFragmentCallbacks mCallbacks;

    // ----------------------------------------------------

    public interface PatternFragmentCallbacks{
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
        View view = inflater.inflate( R.layout.fragment_main_pattern, container, false );
        mCancelButton = ( ImageButton ) view.findViewById( R.id.cancelButton );
        mNextButton = ( ImageButton ) view.findViewById( R.id.okButton );
        mImageView = ( ImageView ) view.findViewById( R.id.patternView );

        mImageView.setOnTouchListener( this );
        mCancelButton.setOnClickListener( this );
        mNextButton.setOnClickListener( this );

        return view;
    }



    @Override
    public boolean onTouch( View v, MotionEvent event ){

        switch( event.getAction() ){
            case MotionEvent.ACTION_DOWN:
                setPatternBg( R.drawable.pattern_orange );//R.drawable.orange_circle);// android.R.color
                // .holo_orange_light );
                addPattern();
                break;
            case MotionEvent.ACTION_UP:
                setPatternBg( R.drawable.pattern_white );//R.drawable.red_circle );//R.color
                // .myTextPrimaryColor );
                addPattern();
                break;
            default:
                break;
        }

        return true;
    }


    private void setPatternBg( int resId ){
        mImageView.setImageResource( resId );
    }


    private void addPattern(){
        long time = System.currentTimeMillis();
        if( lastTouch > 0 ){
            pattern.add( time - lastTouch );
        }
        lastTouch = time;
    }


    @Override
    public void onClick( View v ){
        if( v == mCancelButton || pattern.isEmpty() ){
            mCallbacks.onPatternCanceled();

        }else{
            mCallbacks.onPatternValidated( patternToPrimitiveArray() );
        }
    }


    private long[] patternToPrimitiveArray(){
        long[] p = new long[ pattern.size() + 1 ];
        StringBuilder buf = new StringBuilder( "pattern: " );
        p[ 0 ] = 0; // start immediately
        for( int i = 1; i < p.length; i++ ){
            p[ i ] = pattern.get( i - 1 );
            buf.append( p[ i ] ).append( " " );
        }//end for

        Log.d( getActivity().getPackageName(), buf.toString() + "." );
        return p;
    }
}
