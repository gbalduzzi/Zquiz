package hello;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private static final String template = "Nome:, %s";
    private static final String template2 = "Cognome:, %s";
    private final AtomicLong counter = new AtomicLong();
    
    

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="Martin") 
    String name, @RequestParam(value="cognome", defaultValue="Cossali") String cognome) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name),
                            String.format(template2, cognome));
        					
    }
    
    @RequestMapping("/zerre")
    public Greeting zerre(@RequestParam(value="name", defaultValue="Davide") 
    String name, @RequestParam(value="cognome", defaultValue="Zerre") String cognome) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name),
                            String.format(template2, cognome));
        					
    }
   
}