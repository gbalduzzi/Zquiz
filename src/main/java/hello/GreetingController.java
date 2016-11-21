package hello;

import java.util.concurrent.atomic.AtomicLong;

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
    
    

    /** @RequestMapping(method= RequestMethod.GET, value = "/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="Martin") 
    String name, @RequestParam(value="cognome", defaultValue="Cossali") String cognome) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name),
                            String.format(template2, cognome));
        					
    } **/
    
    /** @RequestMapping("/zerre")
    public Greeting zerre(@RequestParam(value="name", defaultValue="Davide") 	
    String name, @RequestParam(value="cognome", defaultValue="Zerre") String cognome) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name),
                            String.format(template2, cognome));
        					
    } **/
    
    @RequestMapping(method= RequestMethod.POST, value = "/register")
    public <T> T Register(@RequestParam(value="User",defaultValue="" ) String User, @RequestParam(value="Password", defaultValue="") String Password, 
    		@RequestParam(value="Nome",defaultValue="" ) String Nome, @RequestParam(value="Cognome",defaultValue="" ) String Cognome ){
    	
    	if(User.equals("") || Password.equals("")){
    		Error x = new Error(1, "manca o utente o password");
    		return (T)x;
    	}
    	
    	//se ci sono si utente che password:
    	//genero il token
    	Token t = new Token("megazerre");
    	return (T)t;
    }

    
}