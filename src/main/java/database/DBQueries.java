package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Random;

import com.fasterxml.jackson.databind.deser.DataFormatReaders.Match;
import com.mysql.jdbc.PreparedStatement;

import hello.DomandaSingola;
import hello.MatchResult;
import hello.Partita;
import hello.Questions;
import hello.Token;
import hello.User;
import utils.MD5;

public class DBQueries {

	public static void connection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
	}
		

	/*
	 * 
	 * Metodo per inserire utente 
	 * usato nel metodo /register
	 */
	public static void insertUser(String username, String password, String firstName, String secondName){
		password = MD5.encode(password);

		Connection connect = DBConnection.getConnection();
		
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("INSERT INTO utente(Username,Password,Nome,Cognome) VALUES(?,?,?,?)");
			statement.setString(1, username);
			statement.setString(2, password);
			statement.setString(3, firstName);
			statement.setString(4, secondName);
			statement.executeUpdate();
			statement.close();
		} catch(SQLException e) {
			e.printStackTrace();
			DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB		
		} finally {
			DBConnection.closeConnection(connect);
		}
		
		System.out.println("Utente inserito correttamente nel database :)");
	}


	/*
	 * metodo per inserire token nel database
	 * usato nel metodo /register
	 */


	public static void insertToken(String token, String username, Timestamp timestamp){
		
		Connection connect = DBConnection.getConnection();
		
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("INSERT INTO token(Token,Username,FineValidità) VALUES(?,?,?)");
			statement.setString(1, token);
			statement.setString(2, username);
			statement.setTimestamp(3, timestamp);
			statement.executeUpdate();
			statement.close();
			System.out.println("Token inserito correttamente nel database :)");
		} catch (SQLException e) {
			e.printStackTrace();
			DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB		
		} finally {
			DBConnection.closeConnection(connect);
		}
	}



	/*
	 * metodo per cancellare elemento dal database.
	 * 
	 */

	public static void deleteUser(String username){

		Connection connect = DBConnection.getConnection();
		
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("DELETE FROM utente WHERE Username = ?");
			statement.setString(1, username);
			statement.executeUpdate();
			statement.close();
			System.out.println("Utente eliminato correttamente dalla tabella :)");
			//dovrò eliminare anche il token
		} catch (SQLException e) {
			e.printStackTrace();
			DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB		
		} finally {
			DBConnection.closeConnection(connect);
		}
	}

	// metodo che restituisce un utente
	// usato nel metodo /register

	public static User getUser(String utente){
		
		Connection connect = DBConnection.getConnection();
		User ut = null;
		
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("SELECT * FROM utente WHERE Username = ?");
			statement.setString(1, utente);
			ResultSet data = statement.executeQuery();
			ut = User.createUserFromResultSet(data);
		} catch (SQLException e) {
			e.printStackTrace();
			DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB		
		} finally {
			DBConnection.closeConnection(connect);
		}
		
		return ut;

	}

	// metodo select utente che restituisce utente e password di un utente
	// usato nel metodo /authenticate

	public static User authUser(String user, String password){
		password = MD5.encode(password);
		User ut = null;
		Connection connect = DBConnection.getConnection();
		
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("SELECT * FROM utente WHERE Username = ? AND Password = ?");
			statement.setString(1, user);
			statement.setString(2, password);
			ResultSet data = statement.executeQuery();
			ut = User.createUserFromResultSet(data);
		} catch (SQLException e) {
			e.printStackTrace();
			DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB		
		} finally {
			DBConnection.closeConnection(connect);
		}
		return ut;
	}

	// metodo che restituisce il token legato ad un utente
	// usato nel metodo /authenticate

	public static Token selectToken(String user){

		Token t = null;
		Connection connect = DBConnection.getConnection();
		
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("SELECT Token FROM token WHERE Username = ?");
			statement.setString(1, user);
			ResultSet data = statement.executeQuery();
			t = Token.createTokenFromResultSet(data);
		} catch (SQLException e) {
			e.printStackTrace();
			DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB		
		} finally {
			DBConnection.closeConnection(connect);
		}
		return t;
	}

	// metodo che restituisce il token l'utente legato al token
	// usato nel metodo check partita attiva

	public static String getUserFromToken(String token){
		String s = null;

		Connection connect = DBConnection.getConnection();
		
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("SELECT Username FROM token WHERE Token = ?");
			statement.setString(1, token);
			ResultSet data = statement.executeQuery();
			while(data.next()){
				s= data.getString("Username");
			}
			connect.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB		
		} finally {
			DBConnection.closeConnection(connect);
		}
		
		return s;
	}

	//CheckPartitaAttiva(){
	//metodo che torna le partite associate(resultset) al token con stato uguale a 1 (il valore che ricevo è lo user)
	//torna tutti gli elementi della riga.

	public static Partita getActiveMatchesByToken(String token){
		String utente = DBQueries.getUserFromToken(token);

		Connection connect = DBConnection.getConnection();
		Partita p = null;
		
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("SELECT * FROM partita WHERE Status =? AND (Username1= ? OR Username2=?)");
			statement.setInt(1, 1);
			statement.setString(2, utente);
			statement.setString(3, utente);
			ResultSet data = statement.executeQuery();
			p = Partita.createPartitaFromResultSet(data, utente);
		} catch (SQLException e) {
			e.printStackTrace();
			DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB		
		} finally {
			DBConnection.closeConnection(connect);
		}
		
		return p;

	}


	//CreatePartita()
	//metodo che riceve in ingresso i due token.. genera un matchid casuale e inserisce la partita nella tabella

	public static void createMatch(String token1, String token2){
		
		String tok1 = getUserFromToken(token1);
		String tok2 = getUserFromToken(token2);

		Connection connect = DBConnection.getConnection();
		
		Random randomGenerator = new Random();
		int match_ID = randomGenerator.nextInt(100);
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("INSERT INTO partita(Match_ID,Username1,Username2,Status,Inizio) VALUES(?,?,?,?,?)");
			statement.setInt(1, match_ID);
			statement.setString(2, tok1);
			statement.setString(3, tok2);
			statement.setInt(4, 1);
			statement.setTimestamp(5,new Timestamp(System.currentTimeMillis())); // crea data attuale
			statement.executeUpdate();
			statement.close();
			System.out.println("Match inserito :)");
		} catch (SQLException e) {
			e.printStackTrace();
			DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB		
		} finally {
			DBConnection.closeConnection(connect);
		}
	}
	
    public static Questions selectDomande(String t1, String t2){

        String u1 = DBQueries.getUserFromToken(t1);
        String u2 = DBQueries.getUserFromToken(t2);
        Connection connect = DBConnection.getConnection();
        Questions q = null;
        try {
            PreparedStatement statement = (PreparedStatement) connect.prepareStatement("SELECT * FROM domanda ORDER BY RAND() LIMIT 4");
            ResultSet data = statement.executeQuery();
                DomandaSingola d1 = DomandaSingola.createDomandaSingolaFromResultSet(data);
                DomandaSingola d2 = DomandaSingola.createDomandaSingolaFromResultSet(data);
                DomandaSingola d3 = DomandaSingola.createDomandaSingolaFromResultSet(data);
                DomandaSingola d4 = DomandaSingola.createDomandaSingolaFromResultSet(data);
                System.out.println(d1.getDomanda());
                System.out.println(d2.getDomanda());
                System.out.println(d3.getDomanda());
                System.out.println(d4.getDomanda());
                q = new Questions(d1, d2, d3, d4,u1,u2);
        } catch (SQLException e) {
            e.printStackTrace();
            DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB        
        } finally {
            DBConnection.closeConnection(connect);
        }
        return q;
    }



	//CreatePartita()
	//metodo che riceve in ingresso i due token.. genera un matchid casuale e inserisce la partita nella tabella

	public static void EndMatch(int match_id, String winner){

		Connection connect = DBConnection.getConnection();
		
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("UPDATE partita SET Status = 0, Vincitore_ID = ?  WHERE Match_ID = ?  ");
			statement.setString(1, winner);
			statement.setInt(2, match_id);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB		
		} finally {
			DBConnection.closeConnection(connect);
		}
	}

	public static void insertReply(int MatchId, String User, int Domanda, int Risposta){

		Connection connect = DBConnection.getConnection();
		
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("INSERT INTO risposte(Match_ID,Username,Domanda_ID,Risposta) VALUES(?,?,?,?)");
			statement.setInt(1, MatchId);
			statement.setString(2, User);
			statement.setInt(3, Domanda);
			statement.setInt(4, Risposta);
			statement.executeUpdate();
			statement.close();
		} catch(SQLException e) {
			e.printStackTrace();
			DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB		
		} finally {
			DBConnection.closeConnection(connect);
		}
	}
	
	public static boolean checkReplies(int MatchId, String User, int Domanda){

		Connection connect = DBConnection.getConnection();
		
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("SELECT count(*) FROM `risposte` WHERE `Username` = ? AND `Match_ID` = ?");
			statement.setInt(2, MatchId);
			statement.setString(1, User);
			ResultSet data = statement.executeQuery();
			int total = 0;
			if(data.next()) {
				total = data.getInt(1);
			}
			
			statement.close();
			if (total < Domanda) return true;
			return false;
					
		} catch(SQLException e) {
			e.printStackTrace();
			DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB		
		} finally {
			DBConnection.closeConnection(connect);
		}
		
		return false;
	}
	
	
	public static void insertResult(String User, int MatchId, int score){

		Connection connect = DBConnection.getConnection();
		
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("INSERT INTO risultati(Username,Match_ID,Punteggio) VALUES(?,?,?)");
			statement.setString(1, User);
			statement.setInt(2, MatchId);
			statement.setInt(3, score);
			statement.executeUpdate();
			statement.close();
		} catch(SQLException e) {
			e.printStackTrace();
			DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB		
		} finally {
			DBConnection.closeConnection(connect);
		}
	}

	// metodo get result from match( match , user)
	/*
	 * selezionare risultato e utente relativo al match
	 * e li passo al metodo creatematchresultfromresult set
	 */
	
	public static MatchResult getResultFromMatch(int MatchId, String User){

		Connection connect = DBConnection.getConnection();
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("SELECT * FROM risultati Where Match_ID = ?");
			statement.setInt(1, MatchId);
			ResultSet data = statement.executeQuery();
			return MatchResult.createMatchResultFromResultSet(data, User);
		} catch (SQLException e) {
			e.printStackTrace();
			DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB		
		} finally {
			DBConnection.closeConnection(connect);
		}
		return null;
	}

	

	
	
	public static void main(String[] args) {
		//insertUser("BLABLA", "PROVA", "PINCO", "PALLO");
		
		//ConnectionToMySql_InsertElement("GBalduz", "clusone", "Giorgio", "Balduzzi");
		//ConnectionToMySql_InsertElement("DBertoc", "castione", "Danilo", "Bertocchi");
		//ConnectionToMySql_InsertElement("Dave94", "zerrone", "Davide", "Zerre");
		//ConnectionToMySql_InsertElement("Martinparre2", "Juventus", "Martin", "Cossali");
		//ConnectionToMySql_DeleteElement("Martinparre2");
		//ConnectionToMySql_GetElement();
		//ConnectionToMySql_SelectUtente("Martinparre");
		//ConnectionToMySql_SelectUtente("DBertoc");
		//ConnectionToMySql_InsertToken("zerrone", "dave94", sqlTimestamp);

		//ConnectionToMySql_SelectUtente2("martinparre", "juventus"); 
		//ConnectionToMySql_SelectUtente2("g.balduz","clusone"); 
		//ConnectionToMySql_SelectToken("martinparre");

		//ConnectionToMySql_CheckPartitaAttiva("martinparres1jbl49tdarf4g3qt02va5qt3b");

		// funzionano le 2 righe sotto
		//String x =ConnectionToMySql_SelectUsername("martinparres1jbl49tdarf4g3qt02va5qt3b");
		//System.out.println(x);

		//System.out.println("Partita martinparre:");
		/*getActiveMatchesByToken("martinparres1jbl49tdarf4g3qt02va5qt3b");
		System.out.println("partita attiva dave94:");
		getActiveMatchesByToken("dave941t1j63ivum2takn1g2rv0dmmgg");
		System.out.println("Partita attiva d.bertoc:");
		getActiveMatchesByToken("d.bertoc8d5uf5ju8dm8p83vvmauub0kgj");

		System.out.println("crea partita tra giorgio e nannini");
		createMatch("g.balduzjoebtdp6k96q4ogrdnks74f522", "nanniman7shrs0kqb4ti7ohhlfufev01f3");
		System.out.println("crea partita tra danilo e tia");
		createMatch("d.bertoc8d5uf5ju8dm8p83vvmauub0kgj", "tiapera7cjsm2v1per79mgna4inmi4gm8");
	*/
		
		//insertResult("martinparre", 83, 200);
		
		EndMatch(83, "martinparre");
		
	}

}
