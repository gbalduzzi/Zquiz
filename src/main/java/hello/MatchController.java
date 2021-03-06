package hello;

import java.util.Date;

import config.ReadConfigFile;
import database.DBQueries;
import model.CompleteQuestion;

public class MatchController {

	private CompleteQuestion[] Domande = new CompleteQuestion[4];
	private Date tempo; //per regolare la scadenza della partita
	private int[] score = {0, 0};
	private String[] Users = {"", ""};
	
	public MatchController(CompleteQuestion d1, CompleteQuestion d2, CompleteQuestion d3, CompleteQuestion d4, String u1, String u2){
		this.Domande[0] = d1;
		this.Domande[1] = d2;
		this.Domande[2] = d3;
		this.Domande[3] = d4;
		this.setTempo(new Date());
		this.Users[0]= u1;
		this.Users[1]= u2;
	}
	
	public Date getTempo() {
		return tempo;
	}

	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}
	
	public void setScore(int s, String Token){
		String Utente = DBQueries.getUserFromToken(Token);
		if( Users[0].equals(Utente)){
			score[0]=s;
		}else{
			score[1]=s;
		}
	}
	
	public int getScore(String Token){
		String Utente = DBQueries.getUserFromToken(Token);
		if( Users[0].equals(Utente)){
			return score[0];
		}else{
			return score[1];
		}
	}

	public String getUser1(){
		return Users[0];
	}
	
	public String getUser2(){
		return Users[1];
	}
	
	public CompleteQuestion getDomanda(int n){
		ReadConfigFile r = ReadConfigFile.getInstance();
		if(QueueController.getDateDiff(this.getTempo())>(n-1)*r.getAnswerTime()){
			return Domande[n-1];
		}else{
			return null;
		}
	}
	
	public CompleteQuestion getDomandaUnchecked(int n){
		if(n > 0 && n < 5){
			return Domande[n-1];
		}else{
			return null;
		}
	}
	
	public int getScore1(int i){
		return score[i];
	}
}
