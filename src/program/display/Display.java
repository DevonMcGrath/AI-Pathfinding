package program.display;

import java.awt.BorderLayout;

import javax.swing.*;

import program.tools.ResourceLoader;

public class Display extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Constructor - creates the JFrame
	public Display(boolean isMaximized, String title, int length, int height) {

		//Initialize the display
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(length, height);
		if (isMaximized) {
			this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
		this.setLocationRelativeTo(null);
		this.setTitle(title);
		this.setLayout(new BorderLayout());
	}

	//Constructor - creates the JFrame with an image icon set
	public Display(boolean isMaximized, String title, int length,
			int height, String imageName) {

		//Initialize the display
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(length, height);
		if (isMaximized) {
			this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
		this.setLocationRelativeTo(null);
		this.setTitle(title);
		this.setIconImage(ResourceLoader.loadImage(imageName));
		this.setLayout(new BorderLayout());
	}

	//Sets the menu bar
	public void setMenu(DisplayMenu menu) {

		//Loop through the list of menus
		int addCount = 0;
		JMenu[] menus = menu.getMenus();
		for (int n = 0; n < menus.length; n ++) {
			if (menus[n] != null) {
				menu.add(menus[n]);
				addCount ++;
			}
		}
		
		//Only add the menu if it had elements
		if (addCount > 0) {
			this.setJMenuBar(menu);
		}
	}
}
