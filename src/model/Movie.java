package model;
import java.util.ArrayList;

public class Movie implements Comparable<Movie> {

	private String title;
	private final int id;
	private int year;
	private ArrayList<String> genres;
	private ArrayList<Rating> ratings;
	private ArrayList<Tag> tags;
	private String imdbId;
	private String tmdbId;
	
	
	public Movie (int id, String title, int year, ArrayList<String> genres) {
		this.id = id;
		
		while (title.indexOf("\"") != -1) {
			title = title.substring(0, title.indexOf("\"")) + title.substring(title.indexOf("\"") + 1);
		}
		
		this.title = title;
		this.year = year;
		this.genres = new ArrayList<String>();
		for (int i = 0; i < genres.size(); i++) {
			this.genres.add(i, genres.get(i));
		}
		ratings = new ArrayList<Rating>();
		tags = new ArrayList<Tag>();
		imdbId = new String();
		tmdbId = new String();
	}
	
	public Movie (int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getIMDBLink() {
		return imdbId;
	}
	
	public double getAvgRating () {
		double rating = 0;
		for (Rating r : ratings) {
			rating += r.getRating();
		}
		
		if (ratings.size() == 0) {
			return 3.3;
		}
		
		return rating/ratings.size();
	}
	
	
	
	public double getRating (int userID) {
		double rating = -1;
		for (Rating r : ratings) {
			if (r.getUserID() == userID)
				rating = r.getRating();
		}
		
		return rating;
	}
	
	
	public int getID() {
		return id;
	}
	
	public int getYear() {
		return year;
	}
	
	public String toString() {
		double avg = 0.0;
		
		for (Rating r : ratings) {
			avg += r.getRating();
		}
		
		String tagsString = "{";
		
		for (Tag t : tags) {
			tagsString += t.getTag() + ", ";
		}
		
//		tagsString = tagsString.substring(0, tagsString.length() - 2) + "}";
		
		tagsString.concat("}");		
		
		return avg/ratings.size() + ", " + title + ", " + id + " | " + year + " | " + genres.toString() + " | " + imdbId + " | " + tmdbId + " | " + tagsString;
	}
	
	public void addTag(Tag t) {
		tags.add(t);
	}
	
	public void addRating(double rating, int userID, int movieID, long timestamp) {
		ratings.add(new Rating(rating, userID, movieID, timestamp));
	}
	
	public void setImdbId(String s) {
		imdbId = s;
	}
	
	public void setTmdbId(String s) {
		tmdbId = s;
	}
	
	public ArrayList<String> getGenres() {
		ArrayList<String> g = new ArrayList<String>();
		for (String s : genres) {
			g.add(s);
		}
		return g;
	}
	
	public ArrayList<Tag> getTags() {
		ArrayList<Tag> t = new ArrayList<Tag>();
		for (Tag s : tags) {
			t.add(s);
		}
		return t;
	}
	
	public void addGenre(String genre) {
		genres.add(genre);
	}
	
	public int getNumRatings () {
		return ratings.size();
	}
	
	public ArrayList<Rating> getRatings () {
		return ratings;
	}
	
	@Override
	public int compareTo(Movie o) {
		return id - o.id;
	}
	
}
