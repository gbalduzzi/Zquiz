package hello;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {

    
    private final String Username;
    private final String Nome;
    private final String Cognome;
    private final int Vittorie;
    
    public User(String u, String n, String c, int v) {
        this.Username = u ;
        this.Nome = n ;
        this.Cognome = c ;
        this.Vittorie = v;
    }

	public String getUsername() {
		return Username;
	}

	public String getNome() {
		return Nome;
	}

	public String getCognome() {
		return Cognome;
	}

	public int getVittorie() {
		return Vittorie;
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