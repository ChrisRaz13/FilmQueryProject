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
	public DatabaseAccessorObject() {

	}

	private static final String URL = "jdbc:mysql://localhost:3306/sdvid";
	private static final String USER = "student";
	private static final String PWD = "student";

	@Override
	public Film findFilmById(int filmId) throws SQLException {
		Film film = null;
		try (Connection conn = DriverManager.getConnection(URL, USER, PWD)) {
			String sql = "SELECT f.*, l.name AS language_name " + "FROM film f "
					+ "JOIN language l ON f.language_id = l.id " + "WHERE f.id = ?";

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, filmId);

				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						film = createFilmFromResultSet(rs);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return film;
	}

	@Override
	public Actor findActorById(int actorId) throws SQLException {
		Actor actor = null;
		try (Connection conn = DriverManager.getConnection(URL, USER, PWD)) {
			String sql = "SELECT id, first_name, last_name FROM actor WHERE id = ?";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, actorId);
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						actor = new Actor();
						actor.setId(rs.getInt("id"));
						actor.setFirstName(rs.getString("first_name"));
						actor.setLastName(rs.getString("last_name"));
						actor.setFilms(findFilmsByActorId(actorId, conn));

					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return actor;
	}

	@Override
	public List<Actor> findActorsByFilmId(int filmId) throws SQLException {
		List<Actor> actors = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection(URL, USER, PWD)) {
			String sql = "SELECT a.id, a.first_name, a.last_name " + "FROM actor a "
					+ "JOIN film_actor fa ON a.id = fa.actor_id " + "WHERE fa.film_id = ?";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, filmId);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						Actor actor = new Actor();
						actor.setId(rs.getInt("id"));
						actor.setFirstName(rs.getString("first_name"));
						actor.setLastName(rs.getString("last_name"));

						actors.add(actor);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return actors;
	}

	private List<Film> findFilmsByActorId(int actorId, Connection conn) throws SQLException {
		List<Film> films = new ArrayList<>();

		String sql = "SELECT f.* FROM film f " + "JOIN film_actor fa ON f.id = fa.film_id " + "WHERE fa.actor_id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, actorId);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Film film = new Film();
					film.setId(rs.getInt("id"));
					film.setTitle(rs.getString("title"));
					film.setDescription(rs.getString("description"));

					films.add(film);

				}
			}
		}

		return films;
	}

	@Override
	public List<Film> findFilmByKeyword(String keyword) throws SQLException {
		List<Film> films = new ArrayList<>();
		String sql = "SELECT f.*, l.name AS language_name, f.rating " + "FROM film f "
				+ "JOIN language l ON f.language_id = l.id " + "WHERE title LIKE ? OR description LIKE ?";
		try (Connection conn = DriverManager.getConnection(URL, USER, PWD);
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, "%" + keyword + "%");
			stmt.setString(2, "%" + keyword + "%");

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Film film = createFilmFromResultSet(rs);
					films.add(film);
				}
			}
		}
		return films;
	}

	private Film createFilmFromResultSet(ResultSet rs) throws SQLException {
		Film film = new Film();
		film.setId(rs.getInt("id"));
		film.setTitle(rs.getString("title"));
		film.setDescription(rs.getString("description"));
		film.setReleaseYear(rs.getInt("release_year"));
		film.setLanguageId(rs.getInt("language_id"));
		film.setLanguage(rs.getString("language_name"));
		film.setRating(rs.getString("rating"));
		film.setCast(findActorsByFilmId(film.getId()));

		List<Actor> cast = findActorsByFilmId(film.getId());
		film.setCast(cast);

		String languageName = getLanguageNameById(film.getLanguageId());
		film.setLanguage(languageName);

		return film;
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

	@Override
	public String getLanguageNameById(int languageId) {
		String languageName = null;
		String sql = "SELECT name FROM language WHERE id = ?";
		try (Connection conn = DriverManager.getConnection(URL, USER, PWD);
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, languageId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					languageName = rs.getString("name");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return languageName;
	}
}