package ch.derlin.ivibrate.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import ch.derlin.ivibrate.sql.entities.Friend;
import ch.derlin.ivibrate.sql.entities.Message;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static ch.derlin.ivibrate.sql.SqlHelper.*;

/**
 * Created by lucy on 19/06/15.
 */
public class SqlDataSource implements AutoCloseable{

    private SQLiteDatabase db;
    private SqlHelper helper;


    // ----------------------------------------------------


    public SqlDataSource( Context context ){
        helper = SqlHelper.getInstance( context );
    }


    public SqlDataSource( Context context, boolean autoOpen ) throws SQLException{
        helper = SqlHelper.getInstance( context );
        if( autoOpen ) this.open();
    }


    public SqlDataSource open() throws SQLException{
        db = helper.getWritableDatabase();
        return this;
    }


    public void close(){
        db.close();
        helper.close();
    }

    /* *****************************************************************
     * Friends
     * ****************************************************************/


    public boolean addFriend( Friend friend ){
        return db.insert( F_TABLE_NAME, null, friendToContentValues( friend ) ) > 0;
    }


    public Map<String, Friend> getFriends(){
        Map<String, Friend> map = new TreeMap<>();

        Cursor cursor = db.query( F_TABLE_NAME, null, null, null, null, null, null );
        cursor.moveToFirst();

        while( !cursor.isAfterLast() ){
            Friend f = cursorToFriend( cursor );
            map.put( f.getPhone(), f );
            cursor.moveToNext();
        }//end while

        cursor.close();

        return map;
    }


    /* *****************************************************************
     * Messages
     * ****************************************************************/
    public boolean addMessage( Message message ){
        return db.insert( P_TABLE_NAME, null, messageToContentValues( message ) ) > 0;
    }


    public List<Message> getMessagesWith( String phone ){
        List<Message> list = new ArrayList<>();

        Cursor cursor = db.query( P_TABLE_NAME, null, P_COL_PHONE + "= ?",  //
                new String[]{ phone }, null, null, P_COL_DATE );

        cursor.moveToFirst();

        while( !cursor.isAfterLast() ){
            list.add( cursorToMessage( cursor ) );
            cursor.moveToNext();
        }

        cursor.close();

        return list;
    }


    public long getMessagesCount( String phone ){
        return DatabaseUtils.queryNumEntries( db, P_TABLE_NAME, //
                P_COL_PHONE + "= ?", new String[]{ phone } );
    }





    /* *****************************************************************
     * private utils
     * ****************************************************************/


    private static ContentValues friendToContentValues( Friend friend ){
        ContentValues values = new ContentValues();
        values.put( F_COL_PHONE, friend.getPhone() );
        return values;
    }


    private static ContentValues messageToContentValues( Message message ){
        ContentValues values = new ContentValues();
        values.put( P_COL_ID, message.getId() );
        values.put( P_COL_PHONE, message.getPhone_contact() );
        values.put( P_COL_PATTERN, message.getPattern() );
        values.put( P_COL_DATE, message.getDate() );
        values.put( P_COL_DIR, message.getDir() );
        return values;
    }


    private static Friend cursorToFriend( Cursor c ){
        Friend f = new Friend();
        f.setPhone( c.getString( c.getColumnIndex( F_COL_PHONE ) ) );
        return f;
    }


    private Message cursorToMessage( Cursor cursor ){
        Message m = new Message();
        m.setId( cursor.getInt( cursor.getColumnIndex( P_COL_ID ) ) );
        m.setDate( cursor.getString( cursor.getColumnIndex( P_COL_DATE ) ) );
        m.setPattern( cursor.getString( cursor.getColumnIndex( P_COL_PATTERN ) ) );
        m.setPhone_contact( cursor.getString( cursor.getColumnIndex( P_COL_PHONE ) ) );
        m.setDir( cursor.getString( cursor.getColumnIndex( P_COL_DIR ) ) );
        return m;
    }

}
