package ch.derlin.ivibrate.wear;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import ch.derlin.ivibrate.gcm.GcmConstants;
import ch.derlin.ivibrate.gcm.GcmSenderService;
import ch.derlin.ivibrate.main.MainActivity;
import ch.derlin.ivibrate.sql.SqlDataSource;
import ch.derlin.ivibrate.sql.entities.Friend;
import com.google.android.gms.wearable.*;

import java.sql.SQLException;
import java.util.ArrayList;

import static ch.derlin.ivibrate.wear.WearableConstants.*;


/**
 * Listen to the data changes and handle messages from the
 * wearable.
 * -------------------------------------------------  <br />
 * context      Advanced Interface - IVibrate project <br />
 * date         June 2015                             <br />
 * -------------------------------------------------  <br />
 *
 * @author Lucy Linder
 */
public class ListenToWearableService extends WearableListenerService{

    @Override
    public void onCreate(){
        super.onCreate();
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    // ----------------------------------------------------


    @Override
    public void onDataChanged( DataEventBuffer dataEvents ){
        DataMap dataMap;
        for( DataEvent event : dataEvents ){

            // Check the data type
            if( event.getType() == DataEvent.TYPE_CHANGED ){
                // Check the data path
                dataMap = DataMapItem.fromDataItem( event.getDataItem() ).getDataMap();
                String path = event.getDataItem().getUri().getPath();

                if( path.equals( WEARABLE_TO_PHONE_DATA_PATH ) && dataMap.containsKey( ACTION_KEY ) ){
                    doAction( dataMap.getString( ACTION_KEY ), dataMap );
                }

            }
        }
    }

    // ----------------------------------------------------


    /* handle a given action/message */
    private void doAction( String action, DataMap dataMap ){

        switch( action ){

            case ACTION_FEEDBACK:
                boolean ok = dataMap.getBoolean( EXTRA_STATUS );
                String toast = ok ? "PATTERN PLAYED (ack)" : "Error on the watch...";
                Log.d( getPackageName(), toast );
//                Toast.makeText( getApplicationContext(), toast, Toast.LENGTH_SHORT ).show();
                break;

            case ACTION_GET_CONTACTS:
                SendToWearableService.sendContacts( getContactsList() );
                break;

            case ACTION_SEND_MSG:
                String phone = dataMap.getString( EXTRA_PHONE );
                long[] pattern = dataMap.getLongArray( EXTRA_PATTERN );
                String text = dataMap.getString( EXTRA_TEXT );

                if( phone != null && pattern.length > 0 ){
                    GcmSenderService.sendMessage( phone, pattern, text );
                }
                break;

            case ACTION_OPEN:
                String number = dataMap.getString( EXTRA_PHONE );

                // don't know why, but the extras are only passed with a pendingIntent...
                try{
                    Intent intent = new Intent( getApplicationContext(), MainActivity.class );
                    intent.putExtra( GcmConstants.FROM_KEY, number );
                    PendingIntent pendingIntent = PendingIntent.getActivity( getApplicationContext(), 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT );
                    pendingIntent.send( getApplicationContext(), 0, new Intent() );

                }catch( PendingIntent.CanceledException e ){
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }

    // ----------------------------------------------------


    private ArrayList<Bundle> getContactsList(){

        ArrayList<Bundle> list = new ArrayList<>();

        try( SqlDataSource src = new SqlDataSource( getApplicationContext(), true ) ){
            for( Friend friend : src.getFriends().values() ){
                Bundle dm = new Bundle();
                dm.putString( EXTRA_PHONE, friend.getPhone() );
                dm.putString( EXTRA_CONTACT_NAME, friend.getDisplayName() );
                list.add( dm );
            }//end for

        }catch( SQLException e ){
            Log.d( getPackageName(), "error retrieving friends" );
        }
        return list;
    }
}
