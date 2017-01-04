package hello;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Token {

	
	private String Token;
	
	public Token(String t){
		this.setToken(t);
	}

	public String getToken() {
		return Token;
	}

	public void setToken(String token) {
		Token = token;
	}
	
	public static Token createTokenFromResultSet(ResultSet data) {
		try {
			if (data.next()) {
				Token t = new Token(data.getString("Token"));
				return t;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
