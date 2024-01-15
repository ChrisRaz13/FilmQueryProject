package com.skilldistillery.filmquery.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class DatabaseAccessorObject implements DatabaseAccessor {

	private static final String URL = "jdbc:mysql://localhost:3306/sdvid";
	private static final String USER = "student";
	private static final String PWD = "student";

	public Actor findActorById(int actorId) throws SQLException {
		Actor actor = null;
		try (Connection conn = DriverManager.getConnection(URL, USER, PWD)) {
			String sql = "SELECT id, first_name, last_name FROM actor WHERE id = ?";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, actorId);
				try (ResultSet actorResult = stmt.executeQuery()) {
					if (actorResult.next()) {
						actor = new Actor();
						actor.setId(actorResult.getInt(1));
						actor.setFirstName(actorResult.getString(2));
						actor.setLastName(actorResult.getString(3));
						actor.setFilms(findActorsByFilmId(actorId, URL, USER, PWD)); 
					}
				}
			}
		}

		return actor;
	}


	@Override
	public Film findFilmById(int filmId) throws SQLException {
		Film film = null;
		try (Connection conn = DriverManager.getConnection(URL, USER, PWD)) {
			String sql = "SELECT * FROM film WHERE id = ?";

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, filmId);

				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						film = new Film();
						film.setId(filmId);
						film.setDescription(rs.getString("description"));
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return film;
	}

	public List<Film> findActorsByFilmId(int actorId, String url, String user, String pass) {
		List<Film> films = new ArrayList<>();
		try {
			Connection conn = DriverManager.getConnection(url, user, pass);
			String sql = "SELECT id, title, description, release_year, language_id, rental_duration, ";
			sql += " rental_rate, length, replacement_cost, rating, special_features "
					+ " FROM film JOIN film_actor ON film.id = film_actor.film_id " + " WHERE actor_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, actorId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int Id = rs.getInt(1);
				String title = rs.getString(2);
				String desc = rs.getString(3);
				short releaseYear = rs.getShort(4);
				int langId = rs.getInt(5);
				int rentDur = rs.getInt(6);
				double rate = rs.getDouble(7);
				int length = rs.getInt(8);
				double repCost = rs.getDouble(9);
				String rating = rs.getString(10);
				String features = rs.getString(11);
				Film film = new Film(Id, title, desc, releaseYear, langId, rentDur, rate, length, repCost, rating,
						features);
				films.add(film);
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return films;
	}

	@Override
	public List<Actor> findActorsByFilmId(int filmId) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public static String getUrl() {
		return URL;
	}

	public static String getUser() {
		return USER;
	}

	public static String getPwd() {
		return PWD;
	}
}