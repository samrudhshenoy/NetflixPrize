
import java.util.ArrayList;

import model.Movie;
import model.Rating;
import model.Tag;
import model.User;

public class MovieLensCSVTranslator {

	
	
	public Movie translateMovie (String line) {
		
		String title = new String();
		int id = 0;
		int year = 0;
		ArrayList<String> genresList = new ArrayList<String>();
		
		if (Character.isDigit(line.charAt(0))) {
		
			id = Integer.parseInt(line.substring(0, line.indexOf(',')));
		
			if(line.lastIndexOf('(') > 0 && line.indexOf(',') > 0)
				title = line.substring(line.indexOf(',') + 1, line.lastIndexOf('(') - 1);
			
			if (line.contains("("))
				if(Character.isDigit(line.charAt(line.lastIndexOf(')') - 1)))
					year = Integer.parseInt(line.substring(line.lastIndexOf('(') + 1, line.lastIndexOf(')')));
			
			String genres = line.substring(line.lastIndexOf(',') + 1, line.length());
			
			
			if (line.contains("|")) {
					while (genres.indexOf('|') > 0) {
						genresList.add(genres.substring(0, genres.indexOf('|')));
						genres = genres.substring(genres.indexOf('|') + 1);
						if (genres.indexOf('|') == -1)
							genresList.add(genres);
					}
					
			}
			
			else
				genresList.add(genres);
			
			
			Movie m = new Movie(id, title, year, genresList);
			
			if ((line.toLowerCase().contains("kill") || line.toLowerCase().contains("death"))&& !m.getGenres().contains("Crime"))
				m.addGenre("Crime");
			
			if (line.toLowerCase().contains("life") && !m.getGenres().contains("Drama"))
				m.addGenre("Drama");
			
			
			return m; 
		}
		
		return new Movie(0, "", 0, new ArrayList<String>());
	}
	
	public void readLinks (String line, ArrayList<Movie> movies) {
		int id = Integer.parseInt(line.substring(0, line.indexOf(',')));
		for (Movie m : movies) {
			if (m.getID() == id) {
				m.setImdbId(line.substring(line.indexOf(',') + 1, line.lastIndexOf(',')));
				m.setTmdbId(line.substring(line.lastIndexOf(',') + 1));
				break;
			}
		}
	}
	
	
	public void readRatings (String line, ArrayList<Movie> movies, ArrayList<User> users) {
		int uID = Integer.parseInt(line.substring(0, line.indexOf(',')));
		String s = line.substring(line.indexOf(',') + 1);
		int mID = Integer.parseInt(s.substring(0, s.indexOf(',')));
		double rating = Double.parseDouble(s.substring(s.indexOf(',') + 1, s.lastIndexOf(',')));
		long timeStamp = Long.parseLong(line.substring(line.lastIndexOf(',') + 1));
		
//		System.out.println(uID);
		
		boolean found = false;
		int i = 0;
		for (; i < users.size(); i++) {
			if (users.get(i).getID() == uID) {
				found = true;
				for (int e = 0; e < movies.size(); e++) {
					if (movies.get(e).getID() == mID) {
						users.get(i).addMovie(movies.get(e));
						movies.get(e).addRating(rating, uID, mID, timeStamp);
					}
				}
					return;
			}
		}
		
		if (!found) {
			User u = new User(uID);
			for (int e = 0; e < movies.size(); e++) {
				if (movies.get(e).getID() == mID) {
					movies.get(e).addRating(rating, uID, mID, timeStamp);
					u.addMovie(movies.get(e));
				}
				
			}
			users.add(u);
		}
		
	}
	
	
	public void readTags (String line, ArrayList<Movie> movies, ArrayList<User> users) {
		int uID = Integer.parseInt(line.substring(0, line.indexOf(',')));
		String s = line.substring(line.indexOf(',') + 1);
		int mID = Integer.parseInt(s.substring(0, s.indexOf(',')));
		String tag = s.substring(s.indexOf(',') + 1, s.lastIndexOf(','));
		long timeStamp = Long.parseLong(line.substring(line.lastIndexOf(',') + 1));
		
		Tag t = new Tag(tag, uID, mID, timeStamp);
		
		for (int i = 0; i < movies.size(); i++) {
			if (movies.get(i).getID() == mID) {
				if (tag.toLowerCase().contains("romance") || tag.toLowerCase().contains("romantic") || tag.toLowerCase().contains("love")) {
					movies.get(i).addGenre("Romance");
				}
				
				if (tag.toLowerCase().contains("funny") || tag.toLowerCase().contains("humor") || tag.toLowerCase().contains("hilarious")) {
					movies.get(i).addGenre("Comedy");
				}
				
				
				movies.get(i).addTag(t);
			}
		}
		
	}
	
	
	
}
