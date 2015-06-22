package ch.derlin.ivibrate.sql.entities;

import ch.derlin.ivibrate.app.App;
import ch.derlin.ivibrate.app.AppUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lucy on 19/06/15.
 */
public class Message{

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "dd.MM.yy HH:mm" );
    public static final String SENT_MSG = "sent";
    public static final String RECEIVED_MSG = "received";

    Long id;
    String phone_contact;
    long[] pattern;
    Date date;
    String dir;

    // ----------------------------------------------------


    public Long getId(){
        return id;
    }


    public void setId( Long id ){
        this.id = id;
    }


    public String getPhone_contact(){
        return phone_contact;
    }


    public void setPhone_contact( String phone_contact ){
        this.phone_contact = phone_contact;
    }


    public String getPattern(){
        return App.getGson().toJson( pattern );
    }


    public long[] getPatternObject(){
        return pattern;
    }


    public void setPattern( String pattern ){
        this.pattern = AppUtils.getPatternFromString( pattern );
    }


    public String getDate(){
        return DATE_FORMAT.format( this.date );
    }


    public Date getDateObject(){
        return this.date;
    }


    public void setDate( Date date ){
        this.date = date;
    }


    public void setDate( String date ){
        try{
            this.date = DATE_FORMAT.parse( date );
        }catch( ParseException e ){
            e.printStackTrace();
        }
    }


    public String getDir(){
        return dir;
    }


    public void setDir( String dir ){
        this.dir = dir;
    }

    // ----------------------------------------------------


    public static Message createSentInstance( String to, long[] pattern ){
        Message m = create( to, pattern );
        m.dir = SENT_MSG;
        return m;
    }


    public static Message createReceivedInstance( String from, long[] pattern ){
        Message m = create( from, pattern );
        m.dir = RECEIVED_MSG;
        return m;
    }


    private static Message create( String friend, long[] pattern ){
        Message m = new Message();
        m.phone_contact = friend;
        m.date = new Date();
        m.pattern = pattern;

        return m;
    }
}
