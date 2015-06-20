package ch.derlin.ivibrate.app;

import com.google.gson.reflect.TypeToken;

/**
 * Created by lucy on 20/06/15.
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
}
