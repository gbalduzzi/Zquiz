package model;

import java.sql.ResultSet;
import java.sql.SQLException;

//calsse da inviare col json quando riesco, dopo una richiesta dell'utente a inviare un apartita.
public class Match extends BaseClass {

	private int match_id;
	private String opponent;
	
	public Match(int MatchID, String Avversario){
		this.setOpponent(Avversario);
		this.setMatch_id(MatchID);
	}

	public String getOpponent() {
		return opponent;
	}

	public void setOpponent(String opponent) {
		this.opponent = opponent;
	}

	public int getMatch_id() {
		return match_id;
	}

	public void setMatch_id(int match_id) {
		this.match_id = match_id;
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
