package hello;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import database.DBQueries;
import utils.SessionGenerator;

/**
 * In questa classe vengono definite le routes delle nostre API
 *
 */


@RestController
public class GreetingController {


	// metodo post per registrazione utente

	@RequestMapping(method= RequestMethod.POST, value = "/register")
	public <T extends Object> T Register(@RequestParam(value="User",defaultValue="" ) String User, @RequestParam(value="Password", defaultValue="") String Password, 
			@RequestParam(value="Nome",defaultValue="" ) String Nome, @RequestParam(value="Cognome",defaultValue="" ) String Cognome ){

		// caso 1 : mancano utente o password
		if(User.equals("") || Password.equals("")){
			Error x = new Error(1, "manca o utente o password");
			return (T)x;
		}

		ResultSet data = DBQueries.getUser(User);
		try {
			//caso 2 : utente già registrato			
			if(data.next()){
				Error x = new Error(1, "L'utente che stai provando a registrare è già presente");
				return (T)x;
			}
			// caso 3 : utente non ancora registrato
			else{
				DBQueries.insertUser(User, Password, Nome, Cognome);

				//genero il token
				String token = User+SessionGenerator.nextSessionId();

				//magari più tardi andrò a controllare se il token appena generato esiste già

				//genero il timestamp x inserire il token.
				java.sql.Timestamp sqlTimestamp= SessionGenerator.getFutureTimestamp(1);
				DBQueries.insertToken(token, User, sqlTimestamp);
				Token t = new Token(token);
				
				return (T)t;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}


	// metodo post per login utente
	@RequestMapping(method= RequestMethod.POST, value = "/authenticate")
	public <T> T Authenticate(@RequestParam(value="User",defaultValue="" ) String User, @RequestParam(value="Password", defaultValue="") String Password){
		String token = null;

		// caso 1 : mancano utente o password
		if(User.equals("") || Password.equals("")){
			Error x = new Error(1, "manca o utente o password");
			return (T)x;
		}
		
		ResultSet data = DBQueries.authUser(User, Password);
		try {
			//caso 2 : utente già registrato
			if(data.next()){
				ResultSet tok = DBQueries.selectToken(User);
				while(tok.next()){
					token = tok.getString("Token");
				}
				
				/*
				 *  E se l'utente fosse registrato ma non avesse un token valido attivo?
				 */
				
				return (T)token;
			}
			// caso 3 : utente non ancora registrato
			else{
				Error x = new Error(1, "utente non esistente, effettuare la registrazione o verificare di avere inserito username e password corretti");
				return (T)x;
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}


	// metodo get per restituire dati utente quando richiesti

	@RequestMapping(method= RequestMethod.GET, value = "/user")
	public User user(@RequestParam(value="User",defaultValue="") String User){

		ResultSet data = DBQueries.getUser(User);
		try {
			if(data.next()){
				String u = data.getString("Username");
				String n = data.getString("Nome");
				String c = data.getString("Cognome");
				int v = data.getInt("Vittorie");
				User ut = new User(u, n, c,v);
				return ut;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//prova a fare la richiesta per una partita
	@RequestMapping(method= RequestMethod.GET, value = "/searchmatch")
	public <T> T SearchMatch(@RequestParam(value="Token",defaultValue="") String Token) throws InterruptedException{

		//controllo se il token esiste nella tabella dei token. (eventualmente salvo l'utente associato al token per controlli successivi).
		// caso token non esistente funzionante
		String User =   DBQueries.getUserFromToken(Token);
		if(User == null){
			Error e = new Error(1, "Token inviato non riconosciuto. Potrebbe essere scaduto");
			return (T)e;
		}

		//bisogna controllare anche se il token inviato è al momento in una tabella della partita attiva.
		// caso altra partita già attiva funzionante
		ResultSet x = DBQueries.getActiveMatchesByToken(Token);
		try {
			if(x.next()){
				Error e2 = new Error(2, "Hai una partita già attiva");
				return (T)e2;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		GestioneCoda.RequestGame(Token); //inserisce nella coda la richiesta e in caso aggiorna
		
		//caso creazione partita se ci sono almeno 2 giocatori funzionante
		ResultSet temp2 = DBQueries.getActiveMatchesByToken(Token); //do per scontato che la partita a questo punto sia appena stata inserita.
		try {
			
			if(temp2.next()){
					Partita p = new Partita(temp2.getInt(1), User.equals(temp2.getString(2))? temp2.getString(3) : temp2.getString(2) );
					/**
					 * genero l'oggetto partita (con già tutte le domande previste)
					 *  e lo aggiungo alla lista delle partite attive.
					 */
					return (T)p;
			}else if(GestioneCoda.CheckCoda(Token)){
				Error e3 = new Error(0, "Stiamo ricercando una partita per te");
				return (T)e3;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
	//Richiesta domanda
	@RequestMapping(method= RequestMethod.GET, value = "/question")
	public <T> T Question(@RequestParam(value="MatchID",defaultValue="") String MatchID, @RequestParam(value="Number",defaultValue="1") String Number, @RequestParam(value="Token",defaultValue="") String Token){
		
		//controllo che siano stati inseriti tutti i campi
		if(MatchID.equals("") || Token.equals("")){
			Error e2= new Error(2, "Non hai inserito tutti i campi, manca o il MatchId o il Token");
			return (T) e2;
		}
		
		
		/**
		 * se la partita è attiva(presente nella lista){
		 * 		Seleziona e invia domanda dall'oggetto partita prelevato dalla lista
		 * }
		 * se partita non attiva(non presente nella lista){
		 * 		error la partita è scaduta cazzone
		 * }
		 */
		
		
		
		return null;
	}

	
}