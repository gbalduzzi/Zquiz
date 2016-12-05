package hello;

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
}
