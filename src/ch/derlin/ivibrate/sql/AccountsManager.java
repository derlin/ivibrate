package ch.derlin.ivibrate.sql;

import java.sql.*;
import java.util.*;

/**
 * @author: Lucy Linder
 * @date: 19.06.2015
 */
public class AccountsManager{

    private static final String DB_PATH = "mysqlitedb.db";
    private static final String ACCOUNT_TABLE = "ACCOUNTS";
    private static final String COL_REGID = "REGID";
    private static final String COL_NAME = "NAME";

    private final static Random sRandom = new Random();
    private final Set<Integer> mMessageIds = new HashSet<>();

    private static AccountsManager INSTANCE;

    private Map<String, String> users = new HashMap<>();
    private Map<String, String> regIds = new HashMap<>();


    public static void main( String args[] ){
        //        initDB();
        AccountsManager am = new AccountsManager();
        System.out.println( am.getNames() );
        //        am.addAccount( "0795490041", "agnes-regid" );
        //        am.addAccount( "0792458829", "maman-regid" );
        //        am.addAccount( "0787394184", "papa-regid" );
        //        System.out.println("done");
    }


    private static void initDB(){
        Connection c;
        Statement stmt;
        try{
            Class.forName( "org.sqlite.JDBC" );
            c = DriverManager.getConnection( "jdbc:sqlite:mysqlitedb.db" );
            System.out.println( "Opened database successfully" );

            stmt = c.createStatement();
            String sql = String.format( "CREATE TABLE %s " +  //
                            "(%s TEXT PRIMARY KEY     NOT NULL," +  //
                            " %s TEXT UNIQUE NOT NULL, " +
                            " TIMESTAMP DATETIME default CURRENT_TIMESTAMP)", //
                    ACCOUNT_TABLE, COL_REGID, COL_NAME );

            stmt.executeUpdate( sql );
            stmt.close();
            c.close();

        }catch( Exception e ){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit( 0 );
        }
        System.out.println( "Table created successfully" );
    }

    // ----------------------------------------------------


    public static AccountsManager getInstance(){
        if( INSTANCE == null ) INSTANCE = new AccountsManager();
        return INSTANCE;
    }


    private AccountsManager(){
        try{
            Class.forName( "org.sqlite.JDBC" );
            Connection c = getConnection();
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( String.format( "SELECT * FROM %s;", ACCOUNT_TABLE ) );

            while( rs.next() ){
                String regid = rs.getString( COL_REGID );
                String name = rs.getString( COL_NAME );

                users.put( name, regid );
                regIds.put( regid, name );
            }
            rs.close();
            stmt.close();
            c.close();

        }catch( SQLException e ){
            e.printStackTrace();
        }catch( ClassNotFoundException e ){
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------


    public Collection<String> getRegistrationIds(){
        return Collections.unmodifiableCollection( regIds.keySet() );
    }


    public String getRegistrationId( String name ){
        return users.containsKey( name ) ? users.get( name ) : null;
    }


    public String getName( String regId ){
        return regIds.containsKey( regId ) ? regIds.get( regId ) : null;
    }


    public Collection<String> getNames(){
        return Collections.unmodifiableCollection( users.keySet() );
    }

    // ----------------------------------------------------


    public String getUniqueMessageId(){
        int nextRandom = sRandom.nextInt();
        while( mMessageIds.contains( nextRandom ) ){
            nextRandom = sRandom.nextInt();
        }
        return Integer.toString( nextRandom );
    }


    // ----------------------------------------------------


    public synchronized boolean addAccount( String name, String regid ){

        if( users.containsKey( name ) ){
            if( users.get( name ).equals( regid ) ) return true;
            return updateRegid( name, regid );
        }

        try{
            Connection c = getConnection();
            Statement stmt = c.createStatement();

            String sql = String.format( "INSERT INTO %s(%s, %s) VALUES('%s', '%s')", //
                    ACCOUNT_TABLE, COL_REGID, COL_NAME,  //
                    regid, name );

            stmt.executeUpdate( sql );
            stmt.close();
            c.close();

            users.put( name, regid );
            regIds.put( regid, name );

            System.out.println( name + " added to db." );
            return true;
        }catch( Exception e ){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            //            System.exit( 0 );
        }
        return false;
    }


    // ----------------------------------------------------


    public synchronized boolean updateRegid( String name, String regid ){

        if( name == null ){
            return false;
        }

        try{
            Connection c = getConnection();
            Statement stmt = c.createStatement();

            String sql = String.format( "UPDATE %s SET %s = '%s' WHERE %s = '%s'", //
                    ACCOUNT_TABLE, COL_NAME, name, COL_REGID, regid );

            stmt.executeUpdate( sql );
            stmt.close();
            c.close();

            // replace old regid with new one
            regIds.remove( users.get( name ) );
            regIds.put( regid, name );
            users.put( name, regid );

            System.out.println( name + " updated." );
            return true;

        }catch( Exception e ){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }

        return false;
    }

    // ----------------------------------------------------


    public synchronized boolean removeAccount( String regid ){

        String name = regIds.get( regid );

        if( name == null ){
            return false;
        }

        try{
            Connection c = getConnection();
            Statement stmt = c.createStatement();

            String sql = String.format( "DELETE FROM %s WHERE %s = '%s'", //
                    ACCOUNT_TABLE, COL_REGID, regid );

            stmt.executeUpdate( sql );
            stmt.close();
            c.close();

            users.remove( name );
            regIds.remove( regid );

            System.out.println( name + " removed from db." );
            return true;
        }catch( Exception e ){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return false;
    }

    // ----------------------------------------------------


    private static Connection getConnection() throws SQLException{
        return DriverManager.getConnection( "jdbc:sqlite:" + DB_PATH );
    }

}//end class
