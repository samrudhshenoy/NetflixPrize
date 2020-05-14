package model;

import java.util.ArrayList;

public class User implements Comparable<User> {

	private int id;
	private ArrayList<Movie> movies;
	private ArrayList<Integer> favYears;
	
	public User (int id) {
		this.id = id;
		movies = new ArrayList<Movie>();
		favYears = new ArrayList<Integer>();
	}
	
	public User (int id, ArrayList<Movie> movies) {
		this.id = id;
		this.movies = movies;
		favYears = new ArrayList<Integer>();
		for (Movie m : movies) {
			if (m.getRating(id) == 5) {
				favYears.add(m.getYear());
			}
		}
	}
	
	public int getID () {
		return id;
	}
	
	public void addMovie (Movie m) {
		movies.add(m);
		for (Movie mov : movies) {
			if (mov.getRating(id) == 5) {
				favYears.add(mov.getYear());
			}
		}
	}
	
	public Movie getMovie (int id) {
		for (Movie m : movies) {
			if (m.getID() == id)
				return m;
		}
		
		return null;
	}
	
	
	public String toString () {
		String output = new String();
		for (Movie m : movies) {
			output +=  id + " | " + m.toString() + "\n";
		}
		
		return output;
	}
	
	public double getAvgRating () {
		double rating = 0;
		int i = 0;
		for (Movie m : movies) {
			if (m.getRating(id) != -1) {
				rating += m.getRating(id);
				i++;
			}
		}
		
		return rating/i;
	}
	
	public ArrayList<Movie> getMovies () {
		return movies;
	}
	
	public ArrayList<String> getFavGenres() {
		ArrayList<String> genres = new ArrayList<String>();
		for (Movie m : movies) {
			if (m.getRating(id) == 5) {
				ArrayList<String> g = m.getGenres();
				for (int i = 0; i < g.size(); i++) {

					if (!genres.contains(g.get(i)))
						genres.add(g.get(i));
				}
			}
		}
		return genres;
	}
	
	public double getFavYear() {
		double year = 0.0;
		int i = 0;
		for (Movie m : movies) {
			if (m.getRating(id) == 5) {
				year += m.getYear();
				i++;
			}
		}
		return year/i;
	}
	
	public ArrayList<Integer> getFavYears() {
		return favYears;
	}
	
	public ArrayList<Tag> getFavTags() {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		for (Movie m : movies) {
			if (m.getRating(id) >= 4.5) {
				ArrayList<Tag> t = m.getTags();
				for (int i = 0; i < t.size(); i++) {
					boolean found = false;
					for (Tag tag : tags) {
						if (tag.getTag().equals(t.get(i).getTag())) {
							found = true;
							break;
						}
					}
					if (!found)
						tags.add(t.get(i));
				}
			}

		}
		
//		System.out.println(tags.toString());
		return tags;
	}
	
	public ArrayList<String> getLeastFavGenres() {
		ArrayList<String> genres = new ArrayList<String>();
		for (Movie m : movies) {
			if (m.getRating(id) < 1.0) {
				ArrayList<String> g = m.getGenres();
				for (int i = 0; i < g.size(); i++) {

					if (!genres.contains(g.get(i)))
						genres.add(g.get(i));
				}
			}
		}
		return genres;
	}
	
	public int getNumRatings () {
		return movies.size();
	}

	@Override
	public int compareTo(User o) {
		return id - o.id;
	}
	
}
