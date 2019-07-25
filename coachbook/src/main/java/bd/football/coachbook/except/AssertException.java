package bd.football.coachbook.except;

public class AssertException extends RuntimeException {
	
	private static final long serialVersionUID = 6220772000434565077L;

	public AssertException() {
	}

	public AssertException(String paramString) {
		super(paramString);
	}

	public AssertException(Throwable paramThrowable) {
		super(paramThrowable);
	}

	public static void assertNotNull(Object paramObject) {
		if (paramObject != null)
			return;
		throw new AssertException();
	}

	public static void assertNotNull(String paramString, Object paramObject) {
		if (paramObject != null)
			return;
		throw new AssertException(paramString);
	}

	public static void assertTrue(boolean paramBoolean) {
		if (paramBoolean)
			return;
		throw new AssertException();
	}

}
