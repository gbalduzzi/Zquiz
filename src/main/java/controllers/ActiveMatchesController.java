package controllers;

import java.io.Console;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.text.html.HTMLDocument.Iterator;

import config.ReadConfigFile;
import database.DBQueries;

public class ActiveMatchesController implements Runnable{
	
	//Lista delle partite attive (megluo usare dictionary) (key-> matchID, Oggetto contenente tutti i dati)
	//Ogni oggetto della lista conterrà già tutte le domande.
	//Dictionary<int MatchID, Questions x> PartiteAttive;
	
	public static Map<Integer, MatchController> PartiteAttive = new HashMap<Integer, MatchController>();
	
	static ReentrantLock lock = new ReentrantLock();

	public void run(){
		//in questo thread faccio i controlli sulla lista delle partite attive. Quando rimuovo un partita dalla lista(perchè è scaduta
		//oppure perchè è semplicemente finita) allora rimuovo dalla lista la partita e setto il valore lo stato della partita nel database a 0.
		while(true){
			lock.lock();
			ReadConfigFile r = ReadConfigFile.getInstance();
			for(int key : PartiteAttive.keySet()){
				if(QueueController.getDateDiff(PartiteAttive.get(key).getTempo()) > r.getAnswerTime()*4 ){ //tempo oltre al quale decidiamo che la partita si chiude
					// Chiudo la partita perchè è passato troppo tempo
					MatchController m = PartiteAttive.get(key);
					// Salvo i risultati della partita
					DBQueries.insertResult(m.getUser1(), key, m.getScore1(0));
					DBQueries.insertResult(m.getUser2(), key, m.getScore1(1));
					
					String winner = m.getScore1(0) > m.getScore1(1) ? m.getUser1() : m.getUser2();
					
					PartiteAttive.remove(key);
					DBQueries.EndMatch(key, winner);
					System.out.println("Partita rimossa del dictionary");
				}
			}
			lock.unlock();
		}
	}
	
	//metodo ststico per inserire una partita nella lista (ogni oggetto della lista conterrà già tutte le domande previste, così interrogo il database un unica volta).
	public static void InsertMatch(int Match, String t1, String t2){
		System.out.println("partita inserita nel dictionary");
		lock.lock();
		try{
			PartiteAttive.put(Match, DBQueries.selectDomande(t1, t2));
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		finally{
			lock.unlock();
		}
	}
}
