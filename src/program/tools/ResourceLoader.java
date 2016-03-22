package program.tools;

import java.awt.Image;

import javax.imageio.ImageIO;

public class ResourceLoader {

	//Method to load images from a relative location
	public static Image loadImage(String imageName) {

		Image image = null;

		//Get the image
		try{
			image = ImageIO.read(ResourceLoader.class.getResource(
					"/program/images/"+imageName+".png"));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		//Return the image
		return image;
	}
	
	//Method to get an image
	public static Image getImage(String path) {
		
		Image image = null;

		//Return the image
		return image;
	}

}
