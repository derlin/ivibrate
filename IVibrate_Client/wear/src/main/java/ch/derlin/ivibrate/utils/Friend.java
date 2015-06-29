package ch.derlin.ivibrate.utils;

import ch.derlin.ivibrate.comm.WearableConstants;
import com.google.android.gms.wearable.DataMap;

import java.io.Serializable;

/**
 * Created by lucy on 24/06/15.
 */
public class Friend implements Serializable{
    private String phone, name;


    public Friend( String phone, String name ){
        this.phone = phone;
        this.name = name;
    }

    public Friend(DataMap map){
        this.phone = map.getString( WearableConstants.EXTRA_PHONE );
        this.name = map.getString( WearableConstants.EXTRA_CONTACT_NAME );
    }


    public String getPhone(){
        return phone;
    }


    public void setPhone( String phone ){
        this.phone = phone;
    }


    public String getName(){
        return name;
    }


    public void setName( String name ){
        this.name = name;
    }
}
