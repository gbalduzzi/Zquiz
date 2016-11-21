package hello;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.PreparedStatement;

public class WriteToMySql {

	public static void connection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			//System.out.println("Worked");
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
	}
	
	/*
	 * Metodo per inserire elemento nel database
	 */
	
	public static void ConnectionToMySql_InsertElement(String Username, String Password, String Nome, String Cognome){
		
		connection();
		String host = "jdbc:mysql://localhost/zquiz";
		String username = "root";
		String password = "";
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
		
		connection();
		String host = "jdbc:mysql://localhost/zquiz";
		String username = "root";
		String password = "";
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
	connection();
	String host = "jdbc:mysql://localhost/zquiz";
	String username = "root";
	String password = "";
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

	
	
	
	public static void main(String[] args) {
		
		//connection(); prima prova
		ConnectionToMySql_InsertElement("Martinparre", "Juventus", "Martin", "Cossali");
		ConnectionToMySql_InsertElement("GBalduz", "clusone", "Giorgio", "Balduzzi");
		ConnectionToMySql_InsertElement("DBertoc", "castione", "Danilo", "Bertocchi");
		ConnectionToMySql_InsertElement("Dave94", "zerrone", "Davide", "Zerre");
		ConnectionToMySql_InsertElement("Martinparre2", "Juventus", "Martin", "Cossali");
		ConnectionToMySql_DeleteElement("Martinparre2");
		ConnectionToMySql_GetElement();
		
		
	}

}
