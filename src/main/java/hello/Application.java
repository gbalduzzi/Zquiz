package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.lang.Thread;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        
        //inizio thread della gestione della coda.
        GestioneCoda gestoreCoda= new GestioneCoda();
        Thread gc = new Thread(gestoreCoda);
        gc.start();
        
        //thread che controlla le partite attive
        GestionePartita gestorePartita= new GestionePartita();
        Thread gp = new Thread(gestorePartita);
        gp.start();
    }
}