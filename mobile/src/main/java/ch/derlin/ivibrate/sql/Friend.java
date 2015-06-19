package ch.derlin.ivibrate.sql;

import android.util.Log;
import ch.derlin.ivibrate.app.App;
import ch.derlin.ivibrate.utils.LocalContactsManager;
import ch.derlin.ivibrate.utils.LocalContactsManager.LocalContactDetails;

import java.sql.SQLException;

/**
 * Created by lucy on 19/06/15.
 */
public class Friend{

    String phone;
    private long contactId;
    private LocalContactDetails details;
    private boolean isLocalContactLoaded = false;
    private Long messagesCount;

    // ----------------------------------------------------


    public String getPhone(){
        return phone;
    }


    public void setPhone( String phone ){
        this.phone = phone;
    }


    public long getContactId(){
        return contactId;
    }


    public void setContactId( long contactId ){
        this.contactId = contactId;
    }


    public long getMessagesCount(){
        if( messagesCount == null ){
            try( SqlDataSource src = new SqlDataSource( App.getAppContext(), true ) ){
                messagesCount = src.getMessagesCount( phone );
            }catch( SQLException e ){
                Log.d( "SQL", "error retrieving message count for " + this.phone + ": " + e );
            }
        }

        return messagesCount;
    }


    public LocalContactDetails getDetails(){
        if( !isLocalContactLoaded ){
            details = LocalContactsManager.getContactDetails( this.getContactId() );
            isLocalContactLoaded = true;
        }
        return details;
    }


    public void setDetails( LocalContactDetails details ){
        this.details = details;
        isLocalContactLoaded = true;
    }

    // ----------------------------------------------------


}
