package hello;

import java.util.Date;

import database.DBQueries;

public class Questions {

	private DomandaSingola domanda1;
	private DomandaSingola domanda2;
	private DomandaSingola domanda3;
	private DomandaSingola domanda4;
	private Date tempo; //per regolare la scadenza della partita
	private int[] score = {0, 0};
	private String[] Users;
	
	
	
	public Questions(DomandaSingola d1, DomandaSingola d2, DomandaSingola d3, DomandaSingola d4){
		this.domanda1 = d1;
		this.domanda2 = d2;
		this.domanda3 = d3;
		this.domanda4 = d4;
		this.setTempo(new Date());
	}
	
	
	public Questions(DomandaSingola d1, DomandaSingola d2, DomandaSingola d3, DomandaSingola d4, String t1, String t2){
		this.domanda1 = d1;
		this.domanda2 = d2;
		this.domanda3 = d3;
		this.domanda4 = d4;
		this.setTempo(new Date());
		this.Users[0]= DBQueries.getUserFromToken(t1);
		this.Users[1]= DBQueries.getUserFromToken(t2);
	}
	
	public DomandaSingola getDomandaSingola1(){
		return domanda1;
	}
	
	public DomandaSingola getDomandaSingola2(){
		return domanda2;
	}
	
	public DomandaSingola getDomandaSingola3(){
		return domanda3;
	}
	
	public DomandaSingola getDomandaSingola4(){
		return domanda4;
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

}
