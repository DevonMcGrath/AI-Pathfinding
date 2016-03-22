//Name: Simulation
//Author: Devon McGrath
//Date: 07/12/2015
//Description: This class handles all of the graphics and timer events.

//Package for the display
package program.display;

import javax.swing.*;

import program.AI.*;
import program.tools.Point;
import program.tools.RunTracker;
import program.tools.World;

import java.awt.*;
import java.awt.event.*;

public class Simulation extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//Commands
	public static final String START_COMMAND = "Start";
	public static final String PAUSE_COMMAND = "Stop";
	public static final String RESET_COMMAND = "Reset";
	public static final String MOVEMENT_TYPE_COMMAND =
			"Toggle Diagonal Movement";
	public static final String SET_DELAY_COMMAND = "Set Timer Delay";
	public static final String RESET_ON_STUCK_COMMAND =
			"Toggle Reset When Stuck";
	public static final String DISPLAY_GRID = "Toggle grid";
	
	public static final String WALLS_COMMAND = "Toggle walls";
	public static final String AI_COUNT_COMMAND = "Change AI count";
	
	//Variables
	private RunTracker run = new RunTracker();
	private Timer time;
	private Point offset;
	private AI[] ai;
	private int aiCount;
	private World world;
	private DisplayMenu menu;
	private boolean resetsWhenStuck;
	private boolean wallsGetPlaced = true;
	private boolean showGrid;

	//Constructor (sets time as well as other basics)
	public Simulation(World world, int delay) {

		//Initialize
		this.offset = new Point(50,50);
		this.showGrid = true;
		setTimerDelay(delay);
		this.resetsWhenStuck = false;
		this.aiCount = 1;
		initialize(world, true);
		this.setBackground(Color.BLACK);
		this.addMouseListener(new MouseEvents());
		
	}

	//Basic constructor
	public Simulation(World world) {

		//Initialize
		this.offset = new Point(50,50);
		this.showGrid = true;
		this.time = new Timer(250, this);
		this.resetsWhenStuck = false;
		this.aiCount = 1;
		initialize(world, true);
		this.setBackground(Color.BLACK);
		this.addMouseListener(new MouseEvents());
	}

	//Method to help initialize the class
	private void initialize(World world, boolean diagMovement) {

		this.world = world;
		this.ai = new AI[aiCount];
		for (int n = 0; n < aiCount; n ++) {
			Point start = World.random(this.world);
			this.ai[n] = new AI(this.world, diagMovement, start);
			this.world.placeAI(start);
		}
		this.world.setWorld();
	}

	//Timer method
	public void actionPerformed(ActionEvent e) {

		//Only try to move AIs if not stuck
		if (!run.isStuck()) {
			
			//Loop though each AI and perform actions
			for (int n = 0; n < aiCount; n ++) {
		
				//Update
				updateWorld(this.ai[n]);

				//Send data to AI
				updateAI(this.ai[n]);
			}
		}

		//Check if the world should be reset
		if (resetsWhenStuck) {
			checkForReset(ai);
		}

		//Repaint the graphics
		repaint();
	}

	//Graphics method
	public void paint(Graphics g1) {
		super.paint(g1);
		Graphics2D g = (Graphics2D)(g1);

		//Do some calculations
		offset = new Point(30,30);
		boolean yIsBetter = (getHeight() - offset.getY()*2) / world.getY()
				>= (getWidth() - offset.getX()*2) / world.getX();
		int boxSize = calculateGridSquareSize();
		if (yIsBetter) {
			offset = new Point(offset.getX(),
					(getHeight()-boxSize*world.getY())/2);
		}
		else {
			offset = new Point((getWidth()-boxSize*world.getX())/2,
					offset.getY());
		}
				
		//Draw grid
		offset.x = (getWidth()-world.getX()*boxSize)/2;
		offset.y = (getHeight()-world.getY()*boxSize)/2;
		if (showGrid) {
			g.setColor(Color.CYAN);
			for(int n = 0; n <= world.getX(); n ++) {
					g.drawRect(offset.getX()+boxSize*n, offset.getY(),
							1, world.getY()*boxSize);
			}
			for(int n = 0; n <= world.getY(); n++) {
				g.drawRect(offset.getX(), offset.getY()+boxSize*n,
						world.getX()*boxSize, 1);
			}
		}
		g.setColor(Color.WHITE);
		g.drawRect(offset.getX(), offset.getY(),
				world.getX()*boxSize+1, world.getY()*boxSize+1);

		//Draw world
		int wallO = 2; //Wall offset
		int itemO = 10; //Item offset
		int AIO = 1; //AI offset
		for(int i = 0; i < world.getX(); i ++){
			for(int j = 0; j < world.getY(); j ++){
				
				//Only try to draw entity if not empty
				if(world.getWorld()[i][j] != World.ID_EMPTY){

					//Draw walls/blocks
					if(world.getWorld()[i][j] == World.ID_BLOCK){
						g.setColor(Color.WHITE);
						g.fillRect(offset.getX()+wallO+i*boxSize,
								offset.getY()+wallO+j*boxSize,
								boxSize-wallO, boxSize-wallO);
					}

					//Draw item(s) to get
					else if(world.getWorld()[i][j] == World.ID_ITEM){
						g.setColor(Color.YELLOW);
						g.fillRoundRect(offset.getX()+itemO/5+i*boxSize,
								offset.getY()+itemO/5+j*boxSize,
								boxSize-itemO/5, boxSize-itemO/5, 10, 10);
						g.setColor(Color.DARK_GRAY);
						if (boxSize-itemO > 0) {
							g.fill3DRect(offset.getX()+itemO/2+1+i*boxSize,
									offset.getY()+itemO/2+1+j*boxSize,
									boxSize-itemO, boxSize-itemO, true);
						}
					}

					//Draw AI
					else if(world.getWorld()[i][j] == World.ID_AI){
						g.setColor(Color.GREEN);
						g.fillOval(offset.getX()+AIO+i*boxSize,
								offset.getY()+AIO+j*boxSize,
								boxSize-AIO,
								boxSize-AIO);
					}
				}
			}
		}

		//Draw statistics
		if (run.areStatsVisible()) {
			Font stats = new Font("Arial", Font.BOLD, 12);
			float avgCharDist = 5.35F;
			String text = createString();
			g.setFont(stats);
			g.setColor(Color.WHITE);
			g.drawString(text, getWidth()/2 - text.length()*avgCharDist/2,
						 offset.getY() - 5);
		}
	}
	
	//Method to create string to draw
	private String createString() {
		
		String output = "";
		
		//If the run number is visible
		if (run.isRunNumVisible()) {
			output += "Run: " + run.getRunNumber() + "   ";
		}
		
		//If the items collected statistic is visible
		if (run.isItemsColVisible()) {
			output += "Items Collected: " + run.getItemsCollected() + "   ";
		}
		
		//If the average items collected per run is visible
		if (run.isAvgItemRunVisible()) {
			output += "Items Collected Per Run (AVG): "
					+ run.averagePerRun() + "   ";
		}
		
		//If the average number of moves to collect an item is visible
		if (run.isAvgDistVisible()) {
			output += "Number of Moves to Item (AVG): "
					+ run.averageDistPerRun() + "   ";
		}
		
		//If the number of moves is visible
		if (run.isMovesVisible()) {
			output += "Moves: " + run.getMoveCount() + "   ";
		}
		
		//Return the resulting string
		return output;
	}
	
	//Calculates the size of the grid squares
	private int calculateGridSquareSize() {
		
		boolean yIsBetter = (getHeight() - offset.getY()*2) / world.getY()
				>= (getWidth() - offset.getX()*2) / world.getX();
		int boxSize = (yIsBetter) ?
				(getWidth() - offset.getX()*2) / world.getX() :
				(getHeight() - offset.getY()*2) / world.getY();
				
		return boxSize;
	}

	//Method that updates the world and what is contained in it
	private void updateWorld(AI ai){

		int itemCount = World.count(world.getWorld(), World.ID_ITEM);

		//If the total item count is < limit; place another one
		if(itemCount < world.getItemLimit()) {

			//Only increment score if the AI moved
			if (ai.getDistFromLastMove() != 0) {
				run.itemCollected();
			}
			
			//Add a wall and item
			if (wallsGetPlaced) {
				world.generateEntites();
			}
			else { //Add an item
				world.addElement(World.ID_ITEM);
			}
			
			//Update the location of the point where the AI last checked
			//if it was stuck
			ai.setUpdatePoint(ai.getCurrent());
		}
	}

	//Method that sends all necessary data to AI (class)
	private void updateAI(AI ai){

		//Move the AI to its new position
		ai.runAI(world.getWorld());
		
		//Keep track of the number of moves
		if (!Point.isEqual(ai.getCurrent(), ai.getLast())) {
			run.move();
		}
	}
	
	//Method to get the timer delay
	public int getTimerDelay() {
		return time.getDelay();
	}

	//Method to set the delay (milliseconds) on the timer
	public void setTimerDelay(int delay) {

		//Only set the timer if the delay is a valid number
		if (delay > 0) {

			//Check if the timer is running
			boolean isRunning = false;
			if (time != null) {
				isRunning = time.isRunning();
				time.stop();
			}

			//Create the new timer
			time = new Timer(delay, this);

			//Start it again if it was running
			if (isRunning) {
				time.start();
			}
		}
	}
	
	//Method to set the number of AIs
	public void setAICount(int aiCount) {
		
		if (aiCount > 0 && aiCount != this.aiCount) {
			
			int original = this.aiCount;
			this.aiCount = aiCount;
			
			//Transfer values (and delete old AIs)
			if (original > aiCount) {
				
				//Store old values
				AI temp[] = new AI[aiCount];
				for (int n = 0; n < aiCount; n ++) {
					temp[n] = ai[n];
				}
				
				//Delete 'excess' AIs
				for (int n = aiCount; n < original; n ++) {
					world.place(ai[n].getCurrent(), World.ID_EMPTY);
					ai[n] = null;
				}
				
				//Create new array
				this.ai = new AI[aiCount];
				for (int n = 0; n < aiCount; n ++) {
					ai[n] = temp[n];
				}
			}
			
			//Transfer values (and add new AIs)
			else {
				
				//Store old values
				AI temp[] = new AI[original];
				for (int n = 0; n < original; n ++) {
					temp[n] = ai[n];
				}
				
				//Create new array
				this.ai = new AI[aiCount];
				for (int n = 0; n < original; n ++) {
					ai[n] = temp[n];
				}
				
				//Add new AIs
				for (int n = original; n < aiCount; n ++) {
					Point start = World.random(this.world);
					this.ai[n] = new AI(this.world, ai[0].isDiagMovement(),
							start);
					this.world.placeAI(start);
				}
			}
		}
	}

	//Method to start the timer
	public void start() {

		//Only start if the timer has been set
		if (time != null) {
			time.start();
		}
	}
	
	//Method to stop the simulation
	public void stop() {
		
		//Only stop the timer if the time has been set
		if (time != null) {
			time.stop();
			repaint();
		}
	}
	
	//Method to reset the simulation
	public void reset(World w, boolean diagMovement) {
		
		initialize(w, diagMovement);
		run.newRun();
		repaint();
	}
	
	//Method to set whether or not walls get placed when an item is collected
	public void setWallsGetPlaced(boolean walls) {
		if (run.getMoveCount() > 0) {
			reset(new World(world.getWorldSize(), world.getItemLimit()),
					ai[0].isDiagMovement());
		}
		this.wallsGetPlaced = walls;
	}
	
	public boolean wallsGetPlaced() {
		return wallsGetPlaced;
	}
	
	//Method to check if the simulation is running
	public boolean isRunning() {
		
		//Timer not initialized
		if (time == null) {
			return false;
		}
		
		return time.isRunning();
	}
	
	//Turns reset on/off when AI gets stuck
	public void toggleResetWhenStuck() {
		this.resetsWhenStuck = RunTracker.toggle(resetsWhenStuck);
	}
	
	//Method to check if the AI is stuck
	private void checkForReset(AI[] ai) {
		
		//Loop though the array to check if AIs are stuck
		boolean stuck = true;
		if (!run.isStuck()) {
			for (int n = 0; n < aiCount; n ++) {
				if (!ai[n].isStuck()) {
					stuck = false;
					break;
				}
			}
		}
		
		if (stuck && resetsWhenStuck) {
			world = new World(world.getWorldSize(), world.getItemLimit());
			reset(world, ai[0].isDiagMovement());
		}
	}

	//Method to return the AI
	public AI[] getAI() {
		return this.ai;
	}
	
	//Method to return the current world
	public World getWorld() {
		return this.world;
	}

	//Method to set diagonal movement
	public void setDiagMovement(boolean diagMovement) {
		for (int n = 0; n < aiCount; n ++) {
			this.ai[n].setDiagonalMovement(diagMovement);
		}
	}

	//Returns true if the AI can move diagonally
	public boolean isDiagMovement() {
		return this.ai[0].isDiagMovement();
	}
	
	//Returns the menu for the display
	public DisplayMenu getDisplayMenu() {
		return this.menu;
	}
	
	//Returns the run tracker
	public RunTracker getRunTracker() {
		return this.run;
	}
	
	//Sets the display menu
	public void setDisplayMenu(DisplayMenu menu) {
		this.menu = menu;
	}
	
	//Toggles whether or not the grid is drawn
	public void toggleGrid() {
		this.showGrid = RunTracker.toggle(showGrid);
	}
	
	//Gets the selected location
	private void getSelectedSquare(Point location) {
			
		location.x -= offset.x;
		location.y -= offset.y;
			
		int boxSize = calculateGridSquareSize();
		
		Point p = new Point(location.x / boxSize,
				location.y / boxSize);
		
		//Only continue if the click was within the world
		if (Point.isInRange(p, world.getWorldSize())) {
			
			//Place and update graphics
			world.place(p);
			repaint();
		}
	}
	
	//Method called when a key event happens
	public void keyEvent() {
		System.out.println("Pressed!!");
	}
	
	//Class to respond to mouse events
	private class MouseEvents extends MouseAdapter {
		
		//Method responds to mouse clicks
		public void mouseClicked(MouseEvent e) {

			getSelectedSquare(new Point(e.getX(), e.getY()));
		}
	}
}
