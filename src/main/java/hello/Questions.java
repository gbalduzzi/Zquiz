package hello;

import java.util.Date;

import database.DBQueries;

public class Questions {

	private DomandaSingola[] Domande = new DomandaSingola[4];
	private Date tempo; //per regolare la scadenza della partita
	private int[] score = {0, 0};
	private String[] Users = {"", ""};
	
	
	/**
	public Questions(DomandaSingola d1, DomandaSingola d2, DomandaSingola d3, DomandaSingola d4){
		this.domanda1 = d1;
		this.domanda2 = d2;
		this.domanda3 = d3;
		this.domanda4 = d4;
		this.setTempo(new Date());
	} **/
	
	public Questions(DomandaSingola d1, DomandaSingola d2, DomandaSingola d3, DomandaSingola d4, String u1, String u2){
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
	
	public DomandaSingola getDomanda(int n){
		if(GestioneCoda.getDateDiff(this.getTempo())>(n-1)*50000){
			return Domande[n-1];
		}else{
			return null;
		}
	}
	
	public int getScore1(int i){
		return score[i];
	}
}
