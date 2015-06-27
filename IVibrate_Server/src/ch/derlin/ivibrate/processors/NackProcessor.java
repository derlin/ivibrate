package ch.derlin.ivibrate.processors;

import ch.derlin.ivibrate.CcsClient;
import ch.derlin.ivibrate.CcsMessage;
import ch.derlin.ivibrate.GcmConstants;
import ch.derlin.ivibrate.sql.AccountsManager;

import java.util.Map;

/**
 * @author: Lucy Linder
 * @date: 26.06.2015
 */
public class NackProcessor implements IPayloadProcessor{

    @Override
    public void handleMessage( CcsMessage msg ){
        String regid = msg.getFromRegid();

        Map<String, String> payload = msg.getPayload();
        payload.put( GcmConstants.MESG_TYPE_KEY, GcmConstants.ACTION_NACK );
        String jsonRequest = CcsClient.createJsonMessage( regid, AccountsManager.getInstance().getUniqueMessageId(), payload );
        CcsClient.getInstance().send( jsonRequest );
    }
}//end class
