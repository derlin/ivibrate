package ch.derlin.ivibrate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class PatternActivity extends ActionBarActivity implements View.OnTouchListener, View.OnClickListener{

    View patternView;
    Button cancelButton, nextButton;
    List<Long> pattern = new ArrayList<>();
    long lastTouch;
    Bundle bundle;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pattern );
        patternView = findViewById( R.id.pattern_view );
        cancelButton = ( Button ) findViewById( R.id.buttonLeft );
        nextButton = ( Button ) findViewById( R.id.buttonRight );

        bundle = getIntent().getExtras();

        patternView.setOnTouchListener( this );
        cancelButton.setOnClickListener( this );
        nextButton.setOnClickListener( this );

        setSupportActionBar( ( Toolbar ) findViewById( R.id.toolbar_actionbar ) );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
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
        }

        return super.onOptionsItemSelected( item );
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


    @Override
    public void onClick( View v ){
        if( v == cancelButton || pattern.isEmpty() ){
            setResult( RESULT_CANCELED );
            Log.d( this.getPackageName(), "Pattern activity canceled" );

        }else{
            Intent intent = new Intent();
            intent.putExtra( "pattern", patternToPrimitiveArray() );
            if(bundle != null) intent.putExtras( bundle );
            setResult( RESULT_OK, intent );

        }
        finish();
    }


    private long[] patternToPrimitiveArray(){
        long[] p = new long[ pattern.size() + 1 ];
        StringBuffer buf = new StringBuffer( "pattern: " );
        p[ 0 ] = 0; // start immediately
        for( int i = 1; i < p.length; i++ ){
            p[ i ] = pattern.get( i - 1 );
            buf.append( p[ i ] ).append( " " );
        }//end for

        Log.d( this.getPackageName(), buf.toString() + "." );
        return p;
    }
}
