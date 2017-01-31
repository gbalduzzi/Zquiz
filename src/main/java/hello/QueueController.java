package hello;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.crypto.Data;

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
			//controllo se qualche timeout è scaduto....
			lock.lock();
			try{
				Iterator<MatchRequest> it = UtentiInAttesa.iterator();
				while(it.hasNext()) {
					MatchRequest matchRequest = it.next();
					if(getDateDiff(matchRequest.getTempo())>5000){ 
						it.remove();
						Contatore--;
						System.out.println("elemento eliminato dalla coda perchè il tempo è scaduto \n");
						Stamp();
					}
				}

				if(Contatore>2){

					Iterator<MatchRequest> it2 = UtentiInAttesa.iterator();
					MatchRequest precedente = it2.next();
					Date minore;
					while(it2.hasNext()){
						MatchRequest successivo = it2.next();

						int differenza = Math.abs((successivo.getVittorie()-precedente.getVittorie()))*1000;


						if(precedente.getTempoIniziale().before(successivo.getTempoIniziale())){
							minore= precedente.getTempoIniziale();
						} else{
							minore= successivo.getTempoIniziale();
						}

						long dif2= getDateDiff(minore);

						if(differenza/dif2>1){
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

	public static void Stamp(){
		int cont =0; 
		for (MatchRequest matchRequest : UtentiInAttesa) {
			System.out.println("Elem "+ cont +") Token: "+ matchRequest.getToken()+ ", Data: "+ matchRequest.getTempo().toString());
		}
	}

	//calcola la differenza fra due date in secondi
	public static long getDateDiff(Date date) {
		Date temp = new Date(); //di default ha il valore di adesso.
		long diffInMillies = temp.getTime() - date.getTime();
		return diffInMillies;
	}

	//metodo per fare la richiesta di gioco.
	public static Match RequestGame(String Token){

		MatchRequest x = new MatchRequest(Token); //genero la tupla da mettere nella coda.
		lock.lock();
		try{
			Match newMatch = DBQueries.getActiveMatchesByToken(Token); //do per scontato che la partita a questo punto sia appena stata inserita.

			if(newMatch != null){

				return newMatch;

			}

			if(CheckCoda(Token)){ //controlla se il token è già nella coda
				for (MatchRequest matchRequest : UtentiInAttesa) { //cerco l'elemento che mi interessa e aggiorno il suo timestamp
					if(matchRequest.getToken().equals(Token)){
						Date reset = new Date(); //automaticamente mette come valore di default la data corrente.
						matchRequest.setTempo(reset);
						System.out.println("Ho aggiornato il valore della data dopo la nuova richiesta al gioco");
						Stamp();
					}
				}
			}else{//altrimenti se non è già contenuto aggiungo l'elemento alla coda.
				InsertOrdine(x);
				Contatore++;
				System.out.println("valore aggiunto");
				Stamp();
			}
		}finally{
			lock.unlock();
		}
		return null;
	}

	public static void InsertOrdine(MatchRequest u){
		int index=0;
		for (MatchRequest matchRequest : UtentiInAttesa) {
			if(matchRequest.getVittorie()>u.getVittorie()){
				UtentiInAttesa.add(index, u);
			}
			index++;
		}
	}

	//metodo checkcoda()
	//mi controlla se l'utente è già nella coda-> contains elemento in coda e tornerà true o false.
	public static boolean CheckCoda(String Token){
		for (MatchRequest matchRequest : UtentiInAttesa) {
			if(matchRequest.getToken().equals(Token)){
				return true;
			}
		}
		return false;
	}
}


