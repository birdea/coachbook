package bd.football.coachbook.except;

public class OutOfMemoryException extends RuntimeException {

	private static final long serialVersionUID = 5346430055565840622L;

	public OutOfMemoryException() {
		super();
	}

	public OutOfMemoryException(Throwable throwable) {
		super(throwable);
	}

	public OutOfMemoryException(String message) {
		super(message);
	}
}
