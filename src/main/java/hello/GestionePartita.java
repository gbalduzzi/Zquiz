package hello;

import java.util.Dictionary;

public class GestionePartita implements Runnable{
	
	//Lista delle partite attive (megluo usare dictionary) (key-> matchID, Oggetto contenente tutti i dati)
	//Ogni oggetto della lista conterr� gi� tutte le domande.
	Dictionary<int MatchID, Questions x> PartiteAttive;

	public void run(){
		
		//in questo thread faccio i controlli sulla lista delle partite attive. Quando rimuovo un partita dalla lista(perch� � scaduta
		//oppure perch� � semplicemente finita) allora rimuovo dalla lista la partita e setto il valore lo stato della partita nel database a 0.
	}
	
	//metodo ststico per inserire una partita nella lista (ogni oggetto della lista conterr� gi� tutte le domande previste, cos� interrogo il database un unica volta).
	
}
