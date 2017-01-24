package utils;

import java.util.Date;

public class MatchRequest {
	
	private String Token;
	private Date tempo;
	
	public MatchRequest(String token){
		this.setToken(token);
		this.setTempo(new Date()); // quando chiamo new Date() java di default assegna come valore la data corrente.
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

	

	

	

	
}
