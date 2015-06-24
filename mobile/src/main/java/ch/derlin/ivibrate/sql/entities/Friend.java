package ch.derlin.ivibrate.sql.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import ch.derlin.ivibrate.app.App;
import ch.derlin.ivibrate.sql.SqlDataSource;
import ch.derlin.ivibrate.utils.LocalContactsManager;

import java.sql.SQLException;

/**
 * Created by lucy on 19/06/15.
 */
public class Friend implements Parcelable{

    private String phone;
    private boolean isLocalContactLoaded = false;
    private LocalContactDetails details;


    // ----------------------------------------------------


    public Friend(){}


    public Friend( String phone ){
        this.phone = phone;
    }


    public Friend( Parcel in ){
        phone = in.readString();
        isLocalContactLoaded = in.readInt() == 1;
        if( isLocalContactLoaded ){
            details = in.readParcelable( LocalContactDetails.class.getClassLoader() );
        }
    }
    // ----------------------------------------------------


    public String getPhone(){
        return phone;
    }


    public void setPhone( String phone ){
        this.phone = phone;
    }

    public String getDisplayName(){
        LocalContactDetails details = getDetails();
        return details == null ? phone : details.getName();
    }

    public long getMessagesCount(){

        try( SqlDataSource src = new SqlDataSource( App.getAppContext(), true ) ){
            return src.getMessagesCount( phone );
        }catch( SQLException e ){
            Log.d( "SQL", "error retrieving message count for " + this.phone + ": " + e );
        }
        return 0;

    }


    public LocalContactDetails getDetails(){
        if( !isLocalContactLoaded ){
            details = LocalContactsManager.getContactDetails( phone );
            isLocalContactLoaded = true;
        }
        return details;
    }


    public void setDetails( LocalContactDetails details ){
        this.details = details;
        isLocalContactLoaded = true;
    }

    // ----------------------------------------------------


    @Override
    public int describeContents(){
        return 0;
    }


    @Override
    public void writeToParcel( Parcel dest, int flags ){
        dest.writeString( phone );
        dest.writeInt( isLocalContactLoaded ? 1 : 0 );
        if( isLocalContactLoaded ) dest.writeParcelable( details, flags );
    }


    public static final Parcelable.Creator<Friend> CREATOR = new Parcelable.Creator<Friend>(){
        @Override
        public Friend createFromParcel( Parcel source ){
            return new Friend( source );
        }


        @Override
        public Friend[] newArray( int size ){
            return new Friend[ size ];
        }
    };

}
