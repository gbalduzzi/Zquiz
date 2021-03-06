package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import database.DBQueries;

import java.lang.Thread;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        
        DBQueries.EndAllMatch();
        
        //inizio thread della gestione della coda.
        QueueController gestoreCoda= new QueueController();
        Thread gc = new Thread(gestoreCoda);
        gc.start();
        
        //thread che controlla le partite attive
        ActiveMatchesController gestorePartita= new ActiveMatchesController();
        Thread gp = new Thread(gestorePartita);
        gp.start();
    }
    
    
}