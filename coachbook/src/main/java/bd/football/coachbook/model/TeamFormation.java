package bd.football.coachbook.model;

import java.util.ArrayList;

public enum TeamFormation {
	F4_4_2("4-4-2", 4, 4, 2), F4_3_3("4-3-3", 4, 3, 3), F4_2_4("4-2-4", 4, 2, 4), F3_3_4("3-3-4", 3, 3, 4), F3_4_3("3-4-3", 3, 4, 3), F3_5_2("3-5-2", 3, 5, 2);

	public String name;
	public int df;
	public int mf;
	public int fw;

	TeamFormation(String name, int df, int mf, int fw) {
		this.name = name;
		this.df = df;
		this.mf = mf;
		this.fw = fw;
	}
	
	public static ArrayList<String> getFormationNames(){
		ArrayList<String> names = new ArrayList<String>();
		for(TeamFormation item : TeamFormation.values()){
			names.add(item.name);
		}
		return names;
	}
	
	public static TeamFormation getFormationOrdinally(int position){
		return TeamFormation.values()[position];
	}
}