package ch.derlin.ivibrate.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import ch.derlin.ivibrate.main.frag.NotificationFragment;
import ch.derlin.ivibrate.R;
import ch.derlin.ivibrate.main.drawer.NavigationDrawerCallbacks;
import ch.derlin.ivibrate.main.drawer.NavigationDrawerFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements NavigationDrawerCallbacks{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    private List<Fragment> fragments = new ArrayList<>(  );

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );

        // Set up the fragments before instantiating the drawer
        fragments.add( new NotificationFragment() );

        setContentView( R.layout.activity_main );
        mToolbar = ( Toolbar ) findViewById( R.id.toolbar_actionbar );
        setSupportActionBar( mToolbar );



        // Set up the drawer.
        mNavigationDrawerFragment = ( NavigationDrawerFragment ) getFragmentManager().findFragmentById( R.id.fragment_drawer );
        mNavigationDrawerFragment.setup( R.id.fragment_drawer, ( DrawerLayout ) findViewById( R.id.drawer ), mToolbar );


    }


    private void setFragment(Fragment f){
        getSupportFragmentManager().beginTransaction() //
                .replace( R.id.container, f )  //
                .commit();
    }

    @Override
    public void onNavigationDrawerItemSelected( int position ){
        // update the main content by replacing fragments
        Toast.makeText( this, "Menu item selected -> " + position, Toast.LENGTH_SHORT ).show();
        if(position < fragments.size()){
            setFragment( fragments.get( position ) );
        }
    }


    @Override
    public void onBackPressed(){
        if( mNavigationDrawerFragment.isDrawerOpen() ){
            mNavigationDrawerFragment.closeDrawer();
        }else{
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu( Menu menu ){
        if( !mNavigationDrawerFragment.isDrawerOpen() ){
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate( R.menu.main, menu );
            return true;
        }
        return super.onCreateOptionsMenu( menu );
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


}
