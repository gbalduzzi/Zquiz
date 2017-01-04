package hello;

import java.sql.ResultSet;
import java.sql.SQLException;

//calsse da inviare col json quando riesco, dopo una richiesta dell'utente a inviare un apartita.
public class Partita {

	private int MatchID;
	private String Avversario;
	
	public Partita(int MatchID, String Avversario){
		this.setAvversario(Avversario);
		this.setMatchID(MatchID);
	}

	public int getMatchID() {
		return MatchID;
	}

	public void setMatchID(int matchID) {
		MatchID = matchID;
	}

	public String getAvversario() {
		return Avversario;
	}

	public void setAvversario(String avversario) {
		Avversario = avversario;
	}
	
	public static Partita createPartitaFromResultSet(ResultSet data, String User) {
		try {
			if (data.next()) {
				Partita p = new Partita(data.getInt(1), User.equals(data.getString(2))? data.getString(3) : data.getString(2) );
				return p;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
