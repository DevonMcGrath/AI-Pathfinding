package info;

public class Author {
	
	//Returns a string with info about the author
	public static String getAuthorInfo() {
		
		String info = "This program was created to demonstrate the use\n"
				+ "of AI pathfinding. The program does not use any trees\n"
				+ "in it's decision making. Instead, the program weighs\n"
				+ "each move and picks the one with the highest weight.\n\n"
				+ "Like most of my programs that have relatively simple\n"
				+ "graphics, this one also uses JComponents such as a\n"
				+ "JFrame, JPanel, etc.";
		
		String result = "Author: " + Program.AUTHOR + "\nStarted: "
				+ Program.START_DATE + "\nLast Modified: "
				+ Program.LAST_MODIFIED + "\n\n" + info;
		
		return result;
	}

}
