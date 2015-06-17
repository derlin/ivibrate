package ch.derlin.ivibrate;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
/**
 * @author: Lucy Linder
 * @date: 17.06.2015
 */
public class GetAccountsProcessor implements IPayloadProcessor{

    @Override
    public void handleMessage( CcsMessage msg ){
        PseudoDao dao = PseudoDao.getInstance();

        Set<String> accounts = PseudoDao.getInstance().getAccounts();
        StringBuilder builder = new StringBuilder(  );
        for( String account : accounts ){
            builder.append( account ).append( "," );
        }//end for

        builder.deleteCharAt( builder.length() -1 ); // remove last ,

        Map<String,String> payload = new TreeMap<String, String>(  );
        payload.put( GcmConstants.ACCOUNTS_KEY, payload.toString() );

        String jsonRequest = CcsClient.createJsonMessage(
                msg.getFrom(), //
                dao.getUniqueMessageId(), //
                msg.getPayload() );

        CcsClient.getInstance().send( jsonRequest );
    }
}//end class
