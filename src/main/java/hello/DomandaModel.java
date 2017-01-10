package hello;

public class DomandaModel {
	
	private String Question;
	private String Answer1;
	private String Answer2;
	private String Answer3;
	private String Answer4;
	
	int id;
	
	int score;
	int opponentScore;
	
	public DomandaModel(DomandaSingola x, int s1, int so, int id){
		this.Question= x.getDomanda();
		this.Answer1= x.getRisposta1();
		this.Answer2= x.getRisposta2();
		this.Answer3=x.getRisposta3();
		this.Answer4=x.getRisposta4();
		
		this.score= s1;
		this.opponentScore= so;
	}
}
