package com.skilldistillery.filmquery.app;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.skilldistillery.filmquery.database.DatabaseAccessor;
import com.skilldistillery.filmquery.database.DatabaseAccessorObject;
import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class FilmQueryApp {

	private DatabaseAccessor db = new DatabaseAccessorObject();

	public static void main(String[] args) throws SQLException {
		FilmQueryApp app = new FilmQueryApp();
//		app.test();
		app.launch();
	}

//	private void test() throws SQLException {
//		Film film = db.findFilmById(1);
//		System.out.println(film);
//	}

	private void launch() {
		Scanner input = new Scanner(System.in);

		startMenu(input);

		input.close();
	}

	private void startMenu(Scanner input) {
		int choice = 0;

		do {
			System.out.println("1. Look up film by ID");
//			System.out.println("2. Look up actor by ID");
			System.out.println("2. Look up a film by a search keyword");
			System.out.println("3. Exit");
			System.out.print("Enter your choice: ");

			try {
				choice = input.nextInt();

				switch (choice) {
				case 1:
					filmLookup(input);
					break;
//				case 2:
//					actorLookup(input);
//					break;
				case 2:
					lookupFilmByKeyword(input);
					break;
				case 3:
					System.out.println("Exiting the application");
					break;
				default:
					System.out.println("Invalid choice. Please enter a valid option.");
				}
			} catch (Exception e) {
				System.out.println("Invalid input. Please enter a number.");
				input.nextLine();
			}

		} while (choice != 3);
		input.close();
	}

	private void filmLookup(Scanner input) {
		System.out.print("Enter the film ID: ");
		int filmId = input.nextInt();
		try {
			Film film = db.findFilmById(filmId);

			if (film != null) {
				displayFilmDetails(film);

			} else {
				System.out.println("No film found with ID: " + filmId);
			}
		} catch (Exception e) {
			System.out.println("Invalid input. Please enter a number.");
			input.nextLine();
		}
	}

//	private void actorLookup(Scanner input) {
//		System.out.print("Enter the actor ID: ");
//		try {
//			int actorId = input.nextInt();
//			Actor actor = db.findActorById(actorId);
//
//			if (actor != null) {
//				System.out.println("Actor found:");
//				System.out.println(actor);
//			} else {
//				System.out.println("No actor found with ID: " + actorId);
//			}
//		} catch (Exception e) {
//			System.out.println("Invalid input. Please enter a number.");
//			input.nextLine();
//		}
//	}

	private void lookupFilmByKeyword(Scanner input) {
		System.out.print("Enter the search keyword: ");
		String keyword = input.next();
		try {
			List<Film> films = db.findFilmByKeyword(keyword);
			if (!films.isEmpty()) {
				displayFilmList(films);
			} else {
				System.out.println("No films found for the keyword: " + keyword);
			}
		} catch (SQLException e) {
			System.out.println("Error retrieving film information. Please try again.");
			e.printStackTrace();
		}
	}

	private void displayFilmList(List<Film> films) {
		System.out.println("\nFilm List:");
		for (Film film : films) {
			displayFilmDetails(film);
		}
	}

	private void displayFilmDetails(Film film) {
		System.out.println("\nFilm Details:");
		System.out.println("ID: " + film.getId());
		System.out.println("Title: " + film.getTitle());
		System.out.println("Release Year: " + film.getReleaseYear());
		System.out.println("Rating: " + film.getRating());
		System.out.println("Description: " + film.getDescription());
		System.out.println("Language: " + getLanguageName(film.getLanguageId()));
		displayFilmCast(film.getCast());
	}

	private void displayFilmCast(List<Actor> cast) {
		if (cast != null && !cast.isEmpty()) {
			System.out.println("Cast:");
			for (Actor actor : cast) {
				System.out.println(actor.getFirstName() + " " + actor.getLastName());
			}
		}
	}

	private String getLanguageName(int languageId) {
		String languageName = db.getLanguageNameById(languageId);
		return languageName != null ? languageName : "Unknown";
	}
}
