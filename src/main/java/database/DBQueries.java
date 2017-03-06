package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Random;
import com.mysql.jdbc.PreparedStatement;

import hello.MatchController;
import model.CompleteQuestion;
import model.Match;
import model.MatchResult;
import model.Token;
import model.User;
import utils.MD5;

/**
 * @author gioba
 *
 */
public class DBQueries {

	/**
	 * Testa la connessione con il DB
	 */
	public static void connection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Inserisce nel database un nuovo utente
	 * @param username
	 * @param password
	 * @param firstName
	 * @param secondName
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


	/**
	 * Inserisce nel database un nuovo token
	 * @param token
	 * @param username
	 * @param timestamp
	 */
	public static void insertToken(String token, String username, Timestamp timestamp){
		
		Connection connect = DBConnection.getConnection();
		
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("INSERT INTO token(Token,Username,FineValidita) VALUES(?,?,?)");
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


	/**
	 * Elimina un utente dal database
	 * @param username
	 */
	public static void deleteUser(String username){

		Connection connect = DBConnection.getConnection();
		
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("DELETE FROM utente WHERE Username = ?");
			statement.setString(1, username);
			statement.executeUpdate();
			statement.close();
			System.out.println("Utente eliminato correttamente dalla tabella :)");
			//devo eliminare anche il token
		} catch (SQLException e) {
			e.printStackTrace();
			DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB		
		} finally {
			DBConnection.closeConnection(connect);
		}
	}

	/**
	 * Ottiene i dati del DB per l'utente richiesto
	 * @param utente username dell'utente
	 * @return
	 */
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

	/**
	 * Seleziona un utente da username e password (utilizzato per il login utente)
	 * @param user Username inserito dall'utente
	 * @param password Password inserita dall'utente
	 * @return User se presente in DB oppure null
	 */
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

	/**
	 * Restituisce il token legato ad un utente
	 * @param user Username dell'utente
	 * @return Token dell'utente
	 */
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

	/**
	 * Restituisce l'username dell'utente legato al token
	 * @param token
	 * @return username oppure null
	 */
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

	/**
	 * Restituisce il match attivo di un utente
	 * @param token
	 * @return Match attivo oppure null
	 */
	public static Match getActiveMatchesByToken(String token){
		String utente = DBQueries.getUserFromToken(token);

		Connection connect = DBConnection.getConnection();
		Match p = null;
		
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("SELECT * FROM partita WHERE Status =? AND (Username1= ? OR Username2=?)");
			statement.setInt(1, 1);
			statement.setString(2, utente);
			statement.setString(3, utente);
			ResultSet data = statement.executeQuery();
			p = Match.createPartitaFromResultSet(data, utente);
		} catch (SQLException e) {
			e.printStackTrace();
			DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB		
		} finally {
			DBConnection.closeConnection(connect);
		}
		
		return p;

	}

	/**
	 * Inserisce in Database un match tra i due utenti selezionati
	 * @param token1 Token utente 1
	 * @param token2 Token utente 2
	 */
	public static void createMatch(String token1, String token2){
		
		String tok1 = getUserFromToken(token1);
		String tok2 = getUserFromToken(token2);

		Connection connect = DBConnection.getConnection();
		
		Random randomGenerator = new Random();
		int match_ID = randomGenerator.nextInt(Integer.MAX_VALUE);
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
	
    /**
     * Crea l'oggetto di gestione delle domande 
     * @param t1 Token utente 1
     * @param t2 Token utente 2
     * @return MatchController con le domande per la partita
     */
    public static MatchController selectDomande(String t1, String t2){

        String u1 = DBQueries.getUserFromToken(t1);
        String u2 = DBQueries.getUserFromToken(t2);
        Connection connect = DBConnection.getConnection();
        MatchController q = null;
        try {
            PreparedStatement statement = (PreparedStatement) connect.prepareStatement("SELECT * FROM domanda ORDER BY RAND() LIMIT 4");
            ResultSet data = statement.executeQuery();
                CompleteQuestion d1 = CompleteQuestion.createDomandaSingolaFromResultSet(data);
                CompleteQuestion d2 = CompleteQuestion.createDomandaSingolaFromResultSet(data);
                CompleteQuestion d3 = CompleteQuestion.createDomandaSingolaFromResultSet(data);
                CompleteQuestion d4 = CompleteQuestion.createDomandaSingolaFromResultSet(data);
                System.out.println(d1.getQuestion());
                System.out.println(d2.getQuestion());
                System.out.println(d3.getQuestion());
                System.out.println(d4.getQuestion());
                q = new MatchController(d1, d2, d3, d4,u1,u2);
        } catch (SQLException e) {
            e.printStackTrace();
            DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB        
        } finally {
            DBConnection.closeConnection(connect);
        }
        return q;
    }

	/**
	 * Chiude una partita attiva
	 * @param match_id ID del match da chiudere
	 * @param winner username del vincitore
	 */
	public static void EndMatch(int match_id, String winner){
		
		DBQueries.IncrementWin(winner);
		
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

	/**
	 * Inserisce in DB la risposta ad una domanda
	 * @param MatchId
	 * @param User Username dell'utente che ha risposto
	 * @param Domanda Numero della domanda nella partita (compresa tra 1 e 4)
	 * @param Risposta Numero dell'opzione selezionata dall'utente (compreso tra 1 e 4)
	 */
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
	
	/**
	 * Controlla se l'utente ha una risposta alla stessa domanda presente in DB
	 * @param MatchId
	 * @param User
	 * @param Domanda Numero della domanda nella partita (compresa tra 1 e 4)
	 * @return True se la domanda ha una risposta presente in DB, False altrimenti
	 */
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
	
	
	/**
	 * Inserisce in DB i risultati finali della partita
	 * @param User Username dell'utente
	 * @param MatchId
	 * @param score Punteggio dell'utente
	 */
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
	
	/**
	 * Ottiene il risultato di un match per un utente
	 * @param MatchId
	 * @param User
	 * @return MatchResult della partita richiesta o null se la partita non esiste
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

	/**
	 * Incrementa il contatore delle vittorie per il dato utente
	 * @param User
	 */
	public static void IncrementWin(String User){

		Connection connect = DBConnection.getConnection();
		System.out.println("Aumento vittorie per "+User);
		
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("UPDATE utente SET Vittorie = Vittorie +1 WHERE Username = ?");
			statement.setString(1, User);
			statement.executeUpdate();
			statement.close();
		} catch(SQLException e) {
			e.printStackTrace();
			DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB		
		} finally {
			DBConnection.closeConnection(connect);
		}
	}
	

	/**
	 * Chiude tutti i match ativi.
	 */
	public static void EndAllMatch(){
		
		Connection connect = DBConnection.getConnection();
		
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("UPDATE partita SET Status = 0 WHERE Status = ?  ");
			statement.setInt(1, 1);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB		
		} finally {
			DBConnection.closeConnection(connect);
		}
	}

	/**
	 * Restituisce il numero di vittorie per l'utente
	 * @param token
	 * @return numero di vittorie dell'utente
	 */
	public static int getWin(String token){
		String utente = DBQueries.getUserFromToken(token);

		Connection connect = DBConnection.getConnection();
		int vittorie = 0;
		try {
			PreparedStatement statement = (PreparedStatement) connect.prepareStatement("SELECT Vittorie FROM utente WHERE Username = ?");
			statement.setString(1, utente);
			ResultSet data = statement.executeQuery();
			while(data.next()){
			vittorie = data.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			DBConnection.rollbackConnection(connect); //Evito di lasciare operazioni "parziali" sul DB		
		} finally {
			DBConnection.closeConnection(connect);
		}
		return vittorie;
	}

}
