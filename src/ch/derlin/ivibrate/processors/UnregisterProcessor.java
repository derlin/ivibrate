/*
 * Copyright 2014 Wolfram Rittmeyer.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.derlin.ivibrate.processors;

import ch.derlin.ivibrate.CcsClient;
import ch.derlin.ivibrate.CcsMessage;
import ch.derlin.ivibrate.GcmConstants;
import ch.derlin.ivibrate.PseudoDao;

import java.util.Collection;
import java.util.Map;

/**
 * Handles a user registration.
 */
public class UnregisterProcessor implements IPayloadProcessor{

    @Override
    public void handleMessage( CcsMessage msg ){
        PseudoDao dao = PseudoDao.getInstance();
        String accountName = msg.getPayload().get( GcmConstants.MESSAGE_KEY );

        if(dao.removeAccount( accountName )){
            Map<String, String> payload = msg.getPayload();
            payload.put( GcmConstants.MESG_TYPE_KEY, GcmConstants.ACTION_UNREGISTER );
            payload.put( GcmConstants.ACCOUNTS_KEY, accountName );
            Collection<String> registrationIds = dao.getRegistrationIds();
            CcsClient.getInstance().sendBroadcast( registrationIds, payload );
             // also notify the sender
             CcsClient.getInstance().send( CcsClient.createJsonMessage( msg.getFrom(), dao.getUniqueMessageId(), payload ) );
        }
    }

}
