package program.AI;

import program.tools.Point;
import program.tools.World;

public class Path {

	//Move that is returned if a path can not be achieved
	private static final Move ERROR_MOVE = new Move(0,0);

	private static final float WEIGHT_DX_CHANGE = 0.1F;
	private static final float WEIGHT_DY_CHANGE = 0.1F;
	private static final float WEIGHT_INVALID_MOVE = -8;
	private static final float WEIGHT_FOUND = 10.0F;

	private Point worldSize;

	private Move[] m;

	//Constructor
	public Path(Point worldSize) {

		this.worldSize = worldSize;

		//Fill the 'move' array with the 8 possible moves
		/* Move array:
		 * [0] - diagonal (up, left)
		 * [1] - up
		 * [2] - diagonal (up, right)
		 * [3] - left
		 * [4] - right
		 * [5] - diagonal (down, left)
		 * [6] - down
		 * [7] - diagonal (down, right)
		 */
		m = new Move[8];
		m[0] = new Move(-1, -1);
		m[1] = new Move(0, -1);
		m[2] = new Move(1, -1);
		m[3] = new Move(-1, 0);
		m[4] = new Move(1, 0);
		m[5] = new Move(-1, 1);
		m[6] = new Move(0, 1);
		m[7] = new Move(1, 1);
	}

	//Method that will return the closest path to get from (x1,y1) to (x2,y2)
	public Move createPath(boolean canMoveDiag,
			Point start, Point last, Point end, int[][] world) {

		Move move = null;

		//Get the appropriate moves
		if (canMoveDiag) { //Diagonal movement
			move = pathWithDiag(start, last, end, world);
		}
		else {
			move = pathWithNoDiag(start, last, end, world);
		}

		//Return the array of moves
		return move;
	}

	//Method that will return a list of moves if the player can move diagonally
	private Move pathWithDiag(Point start, Point last, Point end,
			int[][] world) {

		//Calculations
		int dx = Point.calculateDX(start, end);
		int dy = Point.calculateDY(start, end);
		double distance = Point.calculateDist(dx, dy);

		//Check if the new location is the same location
		if (distance == 0) {
			return null;
		}

		int weightArraySize = 8;
		float weight[];

		/* Weight array:
		 * [0] - diagonal (up, left)
		 * [1] - up
		 * [2] - diagonal (up, right)
		 * [3] - left
		 * [4] - right
		 * [5] - diagonal (down, left)
		 * [6] - down
		 * [7] - diagonal (down, right)
		 */
		//Create the weight array
		weight = fillWeightArray(world, start, end,
				worldSize, weightArraySize);

		//Get the location of the highest weight
		int index = highestValueIndex(weight);

		//If the new move will end in the last location
		if (Point.isEqual(Point.createNewPoint(start, m[index]), last)) {
			index = nthLargestIndex(weight,2);
		}

		//Check if that move will result in an error
		if (index < 0 || index >= m.length) {
			return ERROR_MOVE;
		}
		if (weight[index] == WEIGHT_INVALID_MOVE) {
			return ERROR_MOVE;
		}

		//Return that move
		return m[index];
	}

	//Method that will return a list of moves if the player can move diagonally
	private Move pathWithNoDiag(Point start, Point last, Point end,
			int[][] world) {

		//Calculations
		int dx = Point.calculateDX(start, end);
		int dy = Point.calculateDY(start, end);
		Point temp = new Point(start.getX(), start.getY());
		double distance = Point.calculateDist(dx, dy);

		//Check if the new location is the same location
		if (distance == 0) {
			return null;
		}

		int weightArraySize = 4;
		float weight[];

		/* Weight array:
		 * [0] - up
		 * [1] - left
		 * [2] - right
		 * [3] - down
		 */
		//Create the weight array
		weight = fillWeightArray(world, temp, end,
				worldSize, weightArraySize);

		//Get the location of the highest weight
		int index = highestValueIndex(weight);

		//Convert the index into the 'move' array index
		int oldIndex = index;
		index = convertIndex(index);

		//If the new move will end in the last location
		if (Point.isEqual(Point.createNewPoint(start, m[index]), last)) {
			oldIndex = nthLargestIndex(weight,2);
			index = convertIndex(oldIndex);
		}

		//Check if that move will result in an error
		if (index < 0 || index >= m.length) {
			return ERROR_MOVE;
		}
		if (weight[oldIndex] == WEIGHT_INVALID_MOVE) {
			return ERROR_MOVE;
		}

		//Return the move
		return m[index];
	}

	//Method to create an empty float array of size
	private float[] createWeightArray(int length, float value) {

		float array[] = new float[length];

		//Loop through the array to initialize values
		for (int n = 0; n < length; n ++) {
			array[n] = value;
		}

		//Return the array
		return array;
	}

	//Method that will fill the weight array with values
	private float[] fillWeightArray(int[][] world, Point current, Point end,
			Point worldSize, int size) {

		//First check if the values are null
		if (current == null || end == null || worldSize == null) {
			return createWeightArray(size, WEIGHT_INVALID_MOVE);
		}
		
		//Create the empty array
		float weight[] = createWeightArray(size, 0.0F);

		//All 8 possible moves
		/* Note:
		 * DLU - diagonal (left, up)
		 * DRU - diagonal (right, up)
		 * DLD - diagonal (left, down)
		 * DRD - diagonal (right, down)
		 */
		float up = 0.0F, left = 0.0F, right = 0.0F, down = 0.0F;
		float DLU = 0.0F, DRU = 0.0F, DLD = 0.0F, DRD = 0.0F;

		//Check for invalid moves first
		if (current.getY() <= 0) {
			up = WEIGHT_INVALID_MOVE;
		}
		if (current.getX() <= 0) {
			left = WEIGHT_INVALID_MOVE;
		}
		if (current.getX() + 1 >= worldSize.getX()) {
			right = WEIGHT_INVALID_MOVE;
		}
		if (current.getY() + 1 >= worldSize.getY()) {
			down = WEIGHT_INVALID_MOVE;
		}

		//If any of those moves were invalid, the diagonal moves
		//in that direction are also invalid
		if (up == WEIGHT_INVALID_MOVE) {
			DLU = WEIGHT_INVALID_MOVE;
			DRU = WEIGHT_INVALID_MOVE;
		}
		if (left == WEIGHT_INVALID_MOVE) {
			DLU = WEIGHT_INVALID_MOVE;
			DLD = WEIGHT_INVALID_MOVE;
		}
		if (right == WEIGHT_INVALID_MOVE) {
			DRU = WEIGHT_INVALID_MOVE;
			DRD = WEIGHT_INVALID_MOVE;
		}
		if (down == WEIGHT_INVALID_MOVE) {
			DLD = WEIGHT_INVALID_MOVE;
			DRD = WEIGHT_INVALID_MOVE;
		}

		//Still checking for invalid moves,
		//Now checking if an object is already in new location
		/* Move array:
		 * [0] - diagonal (up, left)
		 * [1] - up
		 * [2] - diagonal (up, right)
		 * [3] - left
		 * [4] - right
		 * [5] - diagonal (down, left)
		 * [6] - down
		 * [7] - diagonal (down, right)
		 */
		if (DLU != WEIGHT_INVALID_MOVE) {
			Point p = Point.createNewPoint(current, m[0]);
			if (!isValid(world[p.getX()][p.getY()])) {
				DLU = WEIGHT_INVALID_MOVE;
			}
		}
		if (up != WEIGHT_INVALID_MOVE) {
			Point p = Point.createNewPoint(current, m[1]);
			if (!isValid(world[p.getX()][p.getY()])) {
				up = WEIGHT_INVALID_MOVE;
			}
		}
		if (DRU != WEIGHT_INVALID_MOVE) {
			Point p = Point.createNewPoint(current, m[2]);
			if (!isValid(world[p.getX()][p.getY()])) {
				DRU = WEIGHT_INVALID_MOVE;
			}
		}
		if (left != WEIGHT_INVALID_MOVE) {
			Point p = Point.createNewPoint(current, m[3]);
			if (!isValid(world[p.getX()][p.getY()])) {
				left = WEIGHT_INVALID_MOVE;
			}
		}
		if (right != WEIGHT_INVALID_MOVE) {
			Point p = Point.createNewPoint(current, m[4]);
			if (!isValid(world[p.getX()][p.getY()])) {
				right = WEIGHT_INVALID_MOVE;
			}
		}
		if (DLD != WEIGHT_INVALID_MOVE) {
			Point p = Point.createNewPoint(current, m[5]);
			if (!isValid(world[p.getX()][p.getY()])) {
				DLD = WEIGHT_INVALID_MOVE;
			}
		}
		if (down != WEIGHT_INVALID_MOVE) {
			Point p = Point.createNewPoint(current, m[6]);
			if (!isValid(world[p.getX()][p.getY()])) {
				down = WEIGHT_INVALID_MOVE;
			}
		}
		if (DRD != WEIGHT_INVALID_MOVE) {
			Point p = Point.createNewPoint(current, m[7]);
			if (!isValid(world[p.getX()][p.getY()])) {
				DRD = WEIGHT_INVALID_MOVE;
			}
		}

		//Calculate the weight of each move
		if (DLU != WEIGHT_INVALID_MOVE) { //Diagonal left up
			Point p = Point.createNewPoint(current, m[0]);
			if (Point.calculateDist(p, end) == 0) {
				DLU += WEIGHT_FOUND;
			}
			if (Point.calculateDX(current, end)
					<= Point.calculateDX(p, end)) {
				DLU -= WEIGHT_DX_CHANGE / 4;
			}
			else {
				DLU += WEIGHT_DX_CHANGE;
			}
			if (Point.calculateDY(current, end)
					<= Point.calculateDY(p, end)) {
				DLU -= WEIGHT_DY_CHANGE / 4;
			}
			else {
				DLU += WEIGHT_DY_CHANGE;
			}
			DLU -= Point.calculateChangeInDist(current, p, end);
		}
		if (up != WEIGHT_INVALID_MOVE) { //Up
			Point p = Point.createNewPoint(current, m[1]);
			if (Point.calculateDist(p, end) == 0) {
				up += WEIGHT_FOUND;
			}
			if (Point.calculateDX(current, end)
					<= Point.calculateDX(p, end)) {
				up -= WEIGHT_DX_CHANGE / 4;
			}
			else {
				up += WEIGHT_DX_CHANGE;
			}
			if (Point.calculateDY(current, end)
					<= Point.calculateDY(p, end)) {
				up -= WEIGHT_DY_CHANGE / 4;
			}
			else {
				up += WEIGHT_DY_CHANGE;
			}
			up -= Point.calculateChangeInDist(current, p, end);
		}
		if (DRU != WEIGHT_INVALID_MOVE) { //Diagonal right up
			Point p = Point.createNewPoint(current, m[2]);
			if (Point.calculateDist(p, end) == 0) {
				DRU += WEIGHT_FOUND;
			}
			if (Point.calculateDX(current, end)
					<= Point.calculateDX(p, end)) {
				DRU -= WEIGHT_DX_CHANGE / 4;
			}
			else {
				DRU += WEIGHT_DX_CHANGE;
			}
			if (Point.calculateDY(current, end)
					<= Point.calculateDY(p, end)) {
				DRU -= WEIGHT_DY_CHANGE / 4;
			}
			else {
				DRU += WEIGHT_DY_CHANGE;
			}
			DRU -= Point.calculateChangeInDist(current, p, end);
		}
		if (left != WEIGHT_INVALID_MOVE) { //Left
			Point p = Point.createNewPoint(current, m[3]);
			if (Point.calculateDist(p, end) == 0) {
				left += WEIGHT_FOUND;
			}
			if (Point.calculateDX(current, end)
					<= Point.calculateDX(p, end)) {
				left -= WEIGHT_DX_CHANGE / 4;
			}
			else {
				left += WEIGHT_DX_CHANGE;
			}
			if (Point.calculateDY(current, end)
					<= Point.calculateDY(p, end)) {
				left -= WEIGHT_DY_CHANGE / 4;
			}
			else {
				left += WEIGHT_DY_CHANGE;
			}
			left -= Point.calculateChangeInDist(current, p, end);
		}
		if (right != WEIGHT_INVALID_MOVE) { //Right
			Point p = Point.createNewPoint(current, m[4]);
			if (Point.calculateDist(p, end) == 0) {
				right += WEIGHT_FOUND;
			}
			if (Point.calculateDX(current, end)
					<= Point.calculateDX(p, end)) {
				right -= WEIGHT_DX_CHANGE / 4;
			}
			else {
				right += WEIGHT_DX_CHANGE;
			}
			if (Point.calculateDY(current, end)
					<= Point.calculateDY(p, end)) {
				right -= WEIGHT_DY_CHANGE / 4;
			}
			else {
				right += WEIGHT_DY_CHANGE;
			}
			right -= Point.calculateChangeInDist(current, p, end);
		}
		if (DLD != WEIGHT_INVALID_MOVE) { //Diagonal left down
			Point p = Point.createNewPoint(current, m[5]);
			if (Point.calculateDist(p, end) == 0) {
				DLD += WEIGHT_FOUND;
			}
			if (Point.calculateDX(current, end)
					<= Point.calculateDX(p, end)) {
				DLD -= WEIGHT_DX_CHANGE / 4;
			}
			else {
				DLD += WEIGHT_DX_CHANGE;
			}
			if (Point.calculateDY(current, end)
					<= Point.calculateDY(p, end)) {
				DLD -= WEIGHT_DY_CHANGE / 4;
			}
			else {
				DLD += WEIGHT_DY_CHANGE;
			}
			DLD -= Point.calculateChangeInDist(current, p, end);
		}
		if (down != WEIGHT_INVALID_MOVE) { //Down
			Point p = Point.createNewPoint(current, m[6]);
			if (Point.calculateDist(p, end) == 0) {
				down += WEIGHT_FOUND;
			}
			if (Point.calculateDX(current, end)
					<= Point.calculateDX(p, end)) {
				down -= WEIGHT_DX_CHANGE / 4;
			}
			else {
				down += WEIGHT_DX_CHANGE;
			}
			if (Point.calculateDY(current, end)
					<= Point.calculateDY(p, end)) {
				down -= WEIGHT_DY_CHANGE / 4;
			}
			else {
				down += WEIGHT_DY_CHANGE;
			}
			down -= Point.calculateChangeInDist(current, p, end);
		}
		if (DRD != WEIGHT_INVALID_MOVE) { //Diagonal right down
			Point p = Point.createNewPoint(current, m[7]);
			if (Point.calculateDist(p, end) == 0) {
				DRD += WEIGHT_FOUND;
			}
			if (Point.calculateDX(current, end)
					<= Point.calculateDX(p, end)) {
				DRD -= WEIGHT_DX_CHANGE / 4;
			}
			else {
				DRD += WEIGHT_DX_CHANGE;
			}
			if (Point.calculateDY(current, end)
					<= Point.calculateDY(p, end)) {
				DRD -= WEIGHT_DY_CHANGE / 4;
			}
			else {
				DRD += WEIGHT_DY_CHANGE;
			}
			DRD -= Point.calculateChangeInDist(current, p, end);
		}

		//Transfer weights to array
		if (size == 4) {
			weight[0] = up;
			weight[1] = left;
			weight[2] = right;
			weight[3] = down;
		}
		else {
			weight[0] = DLU;
			weight[1] = up;
			weight[2] = DRU;
			weight[3] = left;
			weight[4] = right;
			weight[5] = DLD;
			weight[6] = down;
			weight[7] = DRD;
		}

		//Return the array
		return weight;
	}

	//Method to copy the array so that changes can't be made to original
	public static int[][] copyArray(int array[][], Point size) {

		//Create the array
		int newArray[][] = new int[size.getX()][size.getY()];

		//Loop through the old array to copy values
		for (int i = 0; i < size.getX(); i ++) {
			for (int j = 0; j < size.getY(); j ++) {
				newArray[i][j] = array[i][j];
			}
		}

		//Return the copy of the array
		return newArray;
	}

	//Method to get the best location for the move
	private int highestValueIndex(float[] array) {

		//Loop through the array
		int index = 0;
		for (int n = 0; n < array.length; n ++) {
			if (array[n] > array[index]) {
				index = n;
			}
		}

		//Return the value
		return index;
	}

	//Method to get the nTH largest number index from array
	//Returns -1 if n > array.length
	private int nthLargestIndex(float[] array, int n) {

		//Check if there are n numbers
		if (n > array.length) {
			return -1;
		}

		//Copy the array
		float[] clone = new float[array.length];
		for (int i = 0; i < array.length; i ++) {
			clone[i] = array[i];
		}

		//Sort the array n times
		int largestIndex = 0;
		for (int i = 0; i < n; i ++) {

			largestIndex = i;

			//Loop through array
			for (int j = i; j < array.length; j ++) {

				//Check if value is larger
				if (clone[j] > clone[largestIndex]) {
					largestIndex = j;
				}
			}

			//Swap the values
			float hold = clone[i];
			clone[i] = clone[largestIndex];
			clone[largestIndex] = hold;
		}

		//Return the result
		return largestIndex;
	}

	//Method to convert a move index into the 'm' array
	private int convertIndex(int index) {

		//Convert the index into the 'move' array index
		if (index == 0) { //Up
			index = 1;
		}
		else if (index == 1) { //Left
			index = 3;
		}
		else if (index == 2) { //Right
			index = 4;
		}
		else { //Down
			index = 6;
		}

		return index;
	}
	
	//Method to check if 'id' is a valid move
	private boolean isValid(int id) {
		return (id == World.ID_EMPTY ||
				id == World.ID_ITEM);
	}
}
