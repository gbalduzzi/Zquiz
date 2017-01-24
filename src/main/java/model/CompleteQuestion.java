package model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CompleteQuestion {

	private int question_id;
	private String question;
	private String[] answers = new String[4];
	private int right_answer;
	
	public CompleteQuestion(int id,String Domanda, String r1, String r2, String r3, String r4 , int giusta){
		//assegno.
		this.question_id = id;
		this.setQuestion(Domanda);
		this.answers[0] = r1;
		this.answers[1] = r2;
		this.answers[2] = r3;
		this.answers[3] = r4;
		this.setRight_answer(giusta);
	}
	
	public String getQuestion(){
		return question;
	}
	
	public void setQuestion(String question) {
		this.question = question;
	}
	
	
	public String getAnswer(int i){
		return answers[i-1];
	}
	
	public static CompleteQuestion createDomandaSingolaFromResultSet(ResultSet data) {
		try {
			if (data.next()) {
				CompleteQuestion d = new CompleteQuestion(data.getInt(1), data.getString(2), data.getString(4), data.getString(5), data.getString(6), data.getString(7), data.getInt(8));
				return d;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public int getDomanda_ID() {
		return question_id;
	}

	public void setDomanda_ID(int domanda_ID) {
		question_id = domanda_ID;
	}

	public int getRight_answer() {
		return right_answer;
	}

	public void setRight_answer(int right_answer) {
		this.right_answer = right_answer;
	}
}
