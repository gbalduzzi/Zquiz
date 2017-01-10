package hello;

public class DomandaModel {
	
	private String Question;
	private String Answer1;
	private String Answer2;
	private String Answer3;
	private String Answer4;
	
	private int id;
	
	private int score;
	private int opponentScore;
	
	public DomandaModel(DomandaSingola x, int s1, int so, int id){
		this.setQuestion(x.getDomanda());
		this.setAnswer1(x.getRisposta1());
		this.setAnswer2(x.getRisposta2());
		this.setAnswer3(x.getRisposta3());
		this.setAnswer4(x.getRisposta4());
		
		this.setScore(s1);
		this.setOpponentScore(so);
	}

	public String getQuestion() {
		return Question;
	}

	public void setQuestion(String question) {
		Question = question;
	}

	public String getAnswer1() {
		return Answer1;
	}

	public void setAnswer1(String answer1) {
		Answer1 = answer1;
	}

	public String getAnswer2() {
		return Answer2;
	}

	public void setAnswer2(String answer2) {
		Answer2 = answer2;
	}

	public String getAnswer3() {
		return Answer3;
	}

	public void setAnswer3(String answer3) {
		Answer3 = answer3;
	}

	public String getAnswer4() {
		return Answer4;
	}

	public void setAnswer4(String answer4) {
		Answer4 = answer4;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getOpponentScore() {
		return opponentScore;
	}

	public void setOpponentScore(int opponentScore) {
		this.opponentScore = opponentScore;
	}
}
