package model;

public class Error {

	private int error;
	private String message;
	
	public Error(int e, String m){
		this.setError(e);
		this.setMessage(m);
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
