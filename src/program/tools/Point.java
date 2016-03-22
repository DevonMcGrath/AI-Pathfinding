package program.tools;

import program.AI.Move;

public class Point {

	public int x, y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(Point p) {
		if (p != null) {
			this.x = p.getX();
			this.y = p.getY();
		}
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	//Method that updates the (x,y) given a move
	public void movePoint(Move move) {

		if (move != null) {
			this.x = x + move.getDX();
			this.y = y + move.getDY();
		}
	}

	//Method to check for point equality
	public static boolean isEqual(Point p1, Point p2) {

		//Special case
		if (p1 == null || p2 == null) {
			return false;
		}

		return (p1.getX() == p2.getX() && p1.getY() == p2.getY());
	}

	//Method calculates the x distance between two points
	public static int calculateDX(Point p1, Point p2) {

		//Special case
		if (p1 == null || p2 == null) {
			return -1;
		}

		return Math.abs(p1.getX() - p2.getX());
	}

	//Method calculates the y distance between two points
	public static int calculateDY(Point p1, Point p2) {

		//Special case
		if (p1 == null || p2 == null) {
			return -1;
		}

		return Math.abs(p1.getY() - p2.getY());
	}

	//Method to calculate the distance between two points
	public static double calculateDist(Point p1, Point p2) {

		//Special case
		if (p1 == null || p2 == null) {
			return -1;
		}

		return Math.sqrt(Math.pow(calculateDX(p1,p2), 2) +
				Math.pow(calculateDY(p1,p2), 2));
	}

	//Method to calculate distance between two points
	public static double calculateDist(int dx, int dy) {

		return Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));
	}

	//Method to calculate the change in distance when p1 is moved to p2
	//looking at it from point 'end' || negative means closer!
	public static double calculateChangeInDist(Point p1, Point p2, Point end) {

		//Special case
		if (p1 == null || p2 == null || end == null) {
			return -1;
		}

		return calculateDist(p2,end) - calculateDist(p1,end);
	}

	//Method returns the number of different squares in-between two points
	public static int calculateArea(Point p1, Point p2) {

		//Special case
		if (p1 == null || p2 == null) {
			return -1;
		}

		return Point.calculateDX(p1, p2) * Point.calculateDY(p1, p2);
	}

	//Method that creates a new point from a move
	public static Point createNewPoint(Point p, Move move) {

		//Special case
		if (p == null || move == null) {
			return null;
		}
		return new Point(p.getX() + move.getDX(), p.getY() + move.getDY());
	}
	
	//Method to reorder points based on distance from p
	public static Point[] reorder(Point[] points, Point p) {
		
		//Special cases
		if (points == null || p == null) {
			return null;
		}
		if (points.length == 1) {
			return points;
		}
		
		//Create an array of distances
		double[] dist = new double[points.length];
		for (int n = 0; n < points.length; n ++) {
			dist[n] = calculateDist(points[n], p);
		}
		
		//Sort the array
		for (int i = 0; i < points.length; i ++) {
			int smallestIndex = i;
			for (int j = i; j < points.length; j ++) {
				
				//Check if smaller
				if (dist[j] < dist[smallestIndex]) {
					smallestIndex = j;
				}
			}
			
			//Swap
			if (smallestIndex != i) {
				double value = dist[i]; //DIST swap
				dist[i] = dist[smallestIndex];
				dist[smallestIndex] = value;
				
				Point r = points[i]; //POINT swap
				points[i] = points[smallestIndex];
				points[smallestIndex] = r;
			}
		}
		
		//Return the sorted array
		return points;
	}
	
	//Method that returns the quadrant of the point
	public static int getQuadrant(Point p) {
		
		//Special case
		if (p == null) {
			return -1;
		}
		
		if (p.x >= 0 && p.y >= 0) {
			return 1;
		}
		else if (p.x < 0 && p.y > 0) {
			return 2;
		}
		else if (p.x < 0 && p.y < 0) {
			return 3;
		}
		else {
			return 4;
		}
	}
	
	//Returns p with only positive values
	public static Point abs(Point p) {
		return new Point(Math.abs(p.x), Math.abs(p.y));
	}
	
	//Method that gets whether or not if p1 is between (0,0) and p2
	public static boolean isInRange(Point p1, Point p2) {
		
		//Special case
		if (p1 == null || p2 == null) {
			return false;
		}
		
		//Not in the same quadrant
		if (Point.getQuadrant(p1) != Point.getQuadrant(p2)) {
			return false;
		}
		
		//Get absolute values of points
		Point p1ABS = Point.abs(p1);
		Point p2ABS = Point.abs(p2);
		
		return (p2ABS.x > p1ABS.x && p2ABS.y > p1ABS.y);
	}
}
