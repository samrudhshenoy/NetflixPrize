package model;

public class Tag {

	private final String tag;
	private final int userID;
	private final int movieID;
	private final long timestamp;
	
	public Tag(String tag, int userID, int movieID, long timestamp) {
		this.tag = tag.toString();
		this.userID = userID;
		this.movieID = movieID;
		this.timestamp = timestamp;
	}
	
	public String getTag()  {
		return tag;
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
}
