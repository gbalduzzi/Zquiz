package hello;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import database.DBQueries;
import model.Match;
import utils.MatchRequest;




public class QueueController implements Runnable {

	static List<MatchRequest> UtentiInAttesa = new LinkedList<MatchRequest>();
	static int Contatore=0; //conta gli elementi al momento presenti nella coda

	static ReentrantLock lock = new ReentrantLock();

	public QueueController(){}

	public void run() { //aggiungere qualcosa per far dormire un po' il thread fra un ciclo e l'altro.
		//codice per gestire la coda
		while(true){
			//controllo se qualche timeout e' scaduto....
			lock.lock();
			try{
				Iterator<MatchRequest> it = UtentiInAttesa.iterator();
				while(it.hasNext()) {
					MatchRequest matchRequest = it.next();
					if(getDateDiff(matchRequest.getTempo())>5000){ 
						it.remove();
						Contatore--;
						System.out.println("elemento eliminato dalla coda perche' il tempo e' scaduto \n");
						Stamp();
					}
				}

				if(UtentiInAttesa.size()>=2){

					Iterator<MatchRequest> it2 = UtentiInAttesa.iterator();
					MatchRequest precedente = it2.next();
					Date minore;
					while(it2.hasNext()){
						MatchRequest successivo = it2.next();

						int differenza = Math.abs((successivo.getVittorie()-precedente.getVittorie()))*1000;
						System.out.println("differenza:"+differenza);

						if(precedente.getTempoIniziale().before(successivo.getTempoIniziale())){
							minore= precedente.getTempoIniziale();
						} else{
							minore= successivo.getTempoIniziale();
						}
						
						System.out.println("tempo minore:"+minore);

						long dif2= getDateDiff(minore);
						System.out.println("dif2:"+dif2);
						
						System.out.println("calcolo:"+dif2/differenza);

						if(dif2/differenza>=1){
							UtentiInAttesa.remove(precedente);
							UtentiInAttesa.remove(successivo);
							DBQueries.createMatch(precedente.getToken(), successivo.getToken());
							ActiveMatchesController.InsertMatch(DBQueries.getActiveMatchesByToken(precedente.getToken()).getMatch_id(), precedente.getToken(), successivo.getToken());
							System.out.println("due elementi sono stati inseriti nella tabella e tolti dalla coda");
							Stamp();
							break;
						}
						precedente=successivo;
					}
				}
			}
			finally{
				lock.unlock();
			}

			try {
				Thread.sleep(500);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Stampa a terminale la lista di utenti in attesa
	 */
	public static void Stamp(){
		int cont =0; 
		for (MatchRequest matchRequest : UtentiInAttesa) {
			System.out.println("Elem "+ cont++ +") Token: "+ matchRequest.getToken()+ ", Data: "+ matchRequest.getTempo().toString());
		}
	}

	
	/**
	 * Calcola la distanza in millisecondi tra now e la data passata come parametro
	 * @param date Data passata di cui calcolare la differenza
	 * @return millisecondi tra le due date
	 */
	public static long getDateDiff(Date date) {
		Date temp = new Date(); //di default ha il valore di adesso.
		long diffInMillies = temp.getTime() - date.getTime();
		return diffInMillies;
	}

	/**
	 * Richiesta di una partita da parte di un utente
	 * @param Token Token di autenticazione che identifica l'utente
	 * @return Match creato se presente o null se bisogna aspettare
	 */
	public static Match RequestGame(String Token){

		MatchRequest x = new MatchRequest(Token); //genero la tupla da mettere nella coda.
		lock.lock();
		try{
			Match newMatch = DBQueries.getActiveMatchesByToken(Token); //do per scontato che la partita a questo punto sia appena stata inserita.

			if(newMatch != null){

				return newMatch;

			}
			
			MatchRequest matchRequest = checkList(Token);
			if(matchRequest != null){ //controlla se il token e' gia' nella coda
				Date reset = new Date(); //automaticamente mette come valore di default la data corrente.
				matchRequest.setTempo(reset);
				System.out.println("Ho aggiornato il valore della data dopo la nuova richiesta al gioco");
				Stamp();
			}else{//altrimenti se non e' gia' contenuto aggiungo l'elemento alla coda.
				insertSorted(x);
				Contatore++;
				System.out.println("valore aggiunto");
				Stamp();
			}
		}finally{
			lock.unlock();
		}
		return null;
	}

	/**
	 * Inserisce una nuova richiesta di match nella lista, ordinata in base al numero di vittorie dell'utente
	 * @param u MatchRequest da inserire
	 */
	public static void insertSorted(MatchRequest u){
		int index=0;
		for (MatchRequest matchRequest : UtentiInAttesa) {
			if(matchRequest.getVittorie()>u.getVittorie()){
				UtentiInAttesa.add(index, u);
				return;
			}
		}
		UtentiInAttesa.add(u);
	}

	/**
	 * Metodo che controlla se l'utente definito dal Token esiste nella Lista
	 * @param Token
	 * @return MatchRequest dell'utente o null se l'utente non esiste nella coda
	 */
	public static MatchRequest checkList(String Token){
		for (MatchRequest matchRequest : UtentiInAttesa) {
			if(matchRequest.getToken().equals(Token)){
				return matchRequest;
			}
		}
		return null;
	}
}


