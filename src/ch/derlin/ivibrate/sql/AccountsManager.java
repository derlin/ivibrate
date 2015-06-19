package ch.derlin.ivibrate.sql;

import java.sql.*;
import java.util.*;

/**
 * @author: Lucy Linder
 * @date: 19.06.2015
 */
public class AccountsManager{

    private static final String DB_PATH = "mysqlitedb.db";
    private static final String ACCOUNT_TABLE = "ACCOUNT";
    private static final String COL_REGID = "REGID";
    private static final String COL_NAME = "NAME";
    private static final String COL_PHONE = "PHONE";

    private final static Random sRandom = new Random();
    private final Set<Integer> mMessageIds = new HashSet<>();

    private static final AccountsManager INSTANCE = new AccountsManager();

    private Map<String, Account> users = new HashMap<>();
    private Map<String, Account> regIds = new HashMap<>();


    public static void main( String args[] ){
        Connection c;
        Statement stmt;
        try{
            Class.forName( "org.sqlite.JDBC" );
            c = DriverManager.getConnection( "jdbc:sqlite:mysqlitedb.db" );
            System.out.println( "Opened database successfully" );

            stmt = c.createStatement();
            String sql = String.format( "CREATE TABLE %s " +  //
                            "(%s TEXT PRIMARY KEY     NOT NULL," +  //
                            " %s  TEXT NOT NULL, " +
                            " %s TEXT NOT NULL, " +
                            " TIMESTAMP DATETIME default CURRENT_TIMESTAMP)", //
                    ACCOUNT_TABLE, COL_REGID, COL_NAME, COL_PHONE );

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
        return INSTANCE;
    }


    private AccountsManager(){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = getConnection();
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( String.format( "SELECT * FROM %s;", ACCOUNT_TABLE) );

            while( rs.next() ){
                Account account = new Account( //
                        rs.getString( "regid" ), rs.getString( "name" ), rs.getString( "phone" ) );

                users.put( account.getPhoneNumber(), account );
                regIds.put( account.getRegId(), account );
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


    public String getRegistrationId( String phone ){
        return users.containsKey( phone ) ? users.get( phone ).getRegId() : null;
    }


    public Account getAccount( String regId ){
        return regIds.containsKey( regId ) ? regIds.get( regId ) : null;
    }


    public Collection<Account> getAccounts(){
        return Collections.unmodifiableCollection( users.values() );
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


    public synchronized boolean addAccount(Account account){

        if(regIds.containsKey( account.getRegId() )){
            return false;
        }

        try {
            Connection c = getConnection();
            Statement stmt = c.createStatement();

            String sql = String.format( "INSERT INTO %s(%s, %s, %s) VALUES('%s', '%s', '%s')", //
                    ACCOUNT_TABLE, COL_REGID, COL_NAME, COL_PHONE,  //
                    account.getRegId(), account.getName(), account.getPhoneNumber()
                    );

            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            c.close();

            users.put( account.getPhoneNumber(), account );
            regIds.put( account.getRegId(), account );

            System.out.println( account.getName() + " added to db." );
            return true;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit( 0 );
        }
        return false;
    }

    // ----------------------------------------------------

    public synchronized boolean removeAccount(Account account){

        if(!regIds.containsKey( account.getRegId() )){
            return false;
        }

        try {
            Connection c = getConnection();
            Statement stmt = c.createStatement();

            String sql = String.format( "DELETE FROM %s WHERE %s = '%s')", //
                    ACCOUNT_TABLE, COL_REGID, account.getRegId()
            );

            stmt.executeUpdate( sql );
            stmt.close();
            c.commit();
            c.close();

            users.remove( account.getPhoneNumber() );
            regIds.remove( account.getRegId() );

            System.out.println(account.getName() + " removed from db.");
            return true;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return false;
    }

    // ----------------------------------------------------

    private static Connection getConnection() throws SQLException{
        return DriverManager.getConnection( "jdbc:sqlite:" + DB_PATH );
    }

}//end class
