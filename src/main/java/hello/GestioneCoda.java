package hello;

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
	
	public GestioneCoda(){}
	
	public void run() {
		//codice per gestire la coda
		
		while(true){
			//controllo se qualche timeout è scaduto....
			for (MatchRequest matchRequest : UtentiInAttesa) {
				if(getDateDiff(matchRequest.getTempo())>5){ //se è 5 secondi che non riceve più una richiesta verrà rimosso.
					UtentiInAttesa.remove(matchRequest);
				}
			}
			
			//se si sono loggati in due genera la partita e li meccia
			//metodo per generare la partita una volta passati i due token.  CreatePartita();
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
				
				if(matchRequest.getToken()==Token){
					Date reset = new Date(); //automaticamente mette come valore di default la data corrente.
					matchRequest.setTempo(reset);
					System.out.println("valore aggiornato della richiesta");
				}
				
			}
			
		}else{ //altrimenti se non è già contenuto aggiungo l'elemento alla coda.
			UtentiInAttesa.offer(x);
			System.out.println("valore aaggiunto");
		}
	}
	
	//ControllapartitaAttiva(){
	//metodo che chiama una funzione di controllo del database per cercare se nelle partite c'è già uno user con la partita attiva (stato 1) WriteToMySql.CheckPartitaAttiva(user)
	//controllo se ci sono valori o meno nei resultset e in caso torno anche il matchid che mi servirà dopo....
	
	//metodo checkcoda()
	//mi controlla se l'utente è già nella coda-> contains elemento in coda e tornerà true o false.
}


