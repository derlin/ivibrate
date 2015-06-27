package ch.derlin.ivibrate.main.frag;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import ch.derlin.ivibrate.R;

/**
 * Created by lucy on 24/06/15.
 */
public class TextFragment extends Fragment implements View.OnClickListener{

    private ImageButton mCancelButton, mNextButton;
    private ImageButton mSpeechButton;
    private TextFragmentCallbacks mCallbacks;

    // ----------------------------------------------------

    public interface TextFragmentCallbacks{
        void onNoText();

        void onGetText();

        void onTextCanceled();
    }

    // ----------------------------------------------------


    @Override
    public void onAttach( Activity activity ){
        super.onAttach( activity );
        mCallbacks = ( TextFragmentCallbacks ) getActivity();
    }


    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
        View view = inflater.inflate( R.layout.fragment_main_text, container, false );
        mCancelButton = ( ImageButton ) view.findViewById( R.id.cancelButton );
        mNextButton = ( ImageButton ) view.findViewById( R.id.okButton );
        mSpeechButton = ( ImageButton ) view.findViewById( R.id.speechButton );

        mCancelButton.setOnClickListener( this );
        mNextButton.setOnClickListener( this );
        mSpeechButton.setOnClickListener( this );

        return view;
    }


    @Override
    public void onClick( View v ){
        if( v == mCancelButton ){
            mCallbacks.onTextCanceled();

        }else if( v == mSpeechButton ){
            mCallbacks.onGetText();

        }else{
            mCallbacks.onNoText();
        }
    }

}
