package hello;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class WriteToMySql {

	public static void connection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			//System.out.println("Worked");
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
	}
	
	public static void ConnectionToMySql(){
		
		connection();
		String host = "jdbc:mysql://localhost/zquiz";
		String username = "root";
		String password = "";
		try {
			Connection connect = DriverManager.getConnection(host, username, password);
			System.out.println("Works :)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		//connection(); prima prova
		ConnectionToMySql();
		
	}

}
