package program.display;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import program.control.*;

public class DisplayMenu extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JMenu[] menus;
	private int limit, currentNumOfMenus;
	private ComponentEvent events;

	//Constructor
	public DisplayMenu(EventManager events, int numOfMenus) {

		this.currentNumOfMenus = 0;
		this.events = new ComponentEvent(events);

		//Only initialize if parameter is positive
		if (numOfMenus >= 0) {

			this.limit = numOfMenus;

			//Initialize the array that will hold the menus
			menus = new JMenu[numOfMenus];
			for (int n = 0; n < numOfMenus; n ++) {
				menus[n] = null;
			}
		}
		else {
			this.limit = 0;
		}
	}

	//Method that allows the addition of a menu
	public void addMenu(String title) {

		//Only add menu if there is space
		if (currentNumOfMenus < limit) {

			//Create the menu
			menus[currentNumOfMenus] = new JMenu(title);
			this.add(menus[currentNumOfMenus]);

			currentNumOfMenus ++;
		}

	}

	//Method that allows the removal of a specific menu
	public void removeMenu(int ID) {

		//Only remove menu if it's within range
		if (ID >= 0 && ID < limit && currentNumOfMenus > 0) {
			this.remove(menus[ID]);
			this.setVisible(false); //Refresh
			this.setVisible(true);
			this.menus[ID] = null;
			this.currentNumOfMenus --;
		}
	}

	//Method that allows the removal of the last menu added
	public void removeMenu() {

		//Only remove menu if there is a menu to remove
		if (currentNumOfMenus > 0) {
			this.remove(menus[currentNumOfMenus - 1]);
			this.setVisible(false); //Refresh
			this.setVisible(true);
			this.menus[currentNumOfMenus - 1] = null;
			this.currentNumOfMenus --;
		}
	}

	//Method that allows the addition of a menu item with the title of the menu
	public void addMenuItem(String menuTitle, String title) {

		int location = -1;

		//Find the menu
		for (int n = 0; n < menus.length; n ++) {
			if (menus[n] !=  null) {
				if (menus[n].getText() == menuTitle) {
					location = n;
				}
			}
		}

		//Create the menu if it exists
		if (location != -1) {

			JMenuItem menuItem = new JMenuItem(title);
			menuItem.addActionListener(events);
			menus[location].add(menuItem);
		}
	}

	//Method that allows the addition of a menu item with the title of the menu
	public void addMenuItem(String menuTitle, String title, String tooltip) {

		int location = -1;

		//Find the menu
		for (int n = 0; n < menus.length; n ++) {
			if (menus[n] !=  null) {
				if (menus[n].getText() == menuTitle) {
					location = n;
				}
			}
		}

		//Create the menu if it exists
		if (location != -1) {

			JMenuItem menuItem = new JMenuItem(title);
			menuItem.addActionListener(events);
			if (tooltip.length() > 0) {
				menuItem.setToolTipText(tooltip);
			}
			menus[location].add(menuItem);
		}
	}

	//Method that allows the addition of a menu item with a location
	public void addMenuItem(int location, String title) {

		//Create the menu item if it's within range
		if (location >= 0 && location < menus.length) {

			JMenuItem menuItem = new JMenuItem(title);
			menuItem.addActionListener(events);
			menus[location].add(menuItem);
		}
	}

	//Method that allows the addition of a menu item with a location
	public void addMenuItem(int location, String title, String tooltip) {

		//Create the menu item if it's within range
		if (location >= 0 && location < menus.length) {

			JMenuItem menuItem = new JMenuItem(title);
			menuItem.addActionListener(events);
			if (tooltip.length() > 0) {
				menuItem.setToolTipText(tooltip);
			}
			menus[location].add(menuItem);
		}
	}

	//Method to return the menus
	public JMenu[] getMenus() {
		return this.menus;
	}

	//Class to respond to action events from components
	private class ComponentEvent implements ActionListener {

		private EventManager events;

		//Class constructor
		public ComponentEvent(EventManager events) {

			this.events = events;
		}

		//Method to respond to events from components
		public void actionPerformed(ActionEvent e) {

			//Get the ID of the event
			String ID = e.getActionCommand();

			//Call the event class
			events.events(ID);
		}

	}
}
