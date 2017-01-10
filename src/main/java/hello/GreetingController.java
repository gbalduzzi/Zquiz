package hello;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
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

		User ut = DBQueries.getUser(User);

		//caso 2 : utente già registrato			
		if(ut != null){
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

		User ut = DBQueries.authUser(User, Password);

		//caso 2 : utente già registrato
		if(ut != null){
			Token tok = DBQueries.selectToken(User);

			/*
			 *  E se l'utente fosse registrato ma non avesse un token valido attivo?
			 */

			return (T)tok;
		}
		// caso 3 : utente non ancora registrato
		else{
			Error x = new Error(1, "utente non esistente, effettuare la registrazione o verificare di avere inserito username e password corretti");
			return (T)x;
		}
	}


	// metodo get per restituire dati utente quando richiesti

	@RequestMapping(method= RequestMethod.GET, value = "/user")
	public User user(@RequestParam(value="User",defaultValue="") String User){

		User ut = DBQueries.getUser(User);
		if(ut != null){
			return ut;
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

		Partita result= GestioneCoda.RequestGame(Token); //inserisce nella coda la richiesta e in caso aggiorna

		if(result != null){

			return (T) result;

		}else{
			return (T)new Error(0, "stiamo cercando un partita per te");
		}
	}


	//Richiesta domanda
	@RequestMapping(method= RequestMethod.GET, value = "/question")
	public <T> T Question(@RequestParam(value="MatchID",defaultValue="") String MatchID, @RequestParam(value="Number",defaultValue="1") String Number, @RequestParam(value="Token",defaultValue="") String Token){

		//controllo che siano stati inseriti tutti i campi
		if(MatchID.equals("") || Token.equals("") || Number.equals("")){
			Error e2= new Error(2, "Non hai inserito tutti i campi, manca o il MatchId o il Token");
			return (T) e2;
		}

		String User =   DBQueries.getUserFromToken(Token);
		if(User == null){
			Error e = new Error(1, "Token inviato non riconosciuto. Potrebbe essere scaduto");
			return (T)e;
		}

		int n= Integer.parseInt(Number);
		if(n<1 || n>4){
			Error e = new Error(3, "la domanda non esiste.");
			return (T)e;
		}

		int M = Integer.parseInt(MatchID);
		if(GestionePartita.PartiteAttive.containsKey(M)){
			Questions x =GestionePartita.PartiteAttive.get(M);

			DomandaSingola t = x.getDomanda(n);
			if (t == null) {
				Error e = new Error(5, "Non puoi ricevere ora questa domanda... aspetta");
				return (T)e;
			}
			DomandaModel result = new DomandaModel(x.getDomanda(n), x.getScore(Token), (x.getUser1().equals(User))?x.getScore1(1):x.getScore1(0) , n);
			return (T)result;
		}else{
			Error e = new Error(4, "La partita non è attiva");
			return (T)e;
		}

		/**
		 * se la partita è attiva(presente nella lista){
		 * 		Seleziona e invia domanda dall'oggetto partita prelevato dalla lista
		 * }
		 * se partita non attiva(non presente nella lista){
		 * 		error la partita è scaduta cazzone
		 * }
		 */
	}


}