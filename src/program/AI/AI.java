//Name: AI
//Author: Devon McGrath
//Date: 07/12/2015
//Description: This class handles all of the AI`s actions.

//Package for the AI
package program.AI;

import program.tools.Point;
import program.tools.World;

public class AI {

	//Class will check if the AI is stuck every "MOVE_CHECK" moves.
	//If the AI traveled less than the threshold, the AI is stuck
	private static final byte MOVE_CHECK = 32;
	private static final float STUCK_THRESHOLD = 5.0F;

	//Class variables
	private World w;
	private boolean isStuck;
	private boolean canMoveDiag;
	private Point worldSize;
	private Point current;
	private Point last;
	private Point update;
	private byte updatesSinceCheck;
	private int closestPoint;

	private Point[] snake;
	private boolean isSnake = false;

	//Constructor
	public AI(World w, boolean diagMovement) {
		this.w = w;
		this.worldSize = w.getWorldSize();
		this.isStuck = false;
		this.canMoveDiag = diagMovement;
		this.current = new Point(0,0);
		this.update = new Point(0,0);
		this.last = null;
		this.updatesSinceCheck = 0;
		this.closestPoint = 0;
	}

	//Constructor - with AI start location
	public AI(World w, boolean diagMovement, Point start) {
		this.w = w;
		this.worldSize = w.getWorldSize();
		this.canMoveDiag = diagMovement;
		this.isStuck = false;
		this.current = new Point(start);
		this.update = new Point(start);
		this.last = null;
		this.updatesSinceCheck = 0;
		this.closestPoint = 0;
	}

	//Method that is called by other classes to run the AI
	public Point runAI(int[][] world) {

		//Find the point to go to
		int itemCount = World.count(world, World.ID_ITEM);
		Point p = new Point(current);
		if (closestPoint < itemCount) {
			p = (Point.reorder(World.getPoints(world, worldSize,
					World.ID_ITEM), update))[closestPoint];
		}
		else { //The AI has run out of points to go to
			this.isStuck = true;
		}

		//Call the other method with the destination
		return runAI(world, p);
	}

	//Method to run the AI with a point to go to
	public Point runAI(int[][] world, Point goal) {

		//If the AI is not completely stuck
		if (!isStuck) {

			//Check if the AI is stuck
			boolean stuck = false;
			if (updatesSinceCheck >= MOVE_CHECK) {
				stuck = checkIfStuck();
				this.updatesSinceCheck = 0;
				this.update = new Point(current);
			}
			if (stuck) { //If stuck, try to go to next closest point
				this.closestPoint ++;
			}

			//Increment the number of updates since it checked of the AI is stuck
			this.updatesSinceCheck ++;

			//Generate the array of moves
			Path path = new Path(worldSize);
			Move m = path.createPath(canMoveDiag, current, last, goal, world);

			//Get the new point
			this.last = current;
			Point newMove = move(current, m);
			this.current = newMove;

			/*System.out.println("Snake");//FIXME
			for (int i = 0; i < snake.length; i ++) {
				System.out.println(snake[i].getX()+", "+snake[i].getY()+", "
						+w.getWorld()[snake[i].getX()][snake[i].getY()]);
			}*/

			//Return the new location of the AI
			return newMove;
		}

		//AI is completely stuck
		else {
			this.last = current;
			return current;
		}
	}

	//Method that updates the location of the AI
	private Point move(Point current, Move move) {

		//Return null if either parameters are null
		if (current == null || move == null) {
			return null;
		}

		//Create the new move
		Point p = Point.createNewPoint(current, move);

		//Update the snake
		if (isSnake) {
			updateSnake(p, w.getWorld()[p.getX()][p.getY()] == World.ID_ITEM);
			for (int n = 0; n < snake.length; n ++) {
				w.place(snake[n], World.ID_AI);
			}
		}
		else {
			w.place(current, World.ID_EMPTY);
		}

		//Update the world
		w.place(p, World.ID_AI);

		//Return the point created with the move
		return p;
	}

	//Method will update the snake
	private void updateSnake(Point n, boolean collected) {

		//If an item was collected
		if (collected) {

			//Clone array
			Point[] clone = new Point[snake.length];
			for (int i = 0; i < snake.length; i ++) {
				clone[i] = new Point(snake[i]);
			}

			//Create new array
			this.snake = new Point[snake.length + 1];
			this.snake[0] = new Point(n);
			for (int i = 1; i < snake.length; i ++) {
				this.snake[i] = new Point(clone[i-1]);
			}
		}

		//Item was not collected
		else {

			//Remove the last part of the snake
			Point removed = snake[snake.length-1];
			w.place(removed, World.ID_EMPTY);

			//Shift all values
			for (int i = snake.length - 1; i > 0; i --) {
				this.snake[i] = new Point(snake[i-1]);
			}
		}

		//Place a new part of the snake
		this.snake[0] = n;
		this.w.place(snake[1], World.ID_SNAKE);
	}

	//Method will return true if the AI hasn't moved more than some
	//distance in a specified number of moves
	private boolean checkIfStuck() {
		return (Math.abs(
				Point.calculateDist(update, current)) < STUCK_THRESHOLD);
	}

	//Method to get the distance traveled in the last move
	public double getDistFromLastMove() {
		return Point.calculateDist(current, last);
	}

	public void setUpdatePoint(Point update) {
		this.update = update;
		this.updatesSinceCheck = 0;
		this.isStuck = false;
		this.closestPoint = 0;
	}

	public World getWorld() {
		return w;
	}

	public void setWorld(World w) {
		this.w = w;
	}

	public boolean isStuck() {
		return isStuck;
	}

	public void setDiagonalMovement(boolean diagMovement) {
		this.canMoveDiag = diagMovement;
	}

	public boolean isDiagMovement() {
		return this.canMoveDiag;
	}

	public Point getCurrent() {
		return current;
	}

	public void setCurrent(Point current) {
		this.current = current;
	}

	public Point getLast() {
		return last;
	}

	public void setLast(Point last) {
		this.last = last;
	}

	public boolean isSnake() {
		return isSnake;
	}

	public void setSnake(boolean isSnake) {
		this.isSnake = isSnake;

		//If it is now a snake
		if (isSnake) {
			this.snake = new Point[2];
			this.snake[0] = new Point(current);
			this.snake[1] = new Point(last);
			this.w.place(snake[1], World.ID_SNAKE);
		}

		//AI is no longer a snake
		else {

			//Loop through the array and remove the snake
			for (int n = 1; n < snake.length; n ++) {
				this.w.place(snake[n], World.ID_EMPTY);
			}
			this.snake = null;
		}
	}
}
