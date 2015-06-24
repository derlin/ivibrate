package ch.derlin.ivibrate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import ch.derlin.ivibrate.main.Friend;
import ch.derlin.ivibrate.main.MainActivity;
import com.google.android.gms.wearable.*;

import java.util.ArrayList;

/**
 * Created by michaelHahn on 1/16/15.
 * Listener service or data events on the data layer
 */
public class ListenerService extends WearableListenerService{

    private static final int INDEX_IN_PATTERN_TO_REPEAT = -1; // -1: don't repeatr


    @Override
    public void onDataChanged( DataEventBuffer dataEvents ){

        Log.d( "wearable", "listener called" );
        DataMap dataMap;

        for( DataEvent event : dataEvents ){

            // Check the data type
            if( event.getType() == DataEvent.TYPE_CHANGED ){
                // Check the data path
                dataMap = DataMapItem.fromDataItem( event.getDataItem() ).getDataMap();
                String path = event.getDataItem().getUri().getPath();
                if( path.equals( WearableConstants.PHONE_TO_WEARABLE_DATA_PATH ) ){

                    if( dataMap.containsKey( "pattern" ) ){
                        playPattern( dataMap.getLongArray( "pattern" ) );

                    }else if( dataMap.containsKey( "contacts" ) ){
                        ArrayList<DataMap> maps = dataMap.getDataMapArrayList( "contacts" );
                        ArrayList<Friend> contacts = new ArrayList<>(  );
                        for( DataMap map : maps ){
                            contacts.add( new Friend( map ) );
                        }//end for
                        Bundle bundle = new Bundle();
                        bundle.putSerializable( "contacts", contacts );
                        Intent intent = new Intent( this, MainActivity.class );
                        intent.putExtras( bundle );
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity( intent );
                    }
                }

                Log.v( "wearable", "DataMap received on watch: " + dataMap );

            }else{
                SendToPhoneService.getInstance().sendStatus( false );
                Log.d( "wearable", "event type " + event.getType() );

            }
        }
    }


    private void playPattern( long[] pattern ){
        if( pattern == null ) return;
        Vibrator vibrator = ( Vibrator ) getSystemService( VIBRATOR_SERVICE );
        vibrator.vibrate( pattern, INDEX_IN_PATTERN_TO_REPEAT );
        Log.d( "wearable", "pattern received and played" );

        SendToPhoneService.getInstance().sendStatus( true );
    }
}
