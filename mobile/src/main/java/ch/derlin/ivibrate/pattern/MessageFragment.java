package ch.derlin.ivibrate.pattern;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import ch.derlin.ivibrate.R;

/**
 * Created by lucy on 23/06/15.
 */
public class MessageFragment extends Fragment implements View.OnClickListener{
    Button mLeftButton, mRightButton;
    EditText mEditText;

    MessageFragmentCallbacks mCallbacks;

    interface MessageFragmentCallbacks{
        void onMessageCanceled();
        void onMessageValidated(String message);
    }


    @Override
    public void onAttach( Activity activity ){
        super.onAttach( activity );
        mCallbacks = ( MessageFragmentCallbacks ) activity;
    }


    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
        View view = inflater.inflate( R.layout.fragment_pattern_message, container, false );

        mLeftButton = ( Button ) view.findViewById( R.id.buttonLeft );
        mLeftButton.setOnClickListener( this );
        mRightButton = ( Button ) view.findViewById( R.id.buttonRight );
        mRightButton.setText( "Send" );
        mRightButton.setOnClickListener( this );

        mEditText = ( EditText ) view.findViewById( R.id.editText );

        return view;
    }


    @Override
    public void onClick( View v ){
        if(v == mLeftButton ){
            // cancel clicked
            mCallbacks.onMessageCanceled();
        }else if(v == mRightButton){
            String text = mEditText.getText().toString();
            mCallbacks.onMessageValidated( text.isEmpty() ? null : text );
        }
    }
}
