package utils;

import java.util.Date;

import database.DBConnection;

public class MatchRequest {
	
	private String Token;
	private Date tempo;
	private int Vittorie;
	
	public MatchRequest(String token){
		this.setToken(token);
		this.setTempo(new Date()); // quando chiamo new Date() java di default assegna come valore la data corrente.
		this.Vittorie = DBConnection.getWin(Token);
	}

	public String getToken() {
		return Token;
	}

	public void setToken(String token) {
		Token = token;
	}

	public Date getTempo() {
		return tempo;
	}

	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}

	public int getVittorie() {
		return Vittorie;
	}

	public void setVittorie(int vittorie) {
		Vittorie = vittorie;
	}
}
