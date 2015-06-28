package ch.derlin.ivibrate.wear;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import ch.derlin.ivibrate.gcm.GcmConstants;
import ch.derlin.ivibrate.gcm.GcmSenderService;
import ch.derlin.ivibrate.main.MainActivity;
import ch.derlin.ivibrate.sql.SqlDataSource;
import ch.derlin.ivibrate.sql.entities.Friend;
import com.google.android.gms.wearable.*;

import java.sql.SQLException;
import java.util.ArrayList;

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
    public void onDataChanged( DataEventBuffer dataEvents ){
        DataMap dataMap;
        for( DataEvent event : dataEvents ){

            // Check the data type
            if( event.getType() == DataEvent.TYPE_CHANGED ){
                // Check the data path
                dataMap = DataMapItem.fromDataItem( event.getDataItem() ).getDataMap();
                String path = event.getDataItem().getUri().getPath();

                if( path.equals( WearableConstants.WEARABLE_TO_PHONE_DATA_PATH ) ){
                    if( dataMap.containsKey( "result" ) ){
                        Toast.makeText( getApplicationContext(), "PATTERN PLAYED (ack)", Toast.LENGTH_SHORT ).show();

                    }else if( dataMap.containsKey( "action" ) ){
                        doAction( dataMap.getString( "action" ), dataMap );
                    }
                }

            }
        }
    }


    /* handle a given action/message */
    private void doAction( String action, DataMap dataMap ){

        if( action.equals( "send" ) ){
            String phone = dataMap.getString( "phone" );
            long[] pattern = dataMap.getLongArray( "pattern" );
            String text = dataMap.getString( "text" );

            if( phone != null && pattern.length > 0 ){
                GcmSenderService.sendMessage( phone, pattern, text );
            }

        }else if( action.equals( "getContacts" ) ){
            try( SqlDataSource src = new SqlDataSource( getApplicationContext(), true ) ){
                ArrayList<DataMap> list = new ArrayList<>();
                for( Friend friend : src.getFriends().values() ){
                    DataMap dm = new DataMap();
                    dm.putString( "phone", friend.getPhone() );
                    dm.putString( "name", friend.getDisplayName() );
                    list.add( dm );
                }//end for
                SendToWearableService.getInstance().sendContacts( list );
            }catch( SQLException e ){
                Log.d( getPackageName(), "error retrieving friends" );
            }
        }else if( action.equals( "open" ) ){
            String phone = dataMap.getString( "phone" );

            if( phone != null ){
                // don't know why, but the extras are only passed with a pendingIntent...
                try{
                    Intent intent = new Intent( getApplicationContext(), MainActivity.class );
                    intent.putExtra( GcmConstants.FROM_KEY, phone );
                    PendingIntent pendingIntent = PendingIntent.getActivity( getApplicationContext(), 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT );
                    pendingIntent.send( getApplicationContext(), 0, new Intent() );

                }catch( PendingIntent.CanceledException e ){
                    e.printStackTrace();
                }
            }
        }
    }
}
