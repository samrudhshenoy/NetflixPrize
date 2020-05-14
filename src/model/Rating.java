package model;

public class Rating implements Comparable<Rating>{

	private final double rating;
	private final int userID;
	private final int movieID;
	private final long timestamp;
	
	public Rating(double rating, int userID, int movieID, long timestamp) {
		this.rating = rating;
		this.userID = userID;
		this.movieID = movieID;
		this.timestamp = timestamp;
	}
	
	public double getRating()  {
		return rating;
	}
	
	public int getUserID() {
		return userID;
	}
	
	public int getMovieID() {
		return movieID;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public String toString() {
		return rating + ", " + timestamp;
	}

	@Override
	public int compareTo(Rating o) {
		return 0;
	}
}
