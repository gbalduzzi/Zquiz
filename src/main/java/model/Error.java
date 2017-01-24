package model;

public class Error {

	private int error;
	private String message;
	
	public Error(int e, String m){
		this.setErr(e);
		this.setMessage(m);
	}

	public int getErr() {
		return error;
	}

	public void setErr(int err) {
		error = err;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
