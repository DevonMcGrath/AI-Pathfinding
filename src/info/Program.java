/* Name: Program
 * Author: Devon McGrath
 * Date: 12/15/2015
 * Description: This class contains basic program information.
 */

package info;

public class Program {
	
	//Basic information
	static final String AUTHOR = "Devon McGrath";
	static final String START_DATE = "07/12/2015";
	static final String LAST_MODIFIED = "12/28/2015";
	
	//Method that returns a string containing a short description of the program
	public static String getProgramInfo() {
		
		String info =
				"Background:\n"
				+ "There are many ways to do AI pathfinding in 2D space.\n"
				+ "This program creates an array with weights for each move\n"
				+ "and picks the best one in hopes that doing so will\n"
				+ "result in the AI reaching its destination. This is not always\n"
				+ "the case and therefore, the AI can get stuck relatively easy.\n\n"
				+ "The 'World':\n"
				+ "The world or board, is simply a 2D integer array that is filled\n"
				+ "with IDs to identify what is contained in that point in the world.\n"
				+ "For example, if the ID is zero, the world may contain nothing there.\n"
				+ "The pathfinding completely relies on this array to create the best\n"
				+ "move for the AI.";
		
		return info;
	}

}
