package bd.football.coachbook.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.graphics.Color;
import android.graphics.Paint;
import bd.football.coachbook.model.PlayerChip;
import bd.football.coachbook.model.PlayerChip.ViewPoint;
import bd.football.coachbook.model.TeamFormation;
import bd.football.coachbook.utils.BLog;

public class PlayerChipHelper {

	public Map<Integer, PlayerChip> playerChipMap = Collections.synchronizedMap(new HashMap<Integer, PlayerChip>());
	public ArrayList<PlayerChip> playerChipList;

	private Paint paintPlayerChipCircleFW = new Paint();
	private Paint paintPlayerChipCircleMF = new Paint();
	private Paint paintPlayerChipCircleDF = new Paint();

	public PlayerChipHelper() {//
		paintPlayerChipCircleFW.setColor(Color.rgb(188, 22, 11));
		paintPlayerChipCircleFW.setAntiAlias(true);
		paintPlayerChipCircleMF.setColor(Color.rgb(88, 122, 11));
		paintPlayerChipCircleMF.setAntiAlias(true);
		paintPlayerChipCircleDF.setColor(Color.rgb(88, 22, 181));
		paintPlayerChipCircleDF.setAntiAlias(true);
		init();
	}

	private void init() {
		lastId = 0;
		lastNumber = 0;
		playerChipMap.clear();
	}

	public void setViewBounds(int width, int height) {
		viewWidth = width;
		viewHeight = height;
		dividerY = (viewHeight - 60) / 60;
	}

	private int viewWidth = 0;
	private int viewHeight = 0;
	private int lastId = 0;
	private int lastNumber = 0;
	private int dividerY = 0;

	public PlayerChip makePlayerChip() {
		PlayerChip item = new PlayerChip();
		item.age = 20;
		item.id = lastId++;
		item.number = lastNumber++;
		item.length = 50;
		int[] xy = validAddPointer();
		item.viewPoint = new ViewPoint(xy[0], xy[1]);
		BLog.e("makePlayerChip x:" + xy[0] + ", y:" + xy[1] + ", lastNumber:" + lastNumber + ", dividerY:" + dividerY);
		return item;
	}

	private int[] validAddPointer() {
		int[] xy = new int[2];
		xy[0] = 60;
		xy[1] = 60 * lastNumber;
		if (xy[1] > viewHeight && dividerY != 0) {
			xy[0] = 60 * (lastNumber / dividerY + 1);
			xy[1] = 60 * (lastNumber % dividerY - 1);
		}
		return xy;
	}

	public void addPlayerChip() {
		if (lastId < 1) {
			lastId = 0;
		}
		if (lastNumber < 1) {
			lastNumber = 0;
		}
		PlayerChip chip = makePlayerChip();
		playerChipMap.put(chip.id, chip);
		refreshList();
	}

	public void removePlayerChip() {
		playerChipMap.remove(lastId--);
		lastNumber--;
		refreshList();
	}
	
	public void clearPlayerChips() {
		init();
		refreshList();
	}

	public void setTeamFormation(TeamFormation formation) {
		init();
		for (int i = 0; i < 10; i++) {
			PlayerChip chip = makePlayerChip();
			playerChipMap.put(chip.id, chip);
		}
		refreshList();
		PlayerChip chip;
		int chipCount = 0;
		int lineCount = formation.fw;
		int chipDistance = viewWidth / lineCount;
		int lineDistance = viewHeight;
		for (int i = 0; i < lineCount; i++) {
			chip = playerChipList.get(chipCount++);
			chip.viewPoint.x = (chipDistance * i) + (chipDistance / 2);
			chip.viewPoint.y = lineDistance / 4 * 1;
			chip.paint = paintPlayerChipCircleFW;
		}
		//
		lineCount = formation.mf;
		chipDistance = viewWidth / lineCount;
		for (int i = 0; i < lineCount; i++) {
			chip = playerChipList.get(chipCount++);
			chip.viewPoint.x = (chipDistance * i) + (chipDistance / 2);
			chip.viewPoint.y = lineDistance / 4 * 2;
			chip.paint = paintPlayerChipCircleMF;
		}
		//
		lineCount = formation.df;
		chipDistance = viewWidth / lineCount;
		for (int i = 0; i < lineCount; i++) {
			chip = playerChipList.get(chipCount++);
			chip.viewPoint.x = (chipDistance * i) + (chipDistance / 2);
			chip.viewPoint.y = lineDistance / 4 * 3;
			chip.paint = paintPlayerChipCircleDF;
		}
	}

	public void refreshList() {
		ArrayList<PlayerChip> list = new ArrayList<PlayerChip>();
		Iterator<PlayerChip> iter = playerChipMap.values().iterator();
		while (iter.hasNext()) {
			list.add(iter.next());
		}
		playerChipList = list;
	}

	public void renewPlayerChip(PlayerChip chip) {
		playerChipMap.put(chip.id, chip);
		refreshList();
	}

	public PlayerChip getPlayerChip(int key) {
		return playerChipMap.get(key);
	}

	public ArrayList<PlayerChip> getPlayerChipList() {
		return playerChipList;
	}
}
