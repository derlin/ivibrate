package ch.derlin.ivibrate.app;

import com.google.gson.reflect.TypeToken;

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
}
