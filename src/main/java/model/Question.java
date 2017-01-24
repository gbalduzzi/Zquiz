package model;

public class Question {
	
	private String question;
	private String answer_one;
	private String answer_two;
	private String answer_three;
	private String answer_four;
	
	private int question_id;
	
	private int score;
	private int opponent_score;
	
	public Question(CompleteQuestion x, int s1, int so, int id){
		this.setQuestion(x.getQuestion());
		
		this.setAnswer_one(x.getAnswer(1));
		this.setAnswer_two(x.getAnswer(2));
		this.setAnswer_three(x.getAnswer(3));
		this.setAnswer_four(x.getAnswer(4));
		
		this.setScore(s1);
		this.setOpponent_score(so);
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public int getId() {
		return question_id;
	}

	public void setId(int id) {
		this.question_id = id;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getAnswer_one() {
		return answer_one;
	}

	public void setAnswer_one(String answer_one) {
		this.answer_one = answer_one;
	}

	public String getAnswer_two() {
		return answer_two;
	}

	public void setAnswer_two(String answer_two) {
		this.answer_two = answer_two;
	}

	public String getAnswer_three() {
		return answer_three;
	}

	public void setAnswer_three(String answer_three) {
		this.answer_three = answer_three;
	}

	public String getAnswer_four() {
		return answer_four;
	}

	public void setAnswer_four(String answer_four) {
		this.answer_four = answer_four;
	}

	public int getOpponent_score() {
		return opponent_score;
	}

	public void setOpponent_score(int opponent_score) {
		this.opponent_score = opponent_score;
	}
}
