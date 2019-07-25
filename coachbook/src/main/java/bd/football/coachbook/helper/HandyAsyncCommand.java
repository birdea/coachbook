package bd.football.coachbook.helper;

public interface HandyAsyncCommand {
	//-- execute on worker thread	
	boolean executeExceptionSafely() throws Exception;
	//-- execute on ui thread 
	void onResult(boolean result, Exception e);
}
