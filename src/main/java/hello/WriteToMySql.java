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
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

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
	 * 
	 * Metodo per inserire utente 
	 * usato nel metodo /register
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
			System.out.println("Utente inserito correttamente nel database :)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/*
	 * metodo per inserire token nel database
	 * usato nel metodo /register
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
			System.out.println("Token inserito correttamente nel database :)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/*
	 * metodo per cancellare elemento dal database.
	 * 
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
			System.out.println("Utente eliminato correttamente dalla tabella :)");
			//dovrò eliminare anche il token
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	/*
	 * metodo per prelevare elemento da phpmyadmin
	 * probabilmente non si usa VERIFICARE!!!
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
	
	// metodo che restituisce un utente
	// usato nel metodo /register
	
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
	
	//metodo selct utente che restitutisce tutti i dati dell'utente, anche il numero di vittorie
	//usato in /user
	
	public static ResultSet ConnectionToMySql_SelectUtenteCompleto(String Utente){
		ReadConfigFile r = ReadConfigFile.getInstance();
		connection();
		String host = r.getHostname();
		String username = r.getDBUser();
		String password = r.getDBPwd();
		try {
		Connection connect = DriverManager.getConnection(host, username, password);
		PreparedStatement statement = (PreparedStatement) connect.prepareStatement("SELECT * FROM utente WHERE Username = ?");
		statement.setString(1, Utente);
		ResultSet data = statement.executeQuery();
		return data;
	} catch (SQLException e) {
		e.printStackTrace();
		return null;
	}
}

	// metodo select utente che restituisce utente e password di un utente
	// usato nel metodo /authenticate
	
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
	}
}
	
	// metodo che restituisce il token legato ad un utente
	// usato nel metodo /authenticate
	
	public static ResultSet ConnectionToMySql_SelectToken(String Utente){
		ReadConfigFile r = ReadConfigFile.getInstance();
		connection();
		String host = r.getHostname();
		String username = r.getDBUser();
		String password = r.getDBPwd();
		try {
		Connection connect = DriverManager.getConnection(host, username, password);
		PreparedStatement statement = (PreparedStatement) connect.prepareStatement("SELECT Token FROM token WHERE Username = ?");
		statement.setString(1, Utente);
		ResultSet data = statement.executeQuery();
		return data;
	} catch (SQLException e) {
		e.printStackTrace();
		return null;
	}
}

	// metodo che restituisce il token l'utente legato al token
	// usato nel metodo check partita attiva
	
	public static String ConnectionToMySql_SelectUsername(String token){
		String s = null;
		ReadConfigFile r = ReadConfigFile.getInstance();
		connection();
		String host = r.getHostname();
		String username = r.getDBUser();
		String password = r.getDBPwd();
		try {
		Connection connect = DriverManager.getConnection(host, username, password);
		PreparedStatement statement = (PreparedStatement) connect.prepareStatement("SELECT Username FROM token WHERE Token = ?");
		statement.setString(1, token);
		ResultSet data = statement.executeQuery();
		while(data.next()){
			s= data.getString("Username");
		}
		connect.close();
		statement.close();
		return s;
	} catch (SQLException e) {
		e.printStackTrace();
		return null;
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
	
	//CheckPartitaAttiva(){
	//metodo che torna le partite associate(resultset) al token con stato uguale a 1 (il valore che ricevo è lo user)
	//torna tutti gli elementi della riga.
	
	public static ResultSet ConnectionToMySql_CheckPartitaAttiva(String token){
		String utente = WriteToMySql.ConnectionToMySql_SelectUsername(token);
		ReadConfigFile r = ReadConfigFile.getInstance();
		connection();
		String host = r.getHostname();
		String username = r.getDBUser();
		String password = r.getDBPwd();
		try {
		Connection connect = DriverManager.getConnection(host, username, password);
		PreparedStatement statement = (PreparedStatement) connect.prepareStatement("SELECT * FROM partita WHERE Status =? AND (Username1= ? OR Username2=?)");
		statement.setInt(1, 1);
		statement.setString(2, utente);
		statement.setString(3, utente);
		ResultSet data = statement.executeQuery();
		return data;
	} catch (SQLException e) {
		e.printStackTrace();
		return null;
	}

}
	
	
	//CreatePartita()
	//metodo che riceve in ingresso i due token.. genera un matchid casuale e inserisce la partita nella tabella
	
public static void ConnectionToMySql_CreateMatch(String token1, String token2){
		
		ReadConfigFile r = ReadConfigFile.getInstance();
		connection();
		String host = r.getHostname();
		String username = r.getDBUser();
		String password = r.getDBPwd();
		String tok1 = ConnectionToMySql_SelectUsername(token1);
		String tok2 = ConnectionToMySql_SelectUsername(token2);
		Random randomGenerator = new Random();
		int match_ID = randomGenerator.nextInt(100);
		try {
			Connection connect = DriverManager.getConnection(host, username, password);
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("INSERT INTO partita(Match_ID,Username1,Username2,Status,Inizio) VALUES(?,?,?,?,?)");
			statement.setInt(1, match_ID);
			statement.setString(2, tok1);
			statement.setString(3, tok2);
			statement.setInt(4, 1);
			statement.setTimestamp(5,new Timestamp(System.currentTimeMillis())); // crea data attuale
			statement.executeUpdate();
			statement.close();
			connect.close();
			System.out.println("Match inserito :)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		
		//ConnectionToMySql_SelectUtente2("martinparre", "juventus"); 
		//ConnectionToMySql_SelectUtente2("g.balduz","clusone"); 
		//ConnectionToMySql_SelectToken("martinparre");
		
		//ConnectionToMySql_CheckPartitaAttiva("martinparres1jbl49tdarf4g3qt02va5qt3b");
		
		// funzionano le 2 righe sotto
		//String x =ConnectionToMySql_SelectUsername("martinparres1jbl49tdarf4g3qt02va5qt3b");
		//System.out.println(x);
		
		System.out.println("Partita martinparre:");
		ConnectionToMySql_CheckPartitaAttiva("martinparres1jbl49tdarf4g3qt02va5qt3b");
		System.out.println("partita attiva dave94:");
		ConnectionToMySql_CheckPartitaAttiva("dave941t1j63ivum2takn1g2rv0dmmgg");
		System.out.println("Partita attiva d.bertoc:");
		ConnectionToMySql_CheckPartitaAttiva("d.bertoc8d5uf5ju8dm8p83vvmauub0kgj");
		
		System.out.println("crea partita tra giorgio e nannini");
		ConnectionToMySql_CreateMatch("g.balduzjoebtdp6k96q4ogrdnks74f522", "nanniman7shrs0kqb4ti7ohhlfufev01f3");
		System.out.println("crea partita tra danilo e tia");
		ConnectionToMySql_CreateMatch("d.bertoc8d5uf5ju8dm8p83vvmauub0kgj", "tiapera7cjsm2v1per79mgna4inmi4gm8");
	}

}
