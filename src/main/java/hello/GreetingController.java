package hello;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.crypto.Data;

import java.security.SecureRandom;
import java.math.BigInteger;

import org.springframework.format.datetime.joda.DateTimeParser;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mysql.jdbc.Connection;

@RestController
public class GreetingController {

	/**private static final String template = "Nome:, %s";
    private static final String template2 = "Cognome:, %s";
    private final AtomicLong counter = new AtomicLong(); **/


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
			//caso 2 : utente gi� registrato			
			if(data.next()){
				Error x = new Error(1, "L'untente che stai provando a registrare � gi� presente");
				return (T)x;
			}
			// caso 3 : utente non ancora registrato
			else{
				WriteToMySql.ConnectionToMySql_InsertElement(User, Password, Nome, Cognome);

				//genero il token
				String token = SessionGenerator.nextSessionId();

				//magari pi� tardi andr� a controllare se il token appena generato esiste gi�

				//genero il timestamp x inserire il token.
				java.util.Date date = new java.util.Date();
				System.out.println(date.toString() + " ->data prima il cambio");
				date= GreetingController.addDays(date, 1);
				long time= date.getTime();
				java.sql.Timestamp sqlTimestamp= new java.sql.Timestamp(time);
				System.out.println(date.toString() + " ->data dopo il cambio");
				WriteToMySql.ConnectionToMySql_InsertToken(token, User, sqlTimestamp);

				Token t = new Token(token);
				return (T)t;
			}
		} catch (SQLException e) {
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
}