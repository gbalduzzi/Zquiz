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

	/**private static final String template = "Nome:, %s";
    private static final String template2 = "Cognome:, %s";
    private final AtomicLong counter = new AtomicLong(); **/


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

			// caso 1 : mancano utente o password
			if(User.equals("") || Password.equals("")){
				Error x = new Error(1, "manca o utente o password");
				return (T)x;
			}
			ResultSet data = WriteToMySql.ConnectionToMySql_SelectUtente2(User, Password);
			try {
				//caso 2 : utente già registrato
				if(data.next()){
					Error x = new Error(1, "accesso effettuato");
					return (T)x;
				}
				// caso 3 : utente non ancora registrato
				else{
					Error x = new Error(1, "utente non esistente, effettuare la registrazione");
					return (T)x;
				}
				} 
			catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
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
	
	
}