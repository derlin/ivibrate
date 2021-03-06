package ch.derlin.ivibrate.sql;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import ch.derlin.ivibrate.app.App;
import ch.derlin.ivibrate.sql.entities.LocalContactDetails;

import java.util.ArrayList;
import java.util.List;

 /**
  * Utility calss to query the system database and retrieve
  * information about local contacts.
  * -------------------------------------------------  <br />
  * context      Advanced Interface - IVibrate project <br />
  * date         June 2015                             <br />
  * -------------------------------------------------  <br />
  *
  * @author Lucy Linder
  */
public class LocalContactsManager{


    public static List<LocalContactDetails> getAvailableContacts( String[] phones ){
        List<LocalContactDetails> list = new ArrayList<>();
        for( String phone : phones ){
            LocalContactDetails details = getContactDetails( phone );
            if( details != null ){
                list.add( details );
                Log.d( "LocalContactsManager", "available contact: " + details.getName() );
            }
        }//end for
        return list;
    }

    // ----------------------------------------------------


    public static LocalContactDetails getContactDetails( String number ){

        // define the columns I want the query to return
        String[] projection = new String[]{                 //
                ContactsContract.PhoneLookup.DISPLAY_NAME, //
                ContactsContract.PhoneLookup._ID, //
                ContactsContract.PhoneLookup.PHOTO_URI };

        // encode the phone number and build the filter URI
        Uri contactUri = Uri.withAppendedPath( ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode( number ) );

        // query time
        Cursor cursor = App.getAppContext().getContentResolver().query( contactUri, projection, null, null, null );

        if( cursor.moveToFirst() ){

            LocalContactDetails details = new LocalContactDetails();
            // Get values from contacts database:
            details.setPhone( number );
            details.setContactId( cursor.getLong( cursor.getColumnIndex( ContactsContract.PhoneLookup._ID ) ) );
            details.setName( cursor.getString( cursor.getColumnIndex( ContactsContract.PhoneLookup.DISPLAY_NAME ) ) );
            String s = cursor.getString( cursor.getColumnIndex( ContactsContract.PhoneLookup.PHOTO_URI ) );
            if( s != null ) details.setPhotoUri( Uri.parse( s ) );
            //            Uri uri = ContentUris.withAppendedId( ContactsContract.Contacts.CONTENT_URI, details
            // .contactId );

            return details;

        }else{
            Log.v( "IVibrate", "Started uploadcontactphoto: Contact Not Found @ " + number );
            return null; // contact not found
        }
    }

}
