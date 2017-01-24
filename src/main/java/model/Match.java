package model;

import java.sql.ResultSet;
import java.sql.SQLException;

//calsse da inviare col json quando riesco, dopo una richiesta dell'utente a inviare un apartita.
public class Match {

	private int match_id;
	private String opponent;
	
	public Match(int MatchID, String Avversario){
		this.setAvversario(Avversario);
		this.setMatchID(MatchID);
	}

	public int getMatchID() {
		return match_id;
	}

	public void setMatchID(int matchID) {
		match_id = matchID;
	}

	public String getAvversario() {
		return opponent;
	}

	public void setAvversario(String avversario) {
		opponent = avversario;
	}
	
	public static Match createPartitaFromResultSet(ResultSet data, String User) {
		try {
			if (data.next()) {
				Match p = new Match(data.getInt(1), User.equals(data.getString(2))? data.getString(3) : data.getString(2) );
				return p;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
