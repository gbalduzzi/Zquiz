package hello;

public class GestionePartita implements Runnable{
	
	//Lista delle partite attive (megluo usare dictionary)
	//Ogni oggetto della lista conterrà già tutte le domande.

	public void run(){
		
		//in questo thread faccio i controlli suslla lista delle partite attive. Quando rimuovo un partita dalla lista(perchè è scaduta
		//oppure perchè è semplicemente finita) allora rimuovo dalla lista la partita e setto il valore lo stato della partita nel database a null.
	}
	
	//metodo ststico per inserire una partita nella lista (ogni oggetto della lista conterrà già tutte le domande previste, così interrogo il database un unica volta).
	
	
	
}
