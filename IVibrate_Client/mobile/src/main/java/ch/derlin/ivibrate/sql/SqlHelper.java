package ch.derlin.ivibrate.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Class handling the creation/update of the db.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class SqlHelper extends SQLiteOpenHelper{

    // database
    public static final String DB_NAME = "ivibrate.db";
    public static final int DB_VERSION = 1;


    public static final String F_TABLE_NAME = "friends";
    public static final String F_COL_PHONE = "phone";

    //    public static final String[] F_ALL_COLS = new String[]{   //
    //        F_COL_PHONE, F_COL_CONTACT_ID
    //    };

    public static final String P_COL_ID = "id";
    public static final String P_TABLE_NAME = "patterns";
    public static final String P_COL_PHONE = "friend_phone";
    public static final String P_COL_PATTERN = "pattern";
    public static final String P_COL_TEXT = "text";
    public static final String P_COL_DATE = "date";
    public static final String P_COL_DIR = "direction";
    public static final String P_COL_IS_ACKED = "is_acked";


    private static final String CREATE_FRIENDS_TABLE = String.format(  //
            "CREATE TABLE %s(" +   //
                    "%s TEXT NOT NULL PRIMARY KEY );", //
            F_TABLE_NAME, F_COL_PHONE );

    private static final String CREATE_PATTERNS_TABLE = String.format(  //
            "CREATE TABLE %s(" +   //
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT," + // id
                    "%s TEXT NOT NULL, " + //  phone
                    "%s TEXT NOT NULL, " + //  pattern
                    "%s TEXT         , " + //  text
                    "%s TEXT NOT NULL, " + //  date
                    "%s TEXT NOT NULL, " + //  dir
                    "%s INT DEFAULT 0 " +  // isAcked
                    ");", //
            P_TABLE_NAME, P_COL_ID, P_COL_PHONE, P_COL_PATTERN, P_COL_TEXT, P_COL_DATE, P_COL_DIR, P_COL_IS_ACKED );


    // ----------------------------------------------------
    private static SqlHelper INSTANCE;


    public static synchronized SqlHelper getInstance( Context context ){
        if( INSTANCE == null ) INSTANCE = new SqlHelper( context );
        return INSTANCE;
    }
    // ----------------------------------------------------


    private SqlHelper( Context context ){
        super( context, DB_NAME, null, DB_VERSION );
    }


    @Override
    public void onCreate( SQLiteDatabase db ){
        db.execSQL( CREATE_FRIENDS_TABLE );
        db.execSQL( CREATE_PATTERNS_TABLE );
    }


    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ){
        Log.w( SqlHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", " +
                "which will destroy all old data" );

        db.execSQL( "DROP TABLE IF EXISTS " + F_TABLE_NAME );
        db.execSQL( "DROP TABLE IF EXISTS " + P_TABLE_NAME );
        onCreate( db );
    }
}
