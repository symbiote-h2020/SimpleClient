package hr.fer.symbiote.client.model;

public class CoreResponse<T> {
    private int status;
    private String message;
    private T body;
	
    public CoreResponse() {
	}

	public CoreResponse(int status, String message, T body) {
		this.status = status;
		this.message = message;
		this.body = body;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getBody() {
		return body;
	}

	public void setBody(T body) {
		this.body = body;
	}
}
