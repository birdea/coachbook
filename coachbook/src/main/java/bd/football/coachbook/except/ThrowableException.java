package bd.football.coachbook.except;

public class ThrowableException extends RuntimeException {
	private static final long serialVersionUID = 3274209726145892954L;
	public ThrowableException() {
		super();
    }
	public ThrowableException(Throwable throwable) {
		super(throwable);
	}
}
