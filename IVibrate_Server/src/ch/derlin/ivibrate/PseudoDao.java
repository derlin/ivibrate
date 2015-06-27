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


import gson.GsonUtils;

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
    private static final String usersFileName = "users_file.txt";

    private MapsContainer maps;
    private final static PseudoDao instance = new PseudoDao();
    private final static Random sRandom = new Random();
    private final Set<Integer> mMessageIds = new HashSet<Integer>();


    public static void main( String[] args ){
        // initialise file
        GsonUtils.writeJsonFile( usersFileName, new MapsContainer() );
    }//end main


    private PseudoDao(){
        load();
    }


    public static PseudoDao getInstance(){
        return instance;
    }


    public boolean addUser( String regId, String accountName ){
        synchronized( this ){
            if( accountName != null && !maps.users.containsKey( accountName ) ){
                maps.users.put( accountName, regId );
                maps.tokenIds.put( regId, accountName );
                saveToFile();
                return true;
            }
            return false;
        }
    }


    public boolean removeAccount( String accountName ){
        synchronized( this ){
            if( accountName != null && maps.users.containsKey( accountName ) ){
                maps.tokenIds.remove( maps.users.get( accountName ) );
                maps.users.remove( accountName );
                saveToFile();
                return true;
            }
            return false;
        }
    }


    public Collection<String> getRegistrationIds(){
        return Collections.unmodifiableCollection( maps.users.values() );
    }


    public String getRegistrationId( String account ){
        return maps.users.containsKey( account ) ? maps.users.get( account ) : null;
    }


    public String getAccountName( String regId ){
        return maps.tokenIds.containsKey( regId ) ? maps.tokenIds.get( regId ) : null;
    }


    public Set<String> getAccountNames(){
        return Collections.unmodifiableSet( maps.users.keySet() );
    }


    public String getUniqueMessageId(){
        int nextRandom = sRandom.nextInt();
        while( mMessageIds.contains( nextRandom ) ){
            nextRandom = sRandom.nextInt();
        }
        return Integer.toString( nextRandom );
    }

    // ----------------------------------------------------


    private void saveToFile(){
        if(!GsonUtils.writeJsonFile( usersFileName, this.maps )){
            System.err.println( "Could not serialise map" );
        }

    }


    private void load(){
        MapsContainer maps = ( MapsContainer ) GsonUtils.getJsonFromFile( usersFileName, new MapsContainer() );
        if( maps != null ) this.maps = maps;
    }

    // ----------------------------------------------------

    static class MapsContainer{
        final Map<String, String> users = new HashMap<String, String>();
        final Map<String, String> tokenIds = new HashMap<String, String>();
    }

}
