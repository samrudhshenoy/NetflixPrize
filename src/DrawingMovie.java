
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import model.Movie;
import processing.core.PApplet;
import processing.core.PImage;

public class DrawingMovie {

	private Movie movie;
	private PImage coverArt;
	
	public DrawingMovie(Movie m) {
		this.movie = m;
		coverArt = null;
	}
	
	public void draw(PApplet drawer, float x, float y, float width, float height) {
		
		if (movie != null) {
			if (coverArt != null) {
				drawer.image(coverArt, x, y,width,height);
			}
			
			String title = movie.getTitle();
			drawer.textSize(25);
			drawer.text(title, x, y-8);
			
		}
		drawer.stroke(0);
		drawer.noFill();
		drawer.rect(x, y, width, height);
	}
	

	public void downloadArt(PApplet drawer) {
		
		Thread downloader = new Thread(new Runnable() {

			@Override
			public void run() {
				
				Scanner scan = null;
				String url = "https://www.imdb.com/title/tt" + movie.getIMDBLink();
				
				try {
					String output = "";
					
					URL reader = new URL(url);
					scan = new Scanner(reader.openStream());
					
					while(scan.hasNextLine()) {
						String line = scan.nextLine();
						output += line + "\n";
					}
		
					int index = output.indexOf(".jpg");
					String out1 = output.substring(0, index);
					String out2 = output.substring(index);
					
					String imageURL = out1.substring(out1.lastIndexOf('"') + 1) + out2.substring(0, out2.indexOf('"'));
					
					coverArt = drawer.loadImage(imageURL);
					
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (scan != null)
						scan.close();
				}
				
			}
			
		});
		
		downloader.start();

	}

	
}
