package hello;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

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
		PreparedStatement statement = (PreparedStatement) connect.prepareStatement("SELECT Username FROM utente WHERE Username = ? ");
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

	
	
	
	public static void main(String[] args) {
		
		//connection(); prima prova
		ConnectionToMySql_InsertElement("Martinparre", "Juventus", "Martin", "Cossali");
		//ConnectionToMySql_InsertElement("GBalduz", "clusone", "Giorgio", "Balduzzi");
		//ConnectionToMySql_InsertElement("DBertoc", "castione", "Danilo", "Bertocchi");
		//ConnectionToMySql_InsertElement("Dave94", "zerrone", "Davide", "Zerre");
		//ConnectionToMySql_InsertElement("Martinparre2", "Juventus", "Martin", "Cossali");
		//ConnectionToMySql_DeleteElement("Martinparre2");
		//ConnectionToMySql_GetElement();
		//ConnectionToMySql_SelectUtente("Martinparre");
		//ConnectionToMySql_SelectUtente("DBertoc");
	
	
	
	}

}
