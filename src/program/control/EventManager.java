package program.control;

import info.Author;
import info.Program;

import javax.swing.JOptionPane;

import program.display.Simulation;
import program.tools.Point;
import program.tools.RunTracker;
import program.tools.World;

public class EventManager {

	//Extra commands for 'About' menu
	private static final String PROG_INFO_COMMAND = "Program";
	private static final String AUTHOR_COMMAND = "Author";

	private String exitCommand = "Exit";
	private String lastEventID = "";
	private Simulation simulation;

	//Constructor
	public EventManager(Simulation simulation) {
		this.simulation = simulation;
	}

	//Method that is called by components
	public void events(String ID) {

		lastEventID = ID;

		//Check if the command is the exit command
		if (ID == exitCommand) {
			terminate();
		}

		//Send the command to the appropriate method
		if (ID == Simulation.START_COMMAND) { //PROGRAM
			simulation.start();
		}
		else if (ID == Simulation.PAUSE_COMMAND) {
			simulation.stop();
		}
		else if (ID == Simulation.RESET_COMMAND) {
			reset();
		}
		else if (ID == Simulation.SET_DELAY_COMMAND) {
			setTimerDelay();
		}
		else if (ID == Simulation.RESET_ON_STUCK_COMMAND) {
			simulation.toggleResetWhenStuck();
		}
		else if (ID == Simulation.DISPLAY_GRID) { //END OF PROGRAM
			simulation.toggleGrid();
			simulation.repaint();
		}
		else if (ID == Simulation.MOVEMENT_TYPE_COMMAND) { //SIMULATION
			changeMovementType();
		}
		else if (ID == Simulation.WALLS_COMMAND) {
			simulation.stop();
			simulation.setWallsGetPlaced(
					RunTracker.toggle(simulation.wallsGetPlaced()));
		}
		else if (ID == World.WORLD_SIZE_COMMAND) {
			boolean running = simulation.isRunning();
			simulation.stop();
			Point size = newWorldSize();
			if (size != null) {
				simulation.reset(
						new World(size, simulation.getWorld().getItemLimit()),
						simulation.isDiagMovement());
			}
			else if (size == null && running){
				simulation.start();
			}
		}
		else if (ID == World.ITEM_LIM_COMMAND) {
			boolean running = simulation.isRunning();
			simulation.stop();
			changeItemLimit();
			if (running) {
				simulation.start();
			}
		}
		else if (ID == Simulation.AI_COUNT_COMMAND) { //END OF SIMULATION
			
			boolean running = simulation.isRunning();
			simulation.stop();
			
			//Get user input
			String input = getUserInput("Set AI Count",
					"Enter the number of AIs:");
			int count = -1;
			try {
				count = Integer.parseInt(input);
			}
			catch (java.lang.NumberFormatException e) {
				if (input != null) {
					JOptionPane.showMessageDialog(simulation,
							"There was an error converting "
							+input+" to an integer!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			
			//Change the limit
			simulation.setAICount(count);
			simulation.repaint();
			
			//Continue running the simulation if it was running before
			if (running) {
				simulation.start();
			}
		}
		else if (ID == RunTracker.VEIW_ALL_COMMAND) { //STATS
			simulation.getRunTracker().setVisibility(true);
			simulation.repaint();
		}
		else if (ID == RunTracker.HIDE_ALL_COMMAND) {
			simulation.getRunTracker().setVisibility(false);
			simulation.repaint();
		}
		else if (ID == RunTracker.RUN_NUM_COMMAND) {
			simulation.getRunTracker().toggleRunVisible();
			simulation.repaint();
		}
		else if (ID == RunTracker.ITEMS_C_COMMAND) {
			simulation.getRunTracker().toggleItemsCVisible();
			simulation.repaint();
		}
		else if (ID == RunTracker.AVG_ITEM_PER_RUN_COMMAND) {
			simulation.getRunTracker().toggleAvgItemRun();
			simulation.repaint();
		}
		else if (ID == RunTracker.AVG_DIST_PER_RUN_COMMAND) {
			simulation.getRunTracker().toggleAvgDistToItem();
			simulation.repaint();
		}
		else if (ID == RunTracker.MOVES_VISIBLE_COMMAND) { //END OF STATS
			simulation.getRunTracker().toggleMoves();
			simulation.repaint();
		}
		else if (ID == PROG_INFO_COMMAND) { //ABOUT
			displayProgramInfo();
		}
		else if (ID == AUTHOR_COMMAND) { //END OF ABOUT
			displayAuthorInfo();
		}
	}

	//Method to terminate the program
	private void terminate() {
		System.exit(0);
	}

	//Method that will reset the simulation
	private void reset() {
		World w = new World(simulation.getWorld().getWorldSize(),
				simulation.getWorld().getItemLimit());
		simulation.reset(w, simulation.getAI()[0].isDiagMovement());
	}

	//Method to change the movement type
	private void changeMovementType() {

		//If moving diagonally is true
		if (simulation.isDiagMovement()) {
			simulation.setDiagMovement(false);
		}

		//Can't move diagonally
		else {
			simulation.setDiagMovement(true);
		}
	}

	//Method to set the timer delay
	private void setTimerDelay() {

		//Pause the simulation if it is running
		boolean isRunning = simulation.isRunning();
		if (isRunning) {
			simulation.stop();
		}

		//Get user input
		String text = "Enter the timer delay in milliseconds: ";
		String input = JOptionPane.showInputDialog(simulation, text,
				"Set Timer Delay", JOptionPane.QUESTION_MESSAGE);

		//Try to convert it to an integer
		int delay = 250;
		try {
			delay = Integer.parseInt(input);
		}
		catch (java.lang.NumberFormatException e) { //Error
			delay = simulation.getTimerDelay();

			//Display error message
			if (input != null) {
				JOptionPane.showMessageDialog(simulation, "There was an error "
						+ "converting ''"+input+"'' to an integer.",
						"Conversion Error", JOptionPane.ERROR_MESSAGE);
			}
		}

		//Set the new delay
		simulation.setTimerDelay(delay);

		//Start the simulation again if it was running
		if (isRunning) {
			simulation.start();
		}
	}

	//Method to display program info
	private void displayProgramInfo() {

		JOptionPane.showMessageDialog(simulation, Program.getProgramInfo(),
				"Program Info", JOptionPane.INFORMATION_MESSAGE);
	}

	//Method to display author info
	private void displayAuthorInfo() {

		JOptionPane.showMessageDialog(simulation, Author.getAuthorInfo(),
				"Author Info", JOptionPane.INFORMATION_MESSAGE);
	}

	//Method to get a new world size
	private Point newWorldSize() {

		//Get user input
		String text = "Enter a new world size\n"
				+ "in the format width/height\n"
				+ "Example: ''30/36''";
		String input = getUserInput("Set World Size", text);

		//Check if they entered a valid value
		if (input == null) {
			return null;
		}
		String[] values = input.split("/");
		if (values.length < 2) {
			return null;
		}

		//Try to convert values into integers
		int x = -1, y = -1;
		try {
			x = Integer.parseInt(values[0]);
		}
		catch (java.lang.NumberFormatException e) {
			x = simulation.getWorld().getX();
		}
		try {
			y = Integer.parseInt(values[1]);
		}
		catch (java.lang.NumberFormatException e) {
			y = simulation.getWorld().getY();
		}
		
		//Modify values
		if (x < 0 || x > World.MAX_WORLD_SIZE) { //Check if within range
			x = simulation.getWorld().getX();
		}
		if (y < 0 || y > World.MAX_WORLD_SIZE) { //Check if within range
			y = simulation.getWorld().getY();
		}
		if (x == simulation.getWorld().getX() //They are the same
				&& y == simulation.getWorld().getY()) {
			return null;
		}

		return new Point(x,y);
	}
	
	//Method to change the item limit
	private void changeItemLimit() {
		
		//Get the user input
		String input = getUserInput("Set New Item Limit",
				"Enter a new item limit:");
		
		//Try to convert the input to an integer
		int result;
		try {
			result = Integer.parseInt(input);
			if (result <= 0) {
				JOptionPane.showMessageDialog(simulation,
						"You must enter a positive integer!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		catch (java.lang.NumberFormatException e) {
			if (input != null) {
				JOptionPane.showMessageDialog(simulation, "There was an error "
						+ "converting\n'"+input+"' to an integer!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
			result = -1;
		}
		
		//Only set new value if within range
		if (result > 0 && result < 1000000 &&
				result != simulation.getWorld().getItemLimit()) {
			simulation.reset(new World(simulation.getWorld().getWorldSize(),
					result),simulation.isDiagMovement());
			simulation.repaint();
		}
	}
	
	//Method to get a string input from a JOptionPane
	private String getUserInput(String title, String text) {
		return JOptionPane.showInputDialog(simulation, text, title,
				JOptionPane.QUESTION_MESSAGE);
	}

	//Method to get the ID of the last component event
	public String getLastEventID() {
		return this.lastEventID;
	}

	//Method to return the simulation
	public Simulation getSimulation() {
		return this.simulation;
	}

	//Method to set the exit command
	public void setExitCommand(String command) {
		this.exitCommand = command;
	}

	//Method to get the exit command
	public String getExitCommand() {
		return this.exitCommand;
	}

	public static String getProgInfoCommand() {
		return PROG_INFO_COMMAND;
	}

	public static String getAuthorCommand() {
		return AUTHOR_COMMAND;
	}

}