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
package ch.derlin.ivibrate;

/**
 * Handles an echo request.
 */
public class MessageProcessor implements IPayloadProcessor{

    @Override
    public void handleMessage( ch.derlin.ivibrate.CcsMessage msg ){
        try{
            PseudoDao dao = PseudoDao.getInstance();
            CcsClient client = CcsClient.getInstance();
            String msgId = dao.getUniqueMessageId();
            String account = msg.getPayload().get( GcmConstants.TO_KEY );
            String to = PseudoDao.getInstance().getAllRegistrationIdsForAccount( account ).get( 0 );
            String jsonRequest = CcsClient.createJsonMessage( to, msgId, msg.getPayload() );
            client.send( jsonRequest );
        }catch( Exception e ){
            System.err.println( "Error " + e );
        }
    }

}
