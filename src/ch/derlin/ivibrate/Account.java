package ch.derlin.ivibrate;

/**
 * @author: Lucy Linder
 * @date: 19.06.2015
 */
public class Account{
    String regId;
    String name;
    String phoneNumber;


    public String getRegId(){
        return regId;
    }


    public void setRegId( String regId ){
        this.regId = regId;
    }


    public String getName(){
        return name;
    }


    public void setName( String name ){
        this.name = name;
    }


    public String getPhoneNumber(){
        return phoneNumber;
    }


    public void setPhoneNumber( String phoneNumber ){
        this.phoneNumber = phoneNumber;
    }
}//end class
