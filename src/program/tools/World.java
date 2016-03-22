//Name: World
//Author: Devon McGrath
//Date: 07/12/2015
//Description: This class contains all of the elements on the screen, including the
//AI that is displayed.

//Package for the display
package program.tools;

public class World {

	public static final int MAX_WORLD_SIZE = 1000;

	//Entity IDs
	public static final int INITIAL_BLOCKS = 0;
	public static final int ID_EMPTY = 0;
	public static final int ID_BLOCK = 1;
	public static final int ID_ITEM = 2;
	public static final int ID_AI = 3;
	public static final int ID_SNAKE = 4;

	//Commands that affect this class
	public static final String WORLD_SIZE_COMMAND = "Change World Size";
	public static final String ITEM_LIM_COMMAND = "Change Item Limit";

	//Class variables
	private int world[][];
	private int itemLimit;
	private Point size;

	//Constructor 1 - using a point as world size
	public World(Point size, int itemLimit) {

		this.size = size;
		this.itemLimit = itemLimit;

		//Initialize world
		this.world = new int[size.getX()][size.getY()];
		initializeWorld(this.world, size, ID_EMPTY);
	}

	//Constructor 2 - using 2 integers as world size
	public World(int x, int y, int itemLimit) {

		this.size = new Point(x,y);
		this.itemLimit = itemLimit;

		//Initialize world
		this.world = new int[x][y];
		initializeWorld(this.world, size, ID_EMPTY);
	}

	//Method that places the AI
	public void placeAI(Point p) {

		//Only place if empty
		if (world[p.getX()][p.getY()] == ID_EMPTY) {
			world[p.getX()][p.getY()] = ID_AI;
		}
	}

	//Method that fills the world with entities
	public void setWorld() {

		int count = (itemLimit <= INITIAL_BLOCKS) ? INITIAL_BLOCKS : itemLimit;
		for (int n = 0; n < count; n ++) {

			//Place items
			if (n < itemLimit) {
				addElement(ID_ITEM);
			}

			//Place blocks
			if (n < INITIAL_BLOCKS) {
				addElement(ID_BLOCK);
			}
		}
	}

	//Method that changes the location of an object
	public void changeLocation(Point old, Point end, int id1, int id2){

		//Change what's in the world
		if (old != null && end != null) {
			world[old.getX()][old.getY()] = id1;
			world[end.getX()][end.getY()] = id2;
		}
	}

	//Method that changes the location of an object
	public void changeAILocation(Point old, Point end){

		//Change what's in the world
		if ((old != null && end != null) && !Point.isEqual(old, end)) {
			world[end.getX()][end.getY()] = world[old.getX()][old.getY()];
			world[old.getX()][old.getY()] = ID_EMPTY; //Empty
		}
	}

	//Method that will generate a new item and wall
	public void generateEntites(){

		//Add an item and a block
		addElement(ID_BLOCK);
		addElement(ID_ITEM);
	}

	//Method to add a new item to the world (in a random location)
	public void addElement(int id) {

		//Only try to generate a new item & wall if there is space
		int count = count(world, ID_EMPTY);
		if (count > 0) {

			//Get the spot to number to place 'id'
			int location = (int)(Math.random() * count);

			//Get the location of the nth appearance of an empty block
			Point p = find(world, size, ID_EMPTY, location);

			//Place 'id'
			world[p.getX()][p.getY()] = id;
		}
	}
	
	//Method to place 'id' in the world
	public void place(Point p, int id) {
		
		if (p != null) { //Only place ID if initialized
			world[p.getX()][p.getY()] = id;
		}
	}
	
	//Places a wall or empty space at point p
	public void place(Point p) {
		
		if (p != null) {
			
			int ID = world[p.getX()][p.getY()];
			
			//If a wall is there
			if (ID == ID_BLOCK) {
				world[p.getX()][p.getY()] = ID_EMPTY;
			}
			
			//If it's empty
			else if (ID == ID_EMPTY) {
				world[p.getX()][p.getY()] = ID_BLOCK;
			}
		}
	}

	//Method to initialize the 'world'
	private void initializeWorld(int[][] world, Point size, int id) {

		//Loop through the array and fill it with 'id'
		for (int i = 0; i < size.getX(); i ++) {
			for (int j = 0; j < size.getY(); j ++) {
				world[i][j] = id;
			}
		}
	}

	//Method to find the first location in the world with 'id'
	public static Point find(int[][] world, Point size, int id) {

		//Loop through the array to find the first 'id'
		for (int i = 0; i < size.getX(); i ++) {
			for (int j = 0; j < size.getY(); j ++) {

				//The item was found
				if (world[i][j] == id) {
					return new Point(i,j);
				}
			}
		}

		//Return null since it didn't find 'id'
		return null;
	}

	//Method to find the nth location in the world with 'id'
	//If there are not 'n' points, method will return closest to 'n'
	public static Point find(int[][] world, Point size, int id, int n) {

		//Loop through the array to find the first 'id'
		int count = -1;
		Point p = null;
		for (int i = 0; i < size.getX(); i ++) {
			for (int j = 0; j < size.getY(); j ++) {

				//The item was found
				if (world[i][j] == id) {
					p = new Point(i,j);
					count ++;
				}

				//Check if the nth location was found
				if (count >= n) {
					return p; //Return that point
				}
			}
		}

		//Return the closest point to the nth point
		return p;
	}

	//Method that counts the number of time 'id' appears in 'array'
	public static int count(int[][] array, int id) {

		//Loop through the array
		int count = 0;
		for (int[] arr : array) {
			for (int i : arr) {
				if (i == id) {
					count ++;
				}
			}
		}

		//Return the result
		return count;
	}

	//Method that returns the points of all 'id' that appear in the array
	public static Point[] getPoints(int[][] array, Point size, int id) {

		//Count the number of times 'id' appears in the array
		int count = count(array, id);

		//No elements exist in the array
		if (count  < 1) {
			return null;
		}

		//There are at least one
		Point[] points = new Point[count];
		int index = 0;
		for (int i = 0; i < size.getX(); i ++) { //Loop through the array
			for (int j = 0; j < size.getY(); j ++) {
				if (array[i][j] == id) {
					points[index] = new Point(i,j);
					index ++;
				}
			}
		}

		//Return the array of points
		return points;
	}

	//Method to get a random point
	public static Point random(World w) {

		//Only search for random point if there's space
		int count = count(w.world, ID_EMPTY);
		if (count > 0) {

			//Get the spot to number to place 'id'
			int location = (int)(Math.random() * count);

			//Return the location of the nth appearance of an empty block
			return find(w.world, w.size, ID_EMPTY, location);
		}
		
		//Not enough space
		else {
			return null;
		}
		
	}

	//Return methods
	public int[][] getWorld(){
		return world;
	}

	public Point getWorldSize() {
		return size;
	}

	public int getX(){
		return size.getX();
	}

	public int getY(){
		return size.getY();
	}

	public int getItemLimit(){
		return itemLimit;
	}
}
