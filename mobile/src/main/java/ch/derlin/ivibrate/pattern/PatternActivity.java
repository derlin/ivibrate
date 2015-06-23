package ch.derlin.ivibrate.pattern;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import ch.derlin.ivibrate.R;

public class PatternActivity extends ActionBarActivity implements PatternFragment.PatternFragmentCallbacks,
        MessageFragment.MessageFragmentCallbacks{

    Bundle mBundle;
    Intent mResultIntent = new Intent();


    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pattern );

        mBundle = getIntent().getExtras();

        setSupportActionBar( ( Toolbar ) findViewById( R.id.toolbar_actionbar ) );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        setFrag( new PatternFragment() );
    }


    @Override
    public boolean onOptionsItemSelected( MenuItem item ){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if( id == R.id.action_settings ){
            return true;
        }else if( id == android.R.id.home ){
            cancel();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }


    private void setFrag( Fragment f ){
        getSupportFragmentManager().beginTransaction() //
                .replace( R.id.fragment, f ).commit();
    }


    private void cancel(){
        setResult( RESULT_CANCELED );
        Log.d( getPackageName(), "Pattern activity canceled" );
        finish();
    }
    // ----------------------------------------------------


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
