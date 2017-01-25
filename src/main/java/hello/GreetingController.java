package hello;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import database.DBQueries;
import model.BaseClass;
import model.CompleteQuestion;
import model.Error;
import model.Match;
import model.Question;
import model.Reply;
import model.Token;
import model.User;
import utils.SessionGenerator;

/**
 * In questa classe vengono definite le routes delle nostre API
 *
 */


@RestController
public class GreetingController {

	// metodo post per registrazione utente
	@CrossOrigin(origins = "*")
	@RequestMapping(method= RequestMethod.POST, value = "/register")
	public BaseClass Register(@RequestParam(value="username",defaultValue="" ) String User, 
										 @RequestParam(value="password", defaultValue="") String Password, 
										 @RequestParam(value="name",defaultValue="" ) String Nome, 
										 @RequestParam(value="surname",defaultValue="" ) String Cognome ){

		// caso 1 : mancano utente o password
		if(User.equals("") || Password.equals("")){
			Error x = new Error(1, "manca o utente o password");
			return x;
		}

		User ut = DBQueries.getUser(User);

		//caso 2 : utente gi� registrato			
		if(ut != null){
			Error x = new Error(1, "L'utente che stai provando a registrare � gi� presente");
			return x;
		}
		// caso 3 : utente non ancora registrato
		else{
			DBQueries.insertUser(User, Password, Nome, Cognome);

			//genero il token
			String token = User+SessionGenerator.nextSessionId();

			//genero il timestamp x inserire il token.
			java.sql.Timestamp sqlTimestamp= SessionGenerator.getFutureTimestamp(1);
			DBQueries.insertToken(token, User, sqlTimestamp);
			Token t = new Token(token);

			return t;
		}
	}


	// metodo post per login utente
	@CrossOrigin(origins = "*")
	@RequestMapping(method= RequestMethod.POST, value = "/authenticate")
	public BaseClass Authenticate(@RequestParam(value="username",defaultValue="" ) String User, 
							  @RequestParam(value="password", defaultValue="") String Password){

		// caso 1 : mancano utente o password
		if(User.equals("") || Password.equals("")){
			Error x = new Error(1, "manca o utente o password");
			return x;
		}

		User ut = DBQueries.authUser(User, Password);

		//caso 2 : utente gi� registrato
		if(ut != null){
			Token tok = DBQueries.selectToken(User);

			/*
			 *  E se l'utente fosse registrato ma non avesse un token valido attivo?
			 */

			return tok;
		}
		// caso 3 : utente non ancora registrato
		else{
			Error x = new Error(1, "utente non esistente, effettuare la registrazione o verificare di avere inserito username e password corretti");
			return x;
		}
	}


	// metodo get per restituire dati utente quando richiesti
	@CrossOrigin(origins = "*")
	@RequestMapping(method= RequestMethod.GET, value = "/user")
	public User user(@RequestParam(value="username",defaultValue="") String User){

		User ut = DBQueries.getUser(User);
		if(ut != null){
			return ut;
		}
		return null;
	}

	//prova a fare la richiesta per una partita
	@CrossOrigin(origins = "*")
	@RequestMapping(method= RequestMethod.GET, value = "/searchmatch")
	public BaseClass SearchMatch(@RequestParam(value="token",defaultValue="") String Token) {

		//controllo se il token esiste nella tabella dei token. (eventualmente salvo l'utente associato al token per controlli successivi).
		// caso token non esistente funzionante
		String User =   DBQueries.getUserFromToken(Token);
		if(User == null){
			Error e = new Error(1, "Token inviato non riconosciuto. Potrebbe essere scaduto");
			return e;
		}

		Match match= QueueController.RequestGame(Token); //inserisce nella coda la richiesta e in caso aggiorna

		if(match != null){

			return match;

		}else{
			return new Error(0, "stiamo cercando un partita per te");
		}
	}


	//Richiesta domanda
	@CrossOrigin(origins = "*")
	@RequestMapping(method= RequestMethod.GET, value = "/question")
	public BaseClass Question(@RequestParam(value="match_id",defaultValue="") String MatchID, 
						  @RequestParam(value="number",defaultValue="1") String Number, 
						  @RequestParam(value="token",defaultValue="") String Token){
		
		System.out.println("Domanda richiesta!");
		System.out.println("matchId:"+MatchID);
		System.out.println("number:"+Number);
		System.out.println("token:"+Token);
		
		//controllo che siano stati inseriti tutti i campi
		if(MatchID.equals("") || Token.equals("") || Number.equals("")){
			Error e= new Error(2, "Non hai inserito tutti i campi, manca o il MatchId o il Token");
			return e;
		}

		String User =   DBQueries.getUserFromToken(Token);
		if(User == null){
			Error e = new Error(1, "Token inviato non riconosciuto. Potrebbe essere scaduto");
			return e;
		}

		int n= Integer.parseInt(Number);
		if(n<1 || n>4){
			Error e = new Error(3, "la domanda non esiste.");
			return e;
		}

		int M = Integer.parseInt(MatchID);
		if(ActiveMatchesController.PartiteAttive.containsKey(M)){
			MatchController x =ActiveMatchesController.PartiteAttive.get(M);

			CompleteQuestion t = x.getDomanda(n);
			if (t == null) {
				Error e = new Error(5, "Non puoi ricevere ora questa domanda... aspetta");
				return e;
			}
			
			Question requestedQuestion = new Question(x.getDomanda(n), x.getScore(Token), (x.getUser1().equals(User))?x.getScore1(1):x.getScore1(0) , n);
			return requestedQuestion;
		}else{
			Error e = new Error(4, "La partita non � attiva");
			return e;
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(method= RequestMethod.POST, value = "/reply")
	public BaseClass QuestionReply(@RequestParam(value="token",defaultValue="" ) String Token, 
											  @RequestParam(value="number", defaultValue="") String Number, 
											  @RequestParam(value="match_id",defaultValue="" ) String MatchId, 
											  @RequestParam(value="reply_n",defaultValue="" ) String ReplyNum ){
		
		//controllo che siano stati inseriti tutti i campi
		if(MatchId.equals("") || Token.equals("") || Number.equals("") || ReplyNum.equals("")){
			Error e2= new Error(2, "Non hai inserito tutti i campi");
			return e2;
		}
		
		int match = Integer.parseInt(MatchId);
		int n = Integer.parseInt(Number);
		int reply = Integer.parseInt(ReplyNum);
		
		//Controllo la validit� del token
		String User =   DBQueries.getUserFromToken(Token);
		if(User == null){
			Error e = new Error(1, "Token inviato non riconosciuto. Potrebbe essere scaduto");
			return e;
		}
		
		//Controllo che il numero di domanda sia corretto
		if(n<1 || n>4){
			Error e = new Error(3, "La domanda "+Number+" non esiste");
			return e;
		}
		
		//Controllo che la partita sia attiva
		if(!ActiveMatchesController.PartiteAttive.containsKey(match)){
			Error e = new Error(4, "La partita selezionata non esiste o non � attiva");
			return e;
			
		} else {
			
			if (!DBQueries.checkReplies(match, User, n)) {
				Error e = new Error(5, "Hai gi� risposto a questa domanda");
				return e;
			}
			
			MatchController Match =ActiveMatchesController.PartiteAttive.get(match);
			
			CompleteQuestion q = Match.getDomandaUnchecked(n);
			
			DBQueries.insertReply(match, User, q.getDomanda_ID(), reply);
			
			Reply r = new Reply();
			
			if (q.getRight_answer() == reply) {
				int score = Match.getScore(Token);
				Match.setScore(score + 50, Token);
				// TODO: score in base al tempo
				
				r.setCorrect(true);
			} else {
				r.setCorrect(false);
			}
			return r;
			
		}
		
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(method= RequestMethod.GET, value = "/endmatch")
	public BaseClass EndMatch(@RequestParam(value="match_id",defaultValue="") String MatchID, 
						  @RequestParam(value="token",defaultValue="") String Token){
		
		//controllo che siano stati inseriti tutti i campi
		if(MatchID.equals("") || Token.equals("")){
			Error e2= new Error(2, "Non hai inserito tutti i campi");
			return e2;
		}
		
		int match_id = Integer.parseInt(MatchID);
		
		//Controllo la validit� del token
		String User =   DBQueries.getUserFromToken(Token);
		if(User == null){
			Error e = new Error(1, "Token inviato non riconosciuto. Potrebbe essere scaduto");
			return e;
		}
		
		return DBQueries.getResultFromMatch(match_id, User);
		
	}


}