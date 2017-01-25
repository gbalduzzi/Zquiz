package model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MatchResult extends BaseClass {
	private int score;
	private int opponent_score;
	
	public MatchResult(int score, int oppScore) {
		this.score = score;
		this.opponent_score = oppScore;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public int getOpponent_score() {
		return opponent_score;
	}
	
	public void setOpponent_score(int opponent_score) {
		this.opponent_score = opponent_score;
	}
	
	public static MatchResult createMatchResultFromResultSet(ResultSet data, String User) {
		int score = 0;
		int oppScore = 0;
		try {
			while (data.next()) {
				System.out.println(data.getString(1));
				if (data.getString("Username").equals(User))
					score = data.getInt("Punteggio");
				else
					oppScore = data.getInt("Punteggio");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return new MatchResult(score, oppScore);
	}
	
	
}
