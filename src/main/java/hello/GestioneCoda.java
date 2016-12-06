package hello;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.tomcat.jni.Lock;
import org.hamcrest.core.IsInstanceOf;
import org.springframework.boot.logging.LoggingApplicationListener;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;



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
					WriteToMySql.ConnectionToMySql_CreateMatch(t1.getToken(), t2.getToken());
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
	public static void RequestGame(String Token){

		MatchRequest x = new MatchRequest(Token); //genoro la tupla da mettere nella coda.
		lock.lock();
		try{
			if(UtentiInAttesa.contains(x)){ //se il token inviato è già contenuto nella coda

				for (MatchRequest matchRequest : UtentiInAttesa) { //cerco l'elemento che mi interessa e aggiorno il suo timestamp

					if(matchRequest.getToken().equals(Token)){
						Date reset = new Date(); //automaticamente mette come valore di default la data corrente.
						matchRequest.setTempo(reset);
						System.out.println("valore aggiornato della richiesta");
					}

				}

			}else{//altrimenti se non è già contenuto aggiungo l'elemento alla coda.

				UtentiInAttesa.offer(x);
				Contatore++;
				System.out.println("valore aaggiunto");
				Stamp();
			}
		}finally{
			lock.unlock();
		}
	}


	//metodo checkcoda()
	//mi controlla se l'utente è già nella coda-> contains elemento in coda e tornerà true o false.
	public static boolean CheckCoda(String Token){
		if(UtentiInAttesa.contains(Token)){
			return true;
		}
		return false;
	}
}


