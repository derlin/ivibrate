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

import static ch.derlin.ivibrate.GcmConstants.*;

public class ProcessorFactory{

    public static IPayloadProcessor getProcessor( String action ){
        if( action == null ){
            throw new IllegalStateException( "action must not be null" );
        }
        if( action.equals( ACTION_REGISTER ) ){
            return new RegisterProcessor();
        }else if( action.equals( ACTION_ECHO ) ){
            return new EchoProcessor();
        }else if( action.equals( ACTION_MESSAGE ) ){
            return new MessageProcessor();
        }else if( action.equals( ACTION_GET_ACCOUNTS ) ){
            return new GetAccountsProcessor();
        }
        throw new IllegalStateException( "Action " + action + " is unknown" );
    }
}
