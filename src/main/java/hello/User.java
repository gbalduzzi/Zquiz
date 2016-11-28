package hello;

public class User {

    
    private final String Username;
    private final String Nome;
    private final String Cognome;
    private final int Vittorie;
    
    public User(String u, String n, String c, int v) {
        this.Username = u ;
        this.Nome = n ;
        this.Cognome = c ;
        this.Vittorie = v;
    }

	public String getUsername() {
		return Username;
	}

	public String getNome() {
		return Nome;
	}

	public String getCognome() {
		return Cognome;
	}

	public int getVittorie() {
		return Vittorie;
	}

    
    
    
}