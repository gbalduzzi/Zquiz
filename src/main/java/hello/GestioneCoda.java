package hello;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

import database.DBQueries;




public class GestioneCoda implements Runnable {

	static Queue<MatchRequest> UtentiInAttesa = new LinkedList<MatchRequest>();
	static int Contatore=0; //conta gli elementi al momento presenti nella coda

	static ReentrantLock lock = new ReentrantLock();


	MatchRequest t1;
	MatchRequest t2;

	public GestioneCoda(){}

	public void run() {
		//codice per gestire la coda
		while(true){
			//controllo se qualche timeout è scaduto....
			lock.lock();
			try{
				Iterator<MatchRequest> it = UtentiInAttesa.iterator();
				while(it.hasNext()) {
					MatchRequest matchRequest = it.next();
					if(getDateDiff(matchRequest.getTempo())>10000000){ //se è 5 secondi che non riceve più una richiesta verrà rimosso.
						it.remove();
						Contatore--;
						System.out.println("elemento eliminato dalla coda perchè il tempo è scaduto \n");
						Stamp();
					}
				}

				while(Contatore >= 2){
					t1= UtentiInAttesa.remove();
					Contatore--;
					t2 = UtentiInAttesa.remove();
					Contatore--;
					DBQueries.createMatch(t1.getToken(), t2.getToken());
					System.out.println("due elementi sono stati inseriti nella tabella e tolti dalla coda");
					Stamp();
				}
			}
			finally{
				lock.unlock();
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
	public static Partita RequestGame(String Token){

		MatchRequest x = new MatchRequest(Token); //genero la tupla da mettere nella coda.
		lock.lock();
		try{
			Partita newMatch = DBQueries.getActiveMatchesByToken(Token); //do per scontato che la partita a questo punto sia appena stata inserita.

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
				UtentiInAttesa.offer(x);
				Contatore++;
				System.out.println("valore aggiunto");
				Stamp();
			}
		}finally{
			lock.unlock();
		}
		return null;
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


