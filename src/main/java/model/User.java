package model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {

    
    private final String username;
    private final String name;
    private final String surname;
    private final int wins;
    
    public User(String u, String n, String c, int v) {
        this.username = u ;
        this.name = n ;
        this.surname = c ;
        this.wins = v;
    }

	public String getUsername() {
		return username;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public int getWins() {
		return wins;
	}
	
	public static User createUserFromResultSet(ResultSet data) {
		try {
			if (data.next()) {
				String u = data.getString("Username");
				String n = data.getString("Nome");
				String c = data.getString("Cognome");
				int v = data.getInt("Vittorie");
				User ut = new User(u, n, c,v);
				return ut;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
   
}