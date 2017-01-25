package model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Token extends BaseClass {

	
	private String token;
	
	public Token(String t){
		this.setToken(t);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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
