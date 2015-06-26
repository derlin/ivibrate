package ch.derlin.ivibrate.app;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import ch.derlin.ivibrate.sql.SqlDataSource;
import ch.derlin.ivibrate.sql.entities.Friend;
import com.google.gson.reflect.TypeToken;

import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Utility class.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class AppUtils{

    public static long[] getPatternFromString( String json ){
        try{
            return ( long[] ) App.getGson().fromJson( json,  //
                    new TypeToken<long[]>(){}.getType() );
        }catch( Exception e ){
            return null;
        }
    }


    public static class LoadFriendAsyncTask extends AsyncTask<Void, Void, Map<String, Friend>>{

        private final Context context;


        public LoadFriendAsyncTask( Context context ){
            this.context = context;
        }


        @Override
        protected Map<String, Friend> doInBackground( Void... params ){

            try( SqlDataSource src = new SqlDataSource( context, true ) ){
                return src.getFriends();
            }catch( SQLException e ){
                Log.d( App.getAppContext().getPackageName(), "Error retrieving data: " + e );
            }

            return new TreeMap<>();
        }
    }
}
