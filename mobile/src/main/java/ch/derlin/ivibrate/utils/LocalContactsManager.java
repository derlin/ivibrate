package ch.derlin.ivibrate.utils;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import ch.derlin.ivibrate.app.App;

/**
 * Created by lucy on 19/06/15.
 */
public class LocalContactsManager{

    public static Long getContactIDFromNumber( String contactNumber ){

        Cursor phone_cursor = App.getAppContext().getContentResolver().query( //
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,   //
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?", //
                new String[]{ contactNumber }, null );

        phone_cursor.moveToFirst();

        if( phone_cursor.isAfterLast() ) return null;

        Long id = phone_cursor.getLong( phone_cursor.getColumnIndex( ContactsContract.PhoneLookup._ID ) );
        phone_cursor.close();
        return id;
    }

    // ----------------------------------------------------


    public static LocalContactDetails getContactDetails( long contactId ){
        // define the columns I want the query to return
        String[] projection = new String[]{ //
                ContactsContract.PhoneLookup.DISPLAY_NAME, //
                ContactsContract.PhoneLookup.PHOTO_URI };

        Cursor cursor = App.getAppContext().getContentResolver().query( //
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, //
                ContactsContract.PhoneLookup._ID + " = ?", //
                new String[]{ "" + contactId }, null );

        cursor.moveToFirst();
        if( cursor.isAfterLast() ) return null;

        LocalContactDetails details = new LocalContactDetails();
        details.name = cursor.getString( cursor.getColumnIndex( ContactsContract.PhoneLookup.DISPLAY_NAME ) );
        String s = cursor.getString( cursor.getColumnIndex( ContactsContract.PhoneLookup.PHOTO_URI ) );
        if( s != null ) details.photoUri = Uri.parse( s );

        cursor.close();
        return details;
    }


    // ----------------------------------------------------

    public static class LocalContactDetails{

        private String name;
        public Uri photoUri;


        public Uri getPhotoUri(){
            return photoUri;
        }


        public String getName(){
            return name;
        }
    }
}
