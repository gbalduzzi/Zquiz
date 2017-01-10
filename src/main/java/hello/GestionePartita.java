package hello;

import java.io.Console;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.text.html.HTMLDocument.Iterator;

import database.DBQueries;

public class GestionePartita implements Runnable{
	
	//Lista delle partite attive (megluo usare dictionary) (key-> matchID, Oggetto contenente tutti i dati)
	//Ogni oggetto della lista conterrà già tutte le domande.
	//Dictionary<int MatchID, Questions x> PartiteAttive;
	
	public static Map<Integer, Questions> PartiteAttive = new HashMap<Integer, Questions>();
	
	static ReentrantLock lock = new ReentrantLock();

	public void run(){
		//in questo thread faccio i controlli sulla lista delle partite attive. Quando rimuovo un partita dalla lista(perchè è scaduta
		//oppure perchè è semplicemente finita) allora rimuovo dalla lista la partita e setto il valore lo stato della partita nel database a 0.
		while(true){
			lock.lock();
			for(int key : PartiteAttive.keySet()){
				if(GestioneCoda.getDateDiff(PartiteAttive.get(key).getTempo())>100000){ //tempo oltre al quale decidiamo che la partita si chiude
					PartiteAttive.remove(key);
					DBQueries.EndMatch(key);
				}
			}
		}
	}
	
	//metodo ststico per inserire una partita nella lista (ogni oggetto della lista conterrà già tutte le domande previste, così interrogo il database un unica volta).
	public static void InsertMatch(int Match, String t1, String t2){
		lock.lock();
		try{
			PartiteAttive.put(Match, DBQueries.selectDomande());
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		finally{
			lock.unlock();
		}
	}
	
	public static DomandaSingola GetDomanda(int number){
		
		return null;
	}
	
	
}
