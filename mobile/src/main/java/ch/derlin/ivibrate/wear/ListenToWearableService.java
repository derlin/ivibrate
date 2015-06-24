package ch.derlin.ivibrate.wear;

import android.util.Log;
import android.widget.Toast;
import ch.derlin.ivibrate.app.App;
import ch.derlin.ivibrate.gcm.GcmCallbacks;
import ch.derlin.ivibrate.gcm.GcmSenderService;
import ch.derlin.ivibrate.sql.SqlDataSource;
import ch.derlin.ivibrate.sql.entities.Friend;
import ch.derlin.ivibrate.sql.entities.LocalContactDetails;
import ch.derlin.ivibrate.utils.LocalContactsManager;
import com.google.android.gms.wearable.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static ch.derlin.ivibrate.utils.LocalContactsManager.getAvailableContacts;

/**
 * Created by lucy on 24/06/15.
 */
public class ListenToWearableService extends WearableListenerService{
    boolean mAccountsPending = false;
    ArrayList<DataMap> mAvailableContacts;

    private GcmCallbacks mGcmCallbacks = new GcmCallbacks(){

        @Override
        public void onAccountsReceived( String[] accounts ){
            if( mAvailableContacts != null ) return;
            mAvailableContacts = new ArrayList<>();

            List<LocalContactDetails> availableContacts = getAvailableContacts( accounts );

            for( LocalContactDetails contact : availableContacts ){
                DataMap map = new DataMap();
                map.putString( "name", contact.getName() );
                map.putString( "phone", contact.getPhone() );
                mAvailableContacts.add( map );
            }//end for

            if( mAccountsPending ){
                // TODO
                Log.d( getPackageName(), App.getGson().toJson( mAvailableContacts ) );
                mAccountsPending = false;
                SendToWearableService.getInstance().sendContacts( mAvailableContacts );
            }
        }


        @Override
        public void onNewRegistration( String phone ){
            if( mAvailableContacts != null ){
                LocalContactDetails details = LocalContactsManager.getContactDetails( phone );
                if( details != null ){
                    DataMap map = new DataMap();
                    map.putString( "name", details.getName() );
                    map.putString( "phone", details.getPhone() );
                    mAvailableContacts.add( map );
                }
            }
            Toast.makeText( getApplicationContext(), "New registration: " + phone, Toast.LENGTH_SHORT ).show();
        }

    };


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


    private void doAction( String action, DataMap dataMap ){

        if( action.equals( "send" ) ){
            String phone = dataMap.getString( "phone" );
            long[] pattern = dataMap.getLongArray( "pattern" );
            String text = dataMap.getString( "text" );

            if( phone != null && pattern.length > 0 ){
                GcmSenderService.getInstance().sendMessage( phone, pattern, text );
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
        }
    }
}
