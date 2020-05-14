
import java.io.IOException;
import java.util.ArrayList;

import model.Movie;
import model.User;

public class Tester {

	public static void main(String[] args) {

		MovieLensCSVTranslator translator = new MovieLensCSVTranslator();
		ArrayList<Movie> movies = new ArrayList<Movie>();
		ArrayList<User> users = new ArrayList<User>();
		
		
		try {
		String movieCSV = "small" + FileIO.fileSep + "movies.csv";
		String linksFile = "small" + FileIO.fileSep + "links.csv";
		String ratingsFile = "small" + FileIO.fileSep + "ratings.csv";
		String tagsFile = "small" + FileIO.fileSep + "tags.csv";
		ArrayList<String> movieStrs = FileIO.readFile(movieCSV);
		ArrayList<String> links = FileIO.readFile(linksFile);
		ArrayList<String> ratings = FileIO.readFile(ratingsFile);
		ArrayList<String> tags = FileIO.readFile(tagsFile);
		movieStrs.remove(0);
		links.remove(0);
		ratings.remove(0);
		tags.remove(0);
		
		for (String s : movieStrs) {
			Movie m = translator.translateMovie(s);
			movies.add(m);
		}
		
		for (String s : links) {
			translator.readLinks(s, movies);
		}
		
		for (String s : ratings) {
			translator.readRatings(s, movies, users);
		}
		
		for (String s : tags) {
			translator.readTags(s, movies, users);
		}
		
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		
//		for (Movie m : movies) {
//			System.out.println(m);
//		}

		for (User u : users) {
			System.out.println(u.toString());
		}	
	}

}
