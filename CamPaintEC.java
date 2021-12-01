import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Webcam-based drawing 
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
 */
public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private final RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece
	private boolean twoRegion = false;


	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) {
		// Simply changes the webcam video being displayed as long as specified buttons
		// are pressed according to those instructed.

		if (image != null) {
			if (displayMode == 'w') {
				//System.out.println(targetColor);
				//System.out.println(finder.getRecoloredImage() != null);
				//System.out.println(twoRegion);
				super.draw(g);
			}
			if (displayMode == 'r') {
				g.drawImage(finder.getRecoloredImage(),0,0,null);
			}
			if (displayMode == 'p') {
				g.drawImage(painting,0,0,null);

		}



		}
		// TODO: YOUR CODE HERE
	}

	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	@Override
	public void processImage() {
		// Skips the first image and ensures that target color and image is not null
		// Then uses region finder to find regions of target color and updates the
		// image. Finally, each point in the largest region is set.

		if (super.image != null && targetColor != null) { // skip first frame
			finder.findRegions(targetColor);
			finder.recolorImage();
			for (Point point : finder.largestRegion()) {
				painting.setRGB(point.x, point.y, paintColor.getRGB());
			}
			if (twoRegion) { // EC: added the second largest region so in a way there can be two brushes
				for (Point point : finder.secondLargestRegion()) {
					painting.setRGB(point.x, point.y, Color.red.getRGB());

				}
			}



			// Nested loop over every pixel
		}
		// TODO: YOUR CODE HERE
	}

	/**
	 * Overrides the DrawingGUI method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		// Skipping first frame and ensuring the image is not null, the point at which it is
		// pressed on the webcam feed provides the selectable points for the mouse press
		// and sets the target color to the color at that point.


		if (super.image != null) {
			finder.setImage(super.image);
			targetColor = new Color(super.image.getRGB(x,y));


		}

		// TODO: YOUR CODE HERE
	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "pictures/painting.png", "png");
		}
		else if (k == 'b') {  // EC added ability to change colors
			paintColor = Color.red;
		}
		else if (k == 'a') {  // EC: added boolean to allow for two brushes in a way
			twoRegion = true;

		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}
