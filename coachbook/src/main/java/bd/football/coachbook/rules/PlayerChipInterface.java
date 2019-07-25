package bd.football.coachbook.rules;

public interface PlayerChipInterface {

	public void addPlayerChip();
	public void addPlayerChip(int id);
	public void removePlayerChip();
	public void removePlayerChip(int id);
	public void clearPlayerChips();
	
	public void changeBackgroundImage(int resId);
}
