/* Name: ProgramManager
 * Author: Devon McGrath
 * Date: 11/21/2015
 * Description: This class is the main class for the entire program and
 * oversees all operations done.
 */

package program.control;

import program.display.Display;
import program.display.DisplayMenu;
import program.display.Simulation;
import program.tools.RunTracker;
import program.tools.World;

public class ProgramManager {
	
	public static final String ON_STATE = "(on)";
	public static final String OFF_STATE = "(Off)";

	public static void main(String[] args) {
		
		//Create the display
		Display display = new Display(true, "AI Pathfinding", 900, 700, "Icon");
		World w = new World(36,20,5);
		Simulation simulation = new Simulation(w, 250);
		EventManager events = new EventManager(simulation);
		DisplayMenu menu = new DisplayMenu(events, 6);
		
		//Create the menu for the display
		String menuName1 = "Program";
		String menuName2 = "Simulation";
		String menuName3 = "Stats";
		String menuName4 = "About";
		menu.addMenu(menuName1); //Menu 1
		menu.addMenuItem(menuName1, Simulation.START_COMMAND,
				"Starts the simulation");
		menu.addMenuItem(menuName1, Simulation.PAUSE_COMMAND,
				"Pauses the simulation");
		menu.addMenuItem(menuName1, Simulation.RESET_COMMAND,
				"Resets the simualtion");
		menu.addMenuItem(menuName1, Simulation.SET_DELAY_COMMAND,
				"Allows you to set the delay between display updates");
		menu.addMenuItem(menuName1, Simulation.RESET_ON_STUCK_COMMAND,
				"Toggles reset upon AI getting stuck");
		menu.addMenuItem(menuName1, Simulation.DISPLAY_GRID,
				"Toggles the grid lines on/off");
		menu.addMenuItem(menuName1, "Exit", "Exits the program");
		menu.addMenu(menuName2); //Menu 2
		menu.addMenuItem(menuName2, Simulation.MOVEMENT_TYPE_COMMAND,
				"Switches movement type between diagonal and U L R D");
		menu.addMenuItem(menuName2, Simulation.WALLS_COMMAND,
				"Toggles wall placement upon item collection (RESETS SIMULATION)");
		menu.addMenuItem(menuName2, World.WORLD_SIZE_COMMAND,
				"Allows you to set the world size (RESETS SIMULATION)");
		menu.addMenuItem(menuName2, World.ITEM_LIM_COMMAND,
				"Allows you to set the amount of items (RESETS SIMULATION)");
		menu.addMenuItem(menuName2, Simulation.AI_COUNT_COMMAND,
				"Allows you to set the amount of AI");
		menu.addMenu(menuName3); //Menu 3
		menu.addMenuItem(menuName3, RunTracker.VEIW_ALL_COMMAND,
				"Shows all stats that are recorded");
		menu.addMenuItem(menuName3, RunTracker.HIDE_ALL_COMMAND,
				"Hides all stats that are recorded");
		menu.addMenuItem(menuName3, RunTracker.RUN_NUM_COMMAND,
				"Displays/hides run number");
		menu.addMenuItem(menuName3, RunTracker.ITEMS_C_COMMAND,
				"Displays/hides the number of items collected");
		menu.addMenuItem(menuName3, RunTracker.AVG_ITEM_PER_RUN_COMMAND,
				"Displays/hides the average number of items collected per run");
		menu.addMenuItem(menuName3, RunTracker.AVG_DIST_PER_RUN_COMMAND,
				"Displays/hides the average amount of moves to collect an item");
		menu.addMenuItem(menuName3, RunTracker.MOVES_VISIBLE_COMMAND,
				"Displays/hides the number of moves made in the current run");
		menu.addMenu(menuName4); //Menu 4
		menu.addMenuItem(menuName4, "Program",
				"View information about the program");
		menu.addMenuItem(menuName4, "Author",
				"View information about the creator of this program");
		
		//Add the objects to the display
		display.add(simulation);
		display.setMenu(menu);
		simulation.setDisplayMenu(menu);
		
		//Make the display visible
		display.setVisible(true);
		
		//Start the simulation
		//simulation.start();
	}
}
