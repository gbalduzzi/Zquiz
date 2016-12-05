package hello;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.hamcrest.core.IsInstanceOf;
import org.springframework.boot.logging.LoggingApplicationListener;

public class GestioneCoda implements Runnable {
	
	static Queue<MatchRequest> UtentiInAttesa = new LinkedList<MatchRequest>();
	static int Contatore=0; //conta gli elementi al momento presenti nella coda
	
	MatchRequest t1;
	MatchRequest t2;
	
	public GestioneCoda(){}
	
	public void run() {
		//codice per gestire la coda
		
		while(true){
			//controllo se qualche timeout è scaduto....
			for (MatchRequest matchRequest : UtentiInAttesa) {
				if(getDateDiff(matchRequest.getTempo())>5){ //se è 5 secondi che non riceve più una richiesta verrà rimosso.
					UtentiInAttesa.remove(matchRequest);
					Contatore--;
				}
			}
			
			while(Contatore >= 2){
				t1= UtentiInAttesa.remove();
				Contatore--;
				t2 = UtentiInAttesa.remove();
				Contatore--;
				WriteToMySql.ConnectionToMySql_CreateMatch(t1.getToken(), t2.getToken());
			}
		}
		
		
	}
	
	//calcola la differenza fra due date in secondi
	public static long getDateDiff(Date date) {
		Date temp = new Date(); //di default ha il valore di adesso.
	    long diffInMillies = temp.getTime() - date.getTime();
	    TimeUnit timeUnit= TimeUnit.SECONDS;
		return timeUnit.convert(diffInMillies,timeUnit);
	}
	
	//mrtodo per fare la richiesta di gioco.
	public static void RequestGame(String Token){
		MatchRequest x = new MatchRequest(Token); //genoro la tupla da mettere nella coda.
		
		if(UtentiInAttesa.contains(x)){ //se il token inviato è già contenuto nella coda
			
			for (MatchRequest matchRequest : UtentiInAttesa) { //cerco l'elemento che mi interessa e aggiorno il suo timestamp
				
				if(matchRequest.getToken().equals(Token)){
					Date reset = new Date(); //automaticamente mette come valore di default la data corrente.
					matchRequest.setTempo(reset);
					System.out.println("valore aggiornato della richiesta");
				}
				
			}
			
		}else{ //altrimenti se non è già contenuto aggiungo l'elemento alla coda.
			UtentiInAttesa.offer(x);
			Contatore++;
			System.out.println("valore aaggiunto");
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


