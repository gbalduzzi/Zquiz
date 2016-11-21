package hello;

public class Error {

	private int Err;
	private String Message;
	
	public Error(int e, String m){
		this.setErr(e);
		this.setMessage(m);
	}

	public int getErr() {
		return Err;
	}

	public void setErr(int err) {
		Err = err;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}
	
	
}
