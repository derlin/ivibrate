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

import java.util.*;

/**
 * This class acts as a DAO replacement. There is no
 * persistent state. As soon as you kill the server, all state will
 * be lost.
 * <p/>
 * You have to take care of persisting messages as well as
 * recipients for proper apps!
 */
public class PseudoDao{

    private final static PseudoDao instance = new PseudoDao();
    private final static Random sRandom = new Random();
    private final Set<Integer> mMessageIds = new HashSet<Integer>();
    private final Map<String, String> mUserMap = new HashMap<String, String>();
    private final Map<String, String> mReverseUserMap = new HashMap<String, String>();

    private final String user_file_name = "users.txt";

    private PseudoDao(){
    }


    public static PseudoDao getInstance(){
        return instance;
    }


    public boolean addRegistration( String regId, String accountName ){
        synchronized( this ){
            if( accountName != null && !mUserMap.containsKey( accountName ) ){
                mUserMap.put( accountName, regId );
                mReverseUserMap.put( regId, accountName );
                return true;
            }
            return false;
        }
    }


    public Collection<String> getRegistrationIds(){
        return Collections.unmodifiableCollection( mUserMap.values() );
    }


    public String getRegistrationId( String account ){
        return mUserMap.containsKey( account ) ? mUserMap.get( account ) : null;
    }


    public String getAccount( String regId ){
        return mReverseUserMap.containsKey( regId ) ? mReverseUserMap.get( regId ) : null;
    }

    public Set<String> getAccounts(){
        return Collections.unmodifiableSet( mUserMap.keySet() );
    }


    public String getUniqueMessageId(){
        int nextRandom = sRandom.nextInt();
        while( mMessageIds.contains( nextRandom ) ){
            nextRandom = sRandom.nextInt();
        }
        return Integer.toString( nextRandom );
    }
}
