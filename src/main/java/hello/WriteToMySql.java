package hello;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Locale;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import config.ReadConfigFile;

public class WriteToMySql {

	public static void connection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
	}
	
	/*
	 * Metodo per inserire elemento nel database
	 */
	public static void ConnectionToMySql_InsertElement(String Username, String Password, String Nome, String Cognome){
		
		ReadConfigFile r = ReadConfigFile.getInstance();
		connection();
		String host = r.getHostname();
		String username = r.getDBUser();
		String password = r.getDBPwd();
		try {
			Connection connect = DriverManager.getConnection(host, username, password);
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("INSERT INTO utente(Username,Password,Nome,Cognome) VALUES(?,?,?,?)");
			statement.setString(1, Username);
			statement.setString(2, Password);
			statement.setString(3, Nome);
			statement.setString(4, Cognome);
			statement.executeUpdate();
			statement.close();
			connect.close();
			System.out.println("Works :)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/*
	 * metodo per inserire token nel database
	 */
	
public static void ConnectionToMySql_InsertToken(String Token, String Username, Timestamp s){
		ReadConfigFile r = ReadConfigFile.getInstance();
		connection();
		String host = r.getHostname();
		String username = r.getDBUser();
		String password = r.getDBPwd();
		try {
			Connection connect = DriverManager.getConnection(host, username, password);
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("INSERT INTO token(Token,Username,FineValidità) VALUES(?,?,?)");
			statement.setString(1, Token);
			statement.setString(2, Username);
			statement.setTimestamp(3, s);
			statement.executeUpdate();
			statement.close();
			connect.close();
			System.out.println("Works :)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/*
	 * metodo per cancellare elemento dal database
	 */
	
	public static void ConnectionToMySql_DeleteElement(String Username){
		
		ReadConfigFile r = ReadConfigFile.getInstance();
		connection();
		String host = r.getHostname();
		String username = r.getDBUser();
		String password = r.getDBPwd();
		try {
			Connection connect = DriverManager.getConnection(host, username, password);
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("DELETE FROM utente WHERE Username = ?");
			statement.setString(1, Username);
			statement.executeUpdate();
			statement.close();
			connect.close();
			System.out.println("Works :)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	/*
	 * metodo per prelevare elemento da phpmyadmin
	 */
	
	public static void ConnectionToMySql_GetElement(){
		ReadConfigFile r = ReadConfigFile.getInstance();
		connection();
		String host = r.getHostname();
		String username = r.getDBUser();
		String password = r.getDBPwd();
		try {
		Connection connect = DriverManager.getConnection(host, username, password);
		PreparedStatement statement = (PreparedStatement) connect.prepareStatement("SELECT * FROM utente");
		ResultSet data = statement.executeQuery();
		while(data.next()){
			System.out.println("Username " + data.getObject("Username"));
		}	
		statement.close();
		connect.close();
		System.out.println("Works :)");
	} catch (SQLException e) {
		e.printStackTrace();
	}
}
	
	public static ResultSet ConnectionToMySql_SelectUtente2(String Utente){
		ReadConfigFile r = ReadConfigFile.getInstance();
		connection();
		String host = r.getHostname();
		String username = r.getDBUser();
		String password = r.getDBPwd();
		try {
		Connection connect = DriverManager.getConnection(host, username, password);
		PreparedStatement statement = (PreparedStatement) connect.prepareStatement("SELECT Username FROM utente WHERE Username = ?");
		statement.setString(1, Utente);
		ResultSet data = statement.executeQuery();
		return data;
	} catch (SQLException e) {
		e.printStackTrace();
		return null;
	} finally {
		//da capire come chiudere la connessione. connect.close()
	}
		
}


	public static ResultSet ConnectionToMySql_SelectUtente2(String Utente, String Password){
		ReadConfigFile r = ReadConfigFile.getInstance();
		connection();
		String host = r.getHostname();
		String username = r.getDBUser();
		String password = r.getDBPwd();
		Password = md5(Password);
		try {
		Connection connect = DriverManager.getConnection(host, username, password);
		PreparedStatement statement = (PreparedStatement) connect.prepareStatement("SELECT * FROM utente WHERE Username = ? AND Password = ?");
		statement.setString(1, Utente);
		statement.setString(2, Password);
		ResultSet data = statement.executeQuery();
		return data;
	} catch (SQLException e) {
		e.printStackTrace();
		return null;
	} finally {
		//da capire come chiudere la connessione. connect.close()
	}
		
}
	
	
	
	public static String md5(String source) {
		   String md5 = null;
		   try {
		         MessageDigest mdEnc = MessageDigest.getInstance("MD5"); //Encryption algorithm
		         mdEnc.update(source.getBytes(), 0, source.length());
		         md5 = new BigInteger(1, mdEnc.digest()).toString(16); // Encrypted string
		        } 
		    catch (Exception ex) {
		         return null;
		    }
		    return md5;
		}
	
	
	public static void main(String[] args) {
		
		//connection(); prima prova
		//ConnectionToMySql_InsertElement("Martinparre", "Juventus", "Martin", "Cossali");
		//ConnectionToMySql_InsertElement("GBalduz", "clusone", "Giorgio", "Balduzzi");
		//ConnectionToMySql_InsertElement("DBertoc", "castione", "Danilo", "Bertocchi");
		//ConnectionToMySql_InsertElement("Dave94", "zerrone", "Davide", "Zerre");
		//ConnectionToMySql_InsertElement("Martinparre2", "Juventus", "Martin", "Cossali");
		//ConnectionToMySql_DeleteElement("Martinparre2");
		//ConnectionToMySql_GetElement();
		//ConnectionToMySql_SelectUtente("Martinparre");
		//ConnectionToMySql_SelectUtente("DBertoc");
		//ConnectionToMySql_InsertToken("zerrone", "dave94", sqlTimestamp);
		
		ConnectionToMySql_SelectUtente2("martinparre", "juventus"); 
		//ConnectionToMySql_SelectUtente2("g.balduz","clusone"); 
		
		
	}

}
