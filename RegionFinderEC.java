import java.awt.*;
import java.awt.image.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 */
public class RegionFinder {
	private static final int maxColorDiff = 20 ;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering
	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;			// a region is a list of points


	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;		
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
		//Instantiate regions and visited image
		regions = new ArrayList<ArrayList<Point>>();
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		// Loop through all the pixels
		for (int y = 0; y < image.getHeight(); y ++) {
			for (int x = 0; x < image.getWidth(); x++) {
				// Get the color for that pixel
				Color firstColor = new Color(image.getRGB(x,y));
				//If pixel is unvisited and of same color as selected color, start a new region and start to keep track of
				// points that need to be visited
				if (visited.getRGB(x,y) == 0 && colorMatch(firstColor, targetColor) ) {
					ArrayList<Point> region = new ArrayList<>();
					Stack<Point> toVisit = new Stack<>();
					Point point = new Point(x,y);
					toVisit.push(point);
					// While points that need to be visited are not empty, get a point nd mark it as visited, also add to
					// region because it is an appropriate point
					while(!toVisit.empty()) {
						Point curr = toVisit.pop();
						region.add(curr);
						System.out.println(curr.x - 1 < 0 );
						visited.setRGB(curr.x, curr.y,1);

						// If the point above the current point is unvisited and of the same color add it to visit.
						if (curr.y - 1 > 0 && visited.getRGB(curr.x,curr.y - 1) == 0) {
							Point upper = new Point(curr.x,curr.y - 1);
							Color upperColor = new Color(image.getRGB(curr.x,curr.y - 1));
							if (colorMatch(upperColor,targetColor)) {
								toVisit.push(upper);
							}
						}
						// If the point below the current point is unvisited and of the same color add it to visit.
						if (curr.y + 1 < image.getHeight() && visited.getRGB(curr.x,curr.y + 1) == 0) {
							Point below = new Point(curr.x, curr.y + 1);
							Color belowColor = new Color(image.getRGB(curr.x,curr.y + 1));
							if (colorMatch(belowColor,targetColor)) {
								toVisit.push(below);
							}
						}
						// If the point to the right of the current point is unvisited and of the same color add it to visit.
						if (curr.x + 1 < image.getWidth()  && visited.getRGB(curr.x  + 1, curr.y) == 0) {
							Point right = new Point(curr.x + 1, curr.y);
							Color rightColor = new Color(image.getRGB(curr.x + 1,curr.y));
							if (colorMatch(rightColor,targetColor)) {
								toVisit.push(right);
							}
						}
						// If the point to the left of the current point is unvisited and of the same color add it to visit.
						if (curr.x - 1 > 0 && visited.getRGB(curr.x - 1  ,curr.y) == 0) {
							Point left = new Point(curr.x - 1, curr.y);
							Color leftColor = new Color(image.getRGB(curr.x - 1,curr.y));
							if (colorMatch(leftColor,targetColor)) {
								visited.setRGB(curr.x - 1, curr.y,1);
								toVisit.push(left);
							}
						}
					}
					// If the region full of points is larger than the minimum region side,
					// add it to the list of regions so it can be checked
					// in largest region finding method
					if (region.size() > minRegion) {
						regions.add(region);

					}

				}

			}
		}

	}

 		// TODO: YOUR CODE HERE


	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		// color matching method that takes the color difference for each of the R G B
		// measurements between two colors
		boolean isClose = false;
		int d = (int) Math.sqrt((c1.getRed() - c2.getRed())*(c1.getRed() - c2.getRed()));
		int b = (int) Math.sqrt((c1.getGreen() - c2.getGreen())*(c1.getGreen() - c2.getGreen()));
		int c = (int) Math.sqrt((c1.getBlue() - c2.getBlue())*(c1.getBlue() - c2.getBlue()));
		if (d <= maxColorDiff && b <= maxColorDiff && c <= maxColorDiff) {
			isClose = true;
		}
		return isClose;
			
	}


	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {

		// Method to find largest region out of a array list of region, used by comparing regions and
		// seeing which one is the largest via a for loop.

		ArrayList<Point> largest = new ArrayList<Point>();

		for (ArrayList<Point> region: regions) {
			if (region.size() > largest.size()) {
				largest = region;
			}
		}

		// TODO: YOUR CODE HERE

		return largest;
	}
	public  ArrayList<Point> secondLargestRegion() { // EC: effort to find second largest region to use as second brush

		ArrayList<Point> largest = new ArrayList<Point>();
		ArrayList<Point> second = new ArrayList<>();

		for (ArrayList<Point> region: regions) {
			if (region.size() > largest.size()) {
				second = largest;
				largest = region;
			}
			else if (region.size() > second.size()) {
				second = region;

			}
		}

		return second;
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		if (image != null) { // skip first frame
			// Nested loop over every pixel
			for (ArrayList<Point> region: regions) {
				int re = (int) (Math.random() * 1677000);
				for (Point point: region) recoloredImage.setRGB(point.x,point.y, re);
				// Taken from the CS10 website for basic recoloring code
			}
		}
	}
		// TODO: YOUR CODE HERE

}
