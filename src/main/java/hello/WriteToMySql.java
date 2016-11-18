package hello;

import java.sql.Connection;
import java.sql.DriverManager;
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
	
	public static void ConnectionToMySql(String Username, String Password, String Nome, String Cognome){
		
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
	
	public static void main(String[] args) {
		
		//connection(); prima prova
		ConnectionToMySql("Martinparre","juventus","Martin","Cossali");
		
	}

}
