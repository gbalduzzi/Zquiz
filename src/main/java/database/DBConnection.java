package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import config.ReadConfigFile;

/**
 * 
 * @author gioba
 * Facade per connessione al DB (utilizzabile per ogni query)
 */
public class DBConnection {
	
	/**
	 * Crea la connessione con il DB
	 * @return Connessione al database
	 */
	public static Connection getConnection() {
		ReadConfigFile r = ReadConfigFile.getInstance();
		try {
			return DriverManager.getConnection(r.getHostname(), r.getDBUser(), r.getDBPwd());
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Chiude la connessione al database
	 * @param activeConnection Connessione da chiudere
	 */
	public static void closeConnection(Connection activeConnection) {
		
		try {
			activeConnection.close();
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Rollback della transazione in caso di problemi
	 * @param activeConnection connessione di cui effettuare il rollback
	 */
	public static void rollbackConnection(Connection activeConnection) {
		
		try {
			if (activeConnection != null)
				activeConnection.rollback();
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
	}
}
