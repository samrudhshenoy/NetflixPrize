import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import model.Movie;
import model.Rating;
import model.Tag;
import model.User;


public class NetflixPredictor {


	ArrayList<Movie> movies;
	ArrayList<User> users;

	private static double avgDiff = .22;
	private static int nums = 1;

	/**
	 * 
	 * Use the file names to read all data into some local structures. 
	 * 
	 * @param movieFilePath The full path to the movies database.
	 * @param ratingFilePath The full path to the ratings database.
	 * @param tagFilePath The full path to the tags database.
	 * @param linkFilePath The full path to the links database.
	 */
	public NetflixPredictor (String movieFilePath, String ratingFilePath, String tagFilePath, String linkFilePath) {
		MovieLensCSVTranslator translator = new MovieLensCSVTranslator();
		
		movies = new ArrayList<Movie>();
		users = new ArrayList<User>();
		
		try {
		ArrayList<String> movieStrs = FileIO.readFile(movieFilePath);
		ArrayList<String> links = FileIO.readFile(linkFilePath);
		ArrayList<String> ratings = FileIO.readFile(ratingFilePath);
		ArrayList<String> tags = FileIO.readFile(tagFilePath);
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

		Collections.sort(movies);
		
	}
		
	/**
	 * If userNumber has rated movieNumber, return the rating. Otherwise, return -1.
	 * 
	 * @param userNumber The ID of the user.
	 * @param movieNumber The ID of the movie.
	 * @return The rating that userNumber gave movieNumber, or -1 if the user does not exist in the database, the movie does not exist, or the movie has not been rated by this user.
	 */
	public double getRating(int userID, int movieID) {
		
		int i = Collections.binarySearch(movies, new Movie(movieID));

		if (i < 0)
			return -1;
		
		Movie m = movies.get(i);
		
		return m.getRating(userID);
	}
	
	/**
	 * If userNumber has rated movieNumber, return the rating. Otherwise, use other available data to guess what this user would rate the movie.
	 * 
	 * @param userNumber The ID of the user.
	 * @param movieNumber The ID of the movie.
	 * @return The rating that userNumber gave movieNumber, or the best guess if the movie has not been rated by this user.
	 * @pre A user with id userID and a movie with id movieID exist in the database.
	 */
	
	static int numTimes = 0;
	
	public double guessRating(int userID, int movieID) {
		
		if (getRating(userID, movieID) != -1)
			return getRating(userID, movieID);
		
		double avgMovieRating = getMoviesAvgNumRatings();
		double avgUserRating = getUsersAvgNumRatings();
		double mRating = getMovieAvgRating(movieID);
		double uRating = getUserAvgRating(userID);
		int genreSimilar = getGenreSimilarities(userID, movieID); // the greater, the better
		int leastFavGenreSimilar = getLeastFavGenreSimilarities(userID, movieID);
		int tagSimilarities = getTagSimilarities(userID, movieID); // the greater, the better
		double yearDifference = getYearRange(userID, movieID); // the smaller, the better
		double userBias = getUserBias(userID);
//		double movieBias = getMovieBias(movieID);
		
		
		if (tagSimilarities > 1)
			tagSimilarities = 1;
		
		double rating = 0;
		
		if (yearDifference > 9.7 && tagSimilarities == 0 && genreSimilar < 7) { // 9
			rating -= .27; // .25
		}
		
		rating += (double)tagSimilarities * .55; // .4
		
		rating += (double)genreSimilar * .078;
		
		rating -= (double)leastFavGenreSimilar * .085;
		
//		rating += .2*movieBias;
		
		if (getMovie(movieID).getNumRatings() < 1)
			return uRating + .7*rating - .05;
		
		if (uRating > 4.65 || uRating < 2) 
			return (3*uRating + mRating + .3*rating + 1.1*userBias)/3.85 - .05;
		
//			double biasFactor = getBiasFactor(movieID);
		
			
//			double ratioSum = 2.1;
//			double ratio = (double)getMovie(movieID).getNumRatings() / (double)getUser(userID).getNumRatings();
//			
//			double mRatio = (ratioSum/2) * ratio;
//			double uRatio = (ratioSum/2) / ratio;
//			
//			if (mRatio + uRatio > 2.0 && mRatio + uRatio < 2.2)
//				return (.7*rating + (mRatio+.2)*mRating + (uRatio-.2)*uRating + 1.4*userBias)/2.35;
			
			
			double mDiff = (getMovie(movieID).getNumRatings() - avgMovieRating);
			double uDiff = (getUser(userID).getNumRatings() - avgUserRating);
			
			
			ArrayList<Rating> ratings = getMovie(movieID).getRatings();
			
			if (mDiff < -7) {
				double finalRating = (.7 * rating + .8 * mRating + 1.5 * uRating + 1.4 * userBias) / 2.34;

				if (Math.abs(finalRating - uRating) > 1.3) {
					finalRating = finalRating + .7 * ((finalRating-uRating)/Math.abs(finalRating - uRating));
					return finalRating > 5 ? 5 : finalRating + .015;
				}
				
				return finalRating > 5 ? 5 : finalRating + .015;
			}

			if (uDiff < -103) {
				double finalRating = (.7 * rating + 1.7 * mRating + .6 * uRating + 1.4 * userBias) / 2.3;

				if (Math.abs(finalRating - uRating) > 1.3) {
					finalRating = finalRating + .7 * ((finalRating-uRating)/Math.abs(finalRating - uRating));
					return finalRating > 5 ? 5 : finalRating + .021;
				}
				
				return finalRating > 5 ? 5 : finalRating + .021;
			}

			else {
				double finalRating = (.7 * rating + 1.2 * mRating + 1.1 * uRating + 1.4 * userBias) / 2.33;
				
				if (Math.abs(finalRating - uRating) > 1.3) {
					finalRating = finalRating + .7 * ((finalRating-uRating)/Math.abs(finalRating - uRating));
					return finalRating > 5 ? 5 : finalRating + .016;
				}
				
				return finalRating > 5 ? 5 : finalRating + .016;
			}
			
			
//			0.8613416021340196
//			if (mDiff < -7) {
//				double finalRating = (.7 * rating + .8 * mRating + 1.5 * uRating + 1.4 * userBias) / 2.33;
//
//				if (Math.abs(finalRating - uRating) > 1.3) {
//					finalRating = finalRating + .7 * ((finalRating-uRating)/Math.abs(finalRating - uRating));
//					return finalRating > 5 ? 5 : finalRating + .015;
//				}
//				
//				return finalRating > 5 ? 5 : finalRating + .015;
//			}
//
//			if (uDiff < -103) {
//				double finalRating = (.7 * rating + 1.7 * mRating + .6 * uRating + 1.4 * userBias) / 2.3;
//
//				if (Math.abs(finalRating - uRating) > 1.3) {
//					finalRating = finalRating + .7 * ((finalRating-uRating)/Math.abs(finalRating - uRating));
//					return finalRating > 5 ? 5 : finalRating + .021;
//				}
//				
//				return finalRating > 5 ? 5 : finalRating + .021;
//			}
//
//			else {
//				double finalRating = (.7 * rating + 1.2 * mRating + 1.1 * uRating + 1.4 * userBias) / 2.33;
//				
//				if (Math.abs(finalRating - uRating) > 1.3) {
//					finalRating = finalRating + .7 * ((finalRating-uRating)/Math.abs(finalRating - uRating));
//					return finalRating > 5 ? 5 : finalRating + .016;
//				}
//				
//				return finalRating > 5 ? 5 : finalRating + .016;
//			}
		
	}
	
	
	public double getUserBias (int userID) {
		User u = getUser(userID);
		ArrayList<Movie> uMovies = u.getMovies();
		double biasFactor = 0.0;
		
		for (Movie m : uMovies) {
			biasFactor += (m.getRating(userID) - m.getAvgRating());
		}
		
		if (uMovies.size() == 0)
			return 0;
		
		return biasFactor/(double)uMovies.size();
	}
	
	public double getMovieBias (int movieID) {
		Movie m = getMovie(movieID);
		ArrayList<Rating> ratings = m.getRatings();
		double biasFactor = 0.0;
		
		for (Rating r : ratings) {
			biasFactor += (m.getRating(r.getUserID()) - getUserAvgRating(r.getUserID()));
		}
		
		if (ratings.size() == 0)
			return 0;
		
		return biasFactor/(double)ratings.size();
	}
	
	public double getMoviesAvgNumRatings () {
		double numRatings = 0.0;
		for (Movie m : movies) {
			numRatings += m.getNumRatings();
		}
		return numRatings/movies.size();
	}
	
	public double getUsersAvgNumRatings () {
		double numRatings = 0.0;
		for (User u : users) {
			numRatings += u.getNumRatings();
		}
		return numRatings/users.size();
	}
	
	public double guessRating(int userID, int movieID, double rating, int level) {
		
		if (level == 0)
			return (rating + getMovieAvgRating(movieID) + 1.2*getUserAvgRating(userID))/2.25;

		
		else {
			Movie m = getMovie(movieID);
			
			for (int i = 0; i < 5; i++) {
				double r = guessRating(userID, movieID, rating, level - 1);
				m.addRating(r, userID, movieID, m.getYear());
			}
			
			return (guessRating(userID, movieID, rating, level - 1) + getMovieAvgRating(movieID) + getUserAvgRating(userID))/3;
		}
		
	}
	
	public Movie getMovie (int movieID) {
		int i = Collections.binarySearch(movies, new Movie(movieID));

		if (i < 0)
			return null;
		
		Movie m = movies.get(i);
		
		return m;
	}
 	
	public int getGenreSimilarities(int userID, int movieID) {
		int rating = 0;

		int a = Collections.binarySearch(users, new User(userID));

		if (a < 0)
			return 0;

		User u = users.get(a);

		int e = Collections.binarySearch(movies, new Movie(movieID));

		if (e < 0)
			return 0;

		Movie m = movies.get(e);

		if (m.getAvgRating() >= .5) {
			ArrayList<String> favGenres = u.getFavGenres();
			if (favGenres.size() >= 1) {
				ArrayList<String> movieGenres = m.getGenres();
				for (int i = 0; i < movieGenres.size(); i++) {
					for (int j = 0; j < favGenres.size(); j++) {
						if (movieGenres.get(i).equals(favGenres.get(j))) {
							rating++;
						}
//						else if ((movieGenres.get(i).equalsIgnoreCase("romance") || movieGenres.get(i).equalsIgnoreCase("drama"))
//								&& favGenres.get(j).equalsIgnoreCase("romance") || favGenres.get(j).equalsIgnoreCase("drama")) {
//							rating++;
//						}
					}
				}
			}
		}

		return rating;
	}
	
	public int getLeastFavGenreSimilarities(int userID, int movieID) {
		int rating = 0;

		int a = Collections.binarySearch(users, new User(userID));

		if (a < 0)
			return 0;

		User u = users.get(a);

		int e = Collections.binarySearch(movies, new Movie(movieID));

		if (e < 0)
			return 0;

		Movie m = movies.get(e);

		if (m.getAvgRating() >= .5) {
			ArrayList<String> worstGenres = u.getLeastFavGenres();
			if (worstGenres.size() >= 1) {
				ArrayList<String> movieGenres = m.getGenres();
				for (int i = 0; i < movieGenres.size(); i++) {
					for (int j = 0; j < worstGenres.size(); j++) {
						if (movieGenres.get(i).equals(worstGenres.get(j))) {
							rating++;
						}
//						else if ((movieGenres.get(i).equalsIgnoreCase("romance") || movieGenres.get(i).equalsIgnoreCase("drama"))
//								&& favGenres.get(j).equalsIgnoreCase("romance") || favGenres.get(j).equalsIgnoreCase("drama")) {
//							rating++;
//						}
					}
				}
			}
		}

		return rating;
	}
	
	
	public double getYearRange(int userID, int movieID) {
		double rating = 0;
		
		int a = Collections.binarySearch(users, new User(userID));

		if (a < 0)
			return 0;

		User u = users.get(a);

		int e = Collections.binarySearch(movies, new Movie(movieID));

		if (e < 0)
			return 0;

		Movie m = movies.get(e);

		if (m.getAvgRating() >= .5) {
			rating = Math.abs(u.getFavYear() - m.getYear());
		}

		return rating;
	}
	
	public int getTagSimilarities(int userID, int movieID) {
		int rating = 0;

		int a = Collections.binarySearch(users, new User(userID));

		if (a < 0)
			return 0;

		User u = users.get(a);

		int e = Collections.binarySearch(movies, new Movie(movieID));

		if (e < 0)
			return 0;

		Movie m = movies.get(e);

		if (m.getAvgRating() >= .5) {
			ArrayList<Tag> favTags = u.getFavTags();
			if (favTags.size() >= 1) {
				ArrayList<Tag> tags = m.getTags();
				for (int i = 0; i < tags.size(); i++) {
					for (int j = 0; j < favTags.size(); j++) {
						if (tags.get(i).getTag().equals(favTags.get(j).getTag())) {
							rating++;
						}
						else if (tags.get(i).getTag().toLowerCase().contains("roman") && favTags.get(j).getTag().toLowerCase().contains("roman")) {
							rating++;
						}
					}
				}
			}
		} 
		return rating;
	}
	
	public double getUserAvgRating (int userID) {
		int i = Collections.binarySearch(users, new User(userID));

		if (i < 0)
			return -1;
		
		User u = users.get(i);
		
		return u.getAvgRating();
	}
	
	public double getMovieAvgRating (int movieID) {
		int i = Collections.binarySearch(movies, new Movie(movieID));

		if (i < 0)
			return -1;
		
		Movie m = movies.get(i);
		
		return m.getAvgRating();
	}
	
	public double getBiasFactor (int movieID) {
		double avgRating = getMovieAvgRating(movieID);
		Movie m = getMovie(movieID);
		ArrayList<Rating> ratings = m.getRatings();
		double biasFactor = 0.0;
		
		// user's rating - movie's avg rating
		
		for (Rating r : ratings) {
			biasFactor += (r.getRating() - getUser(r.getUserID()).getAvgRating());
		}
		
		if (ratings.size() == 0)
			return 0;
		
		return biasFactor/ratings.size();
	}
	
	public static double roundToHalf(double d) {
	    return Math.round(d * 2) / 2.0;
	}
	
	/**
	 * Recommend a movie that you think this user would enjoy (but they have not currently rated it). 
	 * 
	 * @param userNumber The ID of the user.
	 * @return The ID of a movie that data suggests this user would rate highly (but they haven't rated it currently).
	 * @pre A user with id userID exists in the database.
	 */
	public int recommendMovie(int userID) {
		double bestRating = 0;
		int bestID = 0;
		for (Movie m : movies) {
			if (getRating(userID, m.getID()) == -1) {
				double guessedRating = guessRating(userID, m.getID());
				if (guessedRating > bestRating) {
					bestRating = guessedRating;
					bestID = m.getID();
				}
			}
		}
		
		
		return bestID;
	}
	
	public ArrayList<Movie> getMovies () {
		return movies;
	}
	
	public User getUser (int userID) {
		int i = Collections.binarySearch(users, new User(userID));

		if (i < 0)
			return null;
		
		User u = users.get(i);
		
		return u;
	}
	
	public  double calcStanDev (ArrayList<Rating> table)
	{
	    // Step 1: 
	    double mean = mean(table);
	    double temp = 0;

	    for (int i = 0; i < table.size(); i++)
	    {
	        double val = table.get(i).getRating();

	        // Step 2:
	        double squrDiffToMean = Math.pow(val - mean, 2);

	        // Step 3:
	        temp += squrDiffToMean;
	    }

	    // Step 4:
	    double meanOfDiffs = (double) temp / (double) (table.size());

	    // Step 5:
	    return Math.sqrt(meanOfDiffs);
	}
	
	public double mean (ArrayList<Rating> ratings) {
		double avg = 0;
		for (Rating r : ratings) {
			avg += r.getRating();
		}
		if (ratings.size()  < 1)
			return 3.3;
		return avg/ratings.size();
	}
	
}
