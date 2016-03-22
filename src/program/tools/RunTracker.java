/* Name RunTracker
 * Author: Devon McGrath
 * Date: 12/15/2015
 * Description: This class keeps track of information about how well the AI
 * is doing.
 */

package program.tools;

public class RunTracker {
	
	//Commands
	public static final String VEIW_ALL_COMMAND = "View All Stats";
	public static final String HIDE_ALL_COMMAND = "Hide All Stats";
	public static final String RUN_NUM_COMMAND = "Toggle Run Number";
	public static final String ITEMS_C_COMMAND = "Toggle Items Collected";
	public static final String AVG_ITEM_PER_RUN_COMMAND =
			"Toggle AVG Item/Run";
	public static final String AVG_DIST_PER_RUN_COMMAND =
			"Toggle AVG Moves to Item";
	public static final String MOVES_VISIBLE_COMMAND = "Toggle Moves";
	
	//Other constants
	private static final byte X_AVG_TO_STUCK = 2;
	
	//Variables
	private int itemsCollected;
	private int totalRuns;
	private int totalItemsCollected;
	private int moves;
	private int movesToPickUp;
	
	private double avgOnLastC;
	
	//Statistic visibilities
	private boolean runNumVisible;
	private boolean itemsColVisible;
	private boolean avgItemRunVisible;
	private boolean avgDistVisible;
	private boolean movesVisible;
	
	//Constructor
	public RunTracker() {
	
		this.itemsCollected = 0;
		this.totalItemsCollected = 0;
		this.totalRuns = 1;
		this.moves = 0;
		this.movesToPickUp = -1;
		
		//Make everything visible to start with
		setVisibility(true);
	}
	
	//Method to increment the total number of moves
	public void move() {
		
		this.moves ++;
	}
	
	//Method to start a new run
	public void newRun() {
		
		if (moves > 0) {
			this.totalRuns ++;
			this.moves = 0;
		}
		this.itemsCollected = 0;
	}
	
	//Method to increment items collected
	public void itemCollected() {
		
		this.itemsCollected ++;
		this.totalItemsCollected ++;
		this.avgOnLastC = averageDistPerRun();
		this.movesToPickUp = moves;
	}
	
	//Method returns true if at least one variable is being displayed
	public boolean areStatsVisible() {
		return (runNumVisible ||
				itemsColVisible ||
				avgItemRunVisible ||
				avgDistVisible ||
				movesVisible);
	}
	
	//Method to return the average number of items collected per run
	public float averagePerRun() {
		return (1.0F*this.totalItemsCollected / this.totalRuns);
	}
	
	//Method to get the average distance to an item
	public float averageDistPerRun() {
		
		//Special case
		if (this.itemsCollected == 0) {
			return -1;
		}
		
		return (1.0F*this.moves / this.itemsCollected);
	}
	
	//Returns the current number of items collected
	public int getItemsCollected() {
		return this.itemsCollected;
	}
	
	//Returns the run number
	public int getRunNumber() {
		return this.totalRuns;
	}
	
	//Returns the number of moves for the current run
	public int getMoveCount() {
		return this.moves;
	}
	
	//Sets the current number of items collected
	public void setItemsCollected(int itemsCollected) {
		this.itemsCollected = itemsCollected;
	}
	
	//Method to set the visibility of all values
	public void setVisibility(boolean isVisible) {
		
		this.runNumVisible = isVisible;
		this.itemsColVisible = isVisible;
		this.avgItemRunVisible = isVisible;
		this.avgDistVisible = isVisible;
		this.movesVisible = isVisible;
	}

	//Getters and setters
	public boolean isRunNumVisible() {
		return runNumVisible;
	}

	public void setRunNumVisible(boolean runNumVisible) {
		this.runNumVisible = runNumVisible;
	}

	public boolean isItemsColVisible() {
		return itemsColVisible;
	}

	public void setItemsColVisible(boolean itemsColVisible) {
		this.itemsColVisible = itemsColVisible;
	}

	public boolean isAvgItemRunVisible() {
		return avgItemRunVisible;
	}

	public void setAvgItemRunVisible(boolean avgItemRunVisible) {
		this.avgItemRunVisible = avgItemRunVisible;
	}

	public boolean isAvgDistVisible() {
		return avgDistVisible;
	}

	public void setAvgDistVisible(boolean avgDistVisible) {
		this.avgDistVisible = avgDistVisible;
	}
	
	public boolean isMovesVisible() {
		return movesVisible;
	}

	public void setMovesVisible(boolean movesVisible) {
		this.movesVisible = movesVisible;
	}

	//Methods to toggle visibility of statistics
	public void toggleRunVisible() {
		this.runNumVisible = toggle(runNumVisible);
	}
	public void toggleItemsCVisible() {
		this.itemsColVisible = toggle(itemsColVisible);
	}
	public void toggleAvgItemRun() {
		this.avgItemRunVisible = toggle(avgItemRunVisible);
	}
	public void toggleAvgDistToItem() {
		this.avgDistVisible = toggle(avgDistVisible);
	}
	public void toggleMoves() {
		this.movesVisible = toggle(movesVisible);
	}
	
	//Method to check if the AIs are stuck
	public boolean isStuck() {
		if (moves > 100 && itemsCollected > 3) {
			return (averageDistPerRun()*moves > avgOnLastC * X_AVG_TO_STUCK*movesToPickUp);
		}
		else { //Isn't enough info to check if they are stuck
			return false;
		}
	}
	
	//Method to call when a boolean's state needs to be switched
	public static boolean toggle(boolean b) {
		return (b) ? false : true;
	}
	
}
