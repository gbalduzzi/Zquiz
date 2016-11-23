package hello;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;
import java.security.SecureRandom;
import java.math.BigInteger;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mysql.jdbc.Connection;

@RestController
public class GreetingController {

    private static final String template = "Nome:, %s";
    private static final String template2 = "Cognome:, %s";
    private final AtomicLong counter = new AtomicLong();
     
    
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
					WriteToMySql.ConnectionToMySql_InsertElement(User, Password, Nome, Cognome);
					String token = SessionGenerator.nextSessionId(); //genero il token
					Token t = new Token("megazerre");
					return (T)t;
					}
				} catch (SQLException e) {
					e.printStackTrace();
					return null;
				}
}
}