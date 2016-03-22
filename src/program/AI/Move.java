/* Name: Move
 * Author: Devon McGrath
 * Date: 12/12/2015
 * Description: This class stores the change in direction for a specific move.
 */

package program.AI;

public class Move {

	private int dx, dy;

	//Constructor
	public Move(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}

	//Method to invert the move
	public static Move invertMove(Move m) {
		return new Move(-m.getDX(), -m.getDY());
	}

	//Method to check for move equality
	public static boolean isEqaul(Move m1, Move m2) {
		return (m1.getDX() == m2.getDX() && m1.getDY() == m2.getDY());
	}

	//Check if m1 is reverse of m2
	public static boolean isReverse(Move m1, Move m2) {

		//Special case
		if (m1 == null || m2 == null) {
			return false;
		}

		return (m1.getDX() == -m2.getDX() && m1.getDY() == -m2.getDY());
	}

	//Add two moves together
	public static Move add(Move m1, Move m2) {

		//Special case
		if (m1 == null || m2 == null) {
			return null;
		}
		
		return new Move(m1.getDX() + m2.getDX(), m1.getDY() + m2.getDY());
	}

	public int getDX() {
		return dx;
	}

	public void setDX(int dx) {
		this.dx = dx;
	}

	public int getDY() {
		return dy;
	}

	public void setDY(int dy) {
		this.dy = dy;
	}
}
