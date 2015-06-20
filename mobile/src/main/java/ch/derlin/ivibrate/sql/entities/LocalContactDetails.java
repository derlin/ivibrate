package ch.derlin.ivibrate.sql.entities;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lucy on 20/06/15.
 */
public class LocalContactDetails implements Parcelable{

    private String name;
    private String phone;
    private Long contactId;
    public Uri photoUri;


    public LocalContactDetails(){}


    public LocalContactDetails( Parcel in ){
        this.name = in.readString();
        this.phone = in.readString();
        this.contactId = ( Long ) in.readValue( Long.class.getClassLoader() );
        this.photoUri = ( Uri ) in.readValue( Uri.class.getClassLoader() );
    }


    public Uri getPhotoUri(){
        return photoUri;
    }


    public String getName(){
        return name;
    }


    public String getPhone(){ return phone; }


    public Long getContactId(){
        return contactId;
    }


    public void setName( String name ){
        this.name = name;
    }


    public void setPhone( String phone ){
        this.phone = phone;
    }


    public void setContactId( Long contactId ){
        this.contactId = contactId;
    }


    public void setPhotoUri( Uri photoUri ){
        this.photoUri = photoUri;
    }

    // ----------------------------------------------------

    @Override
    public int describeContents(){
        return 0;
    }


    @Override
    public void writeToParcel( Parcel dest, int flags ){
        dest.writeString( name );
        dest.writeString( phone );
        dest.writeValue( contactId );
        dest.writeValue( photoUri );
    }

    public static final Parcelable.Creator<LocalContactDetails> CREATOR = new Parcelable.Creator<LocalContactDetails>(){
        @Override
        public LocalContactDetails createFromParcel( Parcel source ){
            return new LocalContactDetails( source );
        }


        @Override
        public LocalContactDetails[] newArray( int size ){
            return new LocalContactDetails[size];
        }
    };
}