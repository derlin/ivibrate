package ch.derlin.ivibrate;

import android.os.Vibrator;
import android.util.Log;
import com.google.android.gms.wearable.*;

/**
 * Created by michaelHahn on 1/16/15.
 * Listener service or data events on the data layer
 */
public class ListenerService extends WearableListenerService{

    private static final String WEARABLE_DATA_PATH = "/derlin/ivibrate/";;


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

                if( path.equals( WEARABLE_DATA_PATH ) && dataMap.containsKey( "pattern" ) ){
                    Vibrator vibrator = ( Vibrator ) getSystemService( VIBRATOR_SERVICE );
                    long[] pattern = dataMap.getLongArray( "pattern" );

                    //-1 - don't repeat
                    final int indexInPatternToRepeat = -1;
                    vibrator.vibrate( pattern, indexInPatternToRepeat );
                    Log.d( "wearable", "pattern received and played" );
                }

                Log.v( "wearable", "DataMap received on watch: " + dataMap );
            }else{
                Log.d( "wearable", "event type " + event.getType() );

            }
        }
    }
}
