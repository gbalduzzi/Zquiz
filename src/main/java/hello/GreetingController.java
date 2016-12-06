package hello;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.crypto.Data;

import java.security.SecureRandom;
import java.math.BigInteger;
import java.nio.charset.Charset;

import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.format.datetime.joda.DateTimeParser;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mysql.jdbc.Connection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@RestController
public class GreetingController {


	// metodo post per registrazione utente

	@RequestMapping(method= RequestMethod.POST, value = "/register")
	public <T> T Register(@RequestParam(value="User",defaultValue="" ) String User, @RequestParam(value="Password", defaultValue="") String Password, 
			@RequestParam(value="Nome",defaultValue="" ) String Nome, @RequestParam(value="Cognome",defaultValue="" ) String Cognome ){

		// caso 1 : mancano utente o password
		if(User.equals("") || Password.equals("")){
			Error x = new Error(1, "manca o utente o password");
			return (T)x;
		}

		ResultSet data = WriteToMySql.ConnectionToMySql_SelectUtente2(User);
		try {
			//caso 2 : utente già registrato			
			if(data.next()){
				Error x = new Error(1, "L'untente che stai provando a registrare è già presente");
				return (T)x;
			}
			// caso 3 : utente non ancora registrato
			else{
				WriteToMySql.ConnectionToMySql_InsertElement(User, md5(Password), Nome, Cognome);

				//genero il token
				String token = User+SessionGenerator.nextSessionId();

				//magari più tardi andrò a controllare se il token appena generato esiste già

				//genero il timestamp x inserire il token.
				java.util.Date date = new java.util.Date();
				date= GreetingController.addDays(date, 1);
				long time= date.getTime();
				java.sql.Timestamp sqlTimestamp= new java.sql.Timestamp(time);
				WriteToMySql.ConnectionToMySql_InsertToken(token, User, sqlTimestamp);
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
		ResultSet data = WriteToMySql.ConnectionToMySql_SelectUtente2(User, Password);
		try {
			//caso 2 : utente già registrato
			if(data.next()){
				ResultSet tok = WriteToMySql.ConnectionToMySql_SelectToken(User);
				while(tok.next()){
					token = tok.getString("Token");
				}
				//Error x = new Error(0, "accesso effettuato come "+User+"  Token: "+token);
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

		ResultSet data = WriteToMySql.ConnectionToMySql_SelectUtenteCompleto(User);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}




	//metodo per aggiungere 30g alla data.
	public static Date addDays(Date d, int mese)
	{
		d.setMonth(d.getMonth() + mese);
		return d;
	}

	// metodo per criptare la stringa in md5

	public static String md5(String source) {
		String md5 = null;
		try {
			MessageDigest mdEnc = MessageDigest.getInstance("MD5"); //Encryption algorithm
			mdEnc.update(source.getBytes(), 0, source.length());
			md5 = new BigInteger(1, mdEnc.digest()).toString(16); // Encrypted string
		} 
		catch (Exception ex) {
			return null;
		}
		return md5;
	}
	
	
	//prova a fare la richiesta per una partita

	@RequestMapping(method= RequestMethod.GET, value = "/searchmatch")
	public <T> T SearchMatch(@RequestParam(value="Token",defaultValue="") String Token) throws InterruptedException{

		//controllo se il token esiste nella tabella dei token. (eventualmente salvo l'utente associato al token per controlli successivi).
		String User =   WriteToMySql.ConnectionToMySql_SelectUsername(Token);
		if(User == null){
			Error e = new Error(1, "Token inviato non riconosciuto. Potrebbe essere scaduto");
			return (T)e;
		}


		//bisogna controllare anche se il token inviato è al momento in una tabella della partita attiva.
		ResultSet x = WriteToMySql.ConnectionToMySql_CheckPartitaAttiva(Token);
		try {
			if(x.next()){
				Error e2 = new Error(2, "Hai una partita già attiva");
				return (T)e2;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		GestioneCoda.RequestGame(Token); //inserisce nella coda la richiesta e in caso aggiorna

		/**
		 * metodo per aspettare un secondo, così do il tempo di inserire la partita.
		 * if(writesoMysql.CheckPartitaAttiva() != null){
		 * 		json partita trovata -> matchID e avversario che prendo dal resultset.
		 * } else(se partiota in cosa){
		 * 		json -> aspetta stronzo sta cercando una partita
		 * }
		 */
		Thread.sleep(500);
		ResultSet temp2 = WriteToMySql.ConnectionToMySql_CheckPartitaAttiva(Token); //do per scontato che la partita a questo punto sia appena stata inserita.
		try {
			if(temp2.next()){
					Partita p = new Partita(temp2.getInt(1), User.equals(temp2.getString(2))? temp2.getString(3) : temp2.getString(2) );
					return (T)p;
			}else if(GestioneCoda.CheckCoda(Token)){
				Error e3 = new Error(0, "Stismo ricercando una partita per te");
				return (T)e3;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}