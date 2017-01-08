package hello;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DomandaSingola {

	private int Domanda_ID;
	private String[] DomandeRisposte = new String[5];
	private int RispostaGiusta;
	
	public DomandaSingola(int id,String Domanda, String r1, String r2, String r3, String r4 , int giusta){
		//assegno.
		this.Domanda_ID = id;
		this.DomandeRisposte[0] = Domanda;
		this.DomandeRisposte[1] = r1;
		this.DomandeRisposte[2] = r2;
		this.DomandeRisposte[3] = r3;
		this.DomandeRisposte[4] = r4;
		this.setRispostaGiusta(giusta);
		
	}
	
	public String getDomanda(){
		return DomandeRisposte[0];
	}
	
	public String getRisposta1(){
		return DomandeRisposte[1];
	}
	
	public String getRisposta2(){
		return DomandeRisposte[2];
	}	

	public String getRisposta3(){
		return DomandeRisposte[3];
	}
	
	public String getRisposta4(){
		return DomandeRisposte[4];
	}
	public int getRispostaGiusta() {
		return RispostaGiusta;
	}

	public void setRispostaGiusta(int rispostaGiusta) {
		RispostaGiusta = rispostaGiusta;
	}
	
	public static DomandaSingola createDomandaSingolaFromResultSet(ResultSet data) {
		try {
			if (data.next()) {
				DomandaSingola d = new DomandaSingola(data.getInt(1), data.getString(2), data.getString(4), data.getString(5), data.getString(6), data.getString(7), data.getInt(8));
				return d;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public int getDomanda_ID() {
		return Domanda_ID;
	}

	public void setDomanda_ID(int domanda_ID) {
		Domanda_ID = domanda_ID;
	}
	
}
