package com.skilldistillery.filmquery.app;

import java.sql.SQLException;
import java.util.Scanner;

import com.skilldistillery.filmquery.database.DatabaseAccessor;
import com.skilldistillery.filmquery.database.DatabaseAccessorObject;
import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class FilmQueryApp {

	private DatabaseAccessor db = new DatabaseAccessorObject();

	public static void main(String[] args) throws SQLException {
		FilmQueryApp app = new FilmQueryApp();
		app.test();
		app.launch();
	}

	private void test() throws SQLException {
		Film film = db.findFilmById(1);
		System.out.println(film);
	}

	private void launch() {
		Scanner input = new Scanner(System.in);

		startUserInterface(input);

		input.close();
	}

	private void startUserInterface(Scanner input) {
		int choice = 0;

		do {
			System.out.println("1. Look up film by ID");
			System.out.println("2. Look up actor by ID");
			System.out.println("3. Exit");
			System.out.print("Enter your choice: ");

			try {
				choice = input.nextInt();

				switch (choice) {
				case 1:
					handleFilmLookup(input);
					break;
				case 2:
					handleActorLookup(input);
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
	}

	private void handleFilmLookup(Scanner input) {
		System.out.print("Enter the film ID: ");
		try {
			int filmId = input.nextInt();
			Film film = db.findFilmById(filmId);

			if (film != null) {
				System.out.println("Film found:");
				System.out.println(film);
			} else {
				System.out.println("No film found with ID: " + filmId);
			}
		} catch (Exception e) {
			System.out.println("Invalid input. Please enter a number.");
			input.nextLine();
		}
	}

	private void handleActorLookup(Scanner input) {
		System.out.print("Enter the actor ID: ");
		try {
			int actorId = input.nextInt();
			Actor actor = db.findActorById(actorId);

			if (actor != null) {
				System.out.println("Actor found:");
				System.out.println(actor);
			} else {
				System.out.println("No actor found with ID: " + actorId);
			}
		} catch (Exception e) {
			System.out.println("Invalid input. Please enter a number.");
			input.nextLine();
		}
	}
}
