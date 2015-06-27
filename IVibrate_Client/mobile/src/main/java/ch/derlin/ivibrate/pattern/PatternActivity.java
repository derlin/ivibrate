package ch.derlin.ivibrate.pattern;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import ch.derlin.ivibrate.R;

/**
 * Activity used to ask a pattern and an optional text
 * to the user in order to send a message.
 * This activity should be called with {@link #startActivityForResult(Intent, int)}.
 * It returns a status code and a bundle with "pattern" and "text" extras.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class PatternActivity extends ActionBarActivity implements PatternFragment.PatternFragmentCallbacks,
        MessageFragment.MessageFragmentCallbacks{

    Bundle mBundle;
    Intent mResultIntent = new Intent();


    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pattern );

        mBundle = getIntent().getExtras();

        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar_actionbar );
        if(toolbar != null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        }

        setFrag( new PatternFragment() );
    }

    // ----------------------------------------------------

    @Override
    public boolean onOptionsItemSelected( MenuItem item ){
        int id = item.getItemId();

        if( id == R.id.action_settings ){
            return true;

        }else if( id == android.R.id.home ){
            cancel();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    // ----------------------------------------------------

    private void setFrag( Fragment f ){
        getSupportFragmentManager().beginTransaction() //
                .replace( R.id.fragment, f ).commit();
    }


    private void cancel(){
        setResult( RESULT_CANCELED );
        Log.d( getPackageName(), "Pattern activity canceled" );
        finish();
    }

    /* *****************************************************************
     * fragments callbacks
     * ****************************************************************/


    @Override
    public void onPatternValidated( long[] pattern ){
        mResultIntent.putExtra( "pattern", pattern );
        setFrag( new MessageFragment() );
    }


    @Override
    public void onPatternCanceled(){
        cancel();
    }

    // ----------------------------------------------------


    @Override
    public void onMessageCanceled(){
        cancel();
    }


    @Override
    public void onMessageValidated( String message ){
        if( message != null ) mResultIntent.putExtra( "message", message );
        if( mBundle != null ) mResultIntent.putExtras( mBundle );
        setResult( RESULT_OK, mResultIntent );
        finish();
    }
}
