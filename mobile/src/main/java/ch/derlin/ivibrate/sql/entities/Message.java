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
    String phoneContact;
    long[] pattern;
    String text;
    Date date;
    String dir;
    boolean isAcked;

    // ----------------------------------------------------


    public Long getId(){
        return id;
    }


    public void setId( Long id ){
        this.id = id;
    }


    public String getPhoneContact(){
        return phoneContact;
    }


    public void setPhoneContact( String phoneContact ){
        this.phoneContact = phoneContact;
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


    public String getText(){
        return text;
    }


    public void setText( String text ){
        this.text = text;
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


    public boolean getIsAcked(){
        return isAcked;
    }


    public void setIsAcked( boolean isAcked ){
        this.isAcked = isAcked;
    }

    // ----------------------------------------------------


    public static Message createSentInstance( String to, long[] pattern, String text ){
        Message m = create( to, pattern, text );
        m.dir = SENT_MSG;
        return m;
    }


    public static Message createReceivedInstance( String from, long[] pattern, String text ){
        Message m = create( from, pattern, text );
        m.dir = RECEIVED_MSG;
        return m;
    }


    private static Message create( String friend, long[] pattern, String text ){
        Message m = new Message();
        m.phoneContact = friend;
        m.date = new Date();
        m.pattern = pattern;
        m.text = text;

        return m;
    }
}
