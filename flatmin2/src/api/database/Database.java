package api.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import api.model.Ausgabe;
import api.model.AuthenticationToken;
import api.model.Einnahme;
import api.model.EinnahmeAusgabe;
import api.model.ShoppingItem;
import api.model.Username;
import api.model.Users;

public class Database
{
	private final static String TAG = "Database: ";

	public static boolean checkLogin(String username, String password)
	{
		return true;
	}

	public static ShoppingItem getShoppingItem(long id) throws SQLException
	{
		ShoppingItem shoppingItem = new ShoppingItem();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = DataSource.getConnection();
			ps = conn.prepareStatement("SELECT * FROM todolist WHERE idtodolist = ?");
			ps.setLong(1, id);
			rs = ps.executeQuery();

			while (rs.next())
			{
				shoppingItem.setIdtodolist(rs.getLong("idtodolist"));
				shoppingItem.setItem(rs.getString("item"));
			}

		} catch (Exception e)
		{

		} finally
		{
			if (conn != null)
				conn.close();
		}

		return shoppingItem;
	}

	public static List<ShoppingItem> getShoppingList() throws SQLException
	{
		List<ShoppingItem> list = new ArrayList<ShoppingItem>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = DataSource.getConnection();
			ps = conn.prepareStatement("SELECT * FROM todolist");
			rs = ps.executeQuery();

			while (rs.next())
			{
				long id = rs.getLong("idtodolist");
				String item = rs.getString("item");
				list.add(new ShoppingItem(id, item));
			}

		} catch (Exception e)
		{

		} finally
		{
			if (conn != null)
				conn.close();
		}

		return list;
	}

	public static ShoppingItem insertShoppingItem(ShoppingItem shoppingItem) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;

		try
		{
			conn = DataSource.getConnection();
			String query = "INSERT INTO todolist (item) VALUES (?)";
			ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, shoppingItem.getItem());
			int row = ps.executeUpdate();

			if (row == 0)
				throw new SQLException("Unable to insert item");

			try (ResultSet generatedKeys = ps.getGeneratedKeys())
			{
				if (generatedKeys.next())
					shoppingItem.setIdtodolist(generatedKeys.getLong(1));
				else
					throw new SQLException("Unable to insert item, no ID obtained.");
			}

		} catch (Exception e)
		{
			System.out.println("Couldn't insert item");
		} finally
		{
			if (conn != null)
				conn.close();
		}

		return shoppingItem;
	}

	public static ShoppingItem updateShoppingItem(ShoppingItem shoppingItem) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;

		try
		{
			conn = DataSource.getConnection();
			String query = "UPDATE todolist SET item = ? WHERE idtodolist = ?";
			ps = conn.prepareStatement(query);
			ps.setString(1, shoppingItem.getItem());
			ps.setLong(2, shoppingItem.getIdtodolist());
			ps.executeUpdate();

		} catch (Exception e)
		{
			System.out.println("Couldn't update item");
		} finally
		{
			if (conn != null)
				conn.close();
		}

		return shoppingItem;
	}

	public static void deleteShoppingItem(long id) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;

		try
		{
			conn = DataSource.getConnection();
			String query = "DELETE FROM todolist WHERE idtodolist = ?";
			ps = conn.prepareStatement(query);
			ps.setLong(1, id);
			ps.executeUpdate();

		} catch (Exception e)
		{
			System.out.println("Couldn't delete item");
		} finally
		{
			if (conn != null)
				conn.close();
		}
	}

	public static Users getUser(long id) throws SQLException
	{
		Users user = new Users();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = DataSource.getConnection();
			ps = conn.prepareStatement("SELECT * FROM users WHERE idusers = ?");
			ps.setLong(1, id);
			rs = ps.executeQuery();

			while (rs.next())
			{
				user.setIdusers(rs.getLong("idusers"));
				user.setFirst(rs.getString("first"));
				user.setLast(rs.getString("last"));
				user.setEmail(rs.getString("email"));
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
			}

		} catch (Exception e)
		{
			System.out.println("Couldn't get user.");
		} finally
		{
			if (conn != null)
				conn.close();
		}

		return user;
	}

	public static List<Users> getUsers() throws SQLException
	{
		List<Users> list = new ArrayList<Users>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = DataSource.getConnection();
			ps = conn.prepareStatement("SELECT * FROM users");
			rs = ps.executeQuery();

			while (rs.next())
			{
				long id = rs.getLong("idusers");
				String first = rs.getString("first");

				String last = rs.getString("last");
				String email = rs.getString("email");
				String username = rs.getString("username");
				String password = rs.getString("password");

				list.add(new Users(id, first, last, email, username, password));
			}

		} catch (Exception e)
		{

		} finally
		{
			if (conn != null)
				conn.close();
		}

		return list;
	}

	public static Users insertUser(Users user) throws Exception
	{
		Connection conn = null;
		PreparedStatement ps = null;

		if (!userExists(user.getUsername()))
		{
			try
			{
				conn = DataSource.getConnection();
				String query = "INSERT INTO users (first, last, email, username, password) VALUES (?, ?, ?, ?, ?)";
				ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, user.getFirst());
				ps.setString(2, user.getLast());
				ps.setString(3, user.getEmail());
				ps.setString(4, user.getUsername());
				ps.setString(5, user.getPassword());

				int row = ps.executeUpdate();

				if (row == 0)
					throw new SQLException("Unable to insert user");

				try (ResultSet generatedKeys = ps.getGeneratedKeys())
				{
					if (generatedKeys.next())
						user.setIdusers(generatedKeys.getLong(1));
					else
						throw new SQLException("Unable to insert user, no ID obtained.");
				}

			} catch (Exception e)
			{
				System.out.println("Couldn't insert user");
			} finally
			{
				if (conn != null)
					conn.close();
			}
		} else
		{
			throw new Exception("Couldn't insert user");
		}

		return user;
	}

	public static boolean userExists(String username) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = DataSource.getConnection();
			String query = "SELECT * FROM users WHERE username = ?";
			ps = conn.prepareStatement(query);
			ps.setString(1, username);
			rs = ps.executeQuery();

			return rs.next();

		} catch (Exception e)
		{
			System.out.println("Couldn't get user");
		} finally
		{
			if (conn != null)
				conn.close();
		}

		return false;
	}

	public static Users updateUser(Users user) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;

		try
		{
			conn = DataSource.getConnection();
			String query = "UPDATE users SET first = ?, last = ?, email = ?, username = ?, password = ? WHERE idusers = ?";
			ps = conn.prepareStatement(query);
			ps.setString(1, user.getFirst());
			ps.setString(2, user.getLast());
			ps.setString(3, user.getEmail());
			ps.setString(4, user.getUsername());
			ps.setString(5, user.getPassword());
			ps.setLong(6, user.getIdusers());
			ps.executeUpdate();

		} catch (Exception e)
		{
			System.out.println("Couldn't update user");
		} finally
		{
			if (conn != null)
				conn.close();
		}

		return user;
	}

	public static void deleteUser(long id) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;

		try
		{
			conn = DataSource.getConnection();
			String query = "DELETE FROM users WHERE idusers = ?";
			ps = conn.prepareStatement(query);
			ps.setLong(1, id);
			ps.executeUpdate();

		} catch (Exception e)
		{
			System.out.println("Couldn't delete user");
		} finally
		{
			if (conn != null)
				conn.close();
		}
	}

	public static List<Einnahme> getEinnahmen() throws SQLException
	{
		List<Einnahme> list = new ArrayList<Einnahme>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = DataSource.getConnection();
			ps = conn.prepareStatement("SELECT * FROM einnahmen");
			rs = ps.executeQuery();

			while (rs.next())
			{
				long id = rs.getLong("id");
				double betrag = rs.getDouble("betrag");
				long idusers = rs.getLong("idusers");
				String datum = rs.getString("datum");
				String notiz = rs.getString("notiz");
				list.add(new Einnahme(id, betrag, idusers, datum, notiz));
			}

		} catch (Exception e)
		{

		} finally
		{
			if (conn != null)
				conn.close();
		}

		return list;
	}

	public static Einnahme getEinnahme(long id) throws SQLException
	{
		Einnahme einnahme = new Einnahme();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = DataSource.getConnection();
			ps = conn.prepareStatement("SELECT * FROM einnahmen WHERE id = ?");
			ps.setLong(1, id);
			rs = ps.executeQuery();

			while (rs.next())
			{
				einnahme.setId(rs.getLong("id"));
				einnahme.setBetrag(rs.getDouble("betrag"));
				einnahme.setIdusers(rs.getLong("idusers"));
				einnahme.setDatum(rs.getString("datum"));
				einnahme.setNotiz(rs.getString("notiz"));
			}

		} catch (Exception e)
		{

		} finally
		{
			if (conn != null)
				conn.close();
		}

		return einnahme;
	}

	public static Einnahme insertEinnahme(Einnahme einnahme) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;
		DecimalFormat numFormat = new DecimalFormat("#.00");
		double betrag = einnahme.getBetrag();
		einnahme.setBetrag(Double.valueOf(numFormat.format(betrag)));

		try
		{
			conn = DataSource.getConnection();
			String query = "INSERT INTO einnahmen (betrag, idusers, datum, notiz) VALUES (?, ?, now(), ?)";
			ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setDouble(1, einnahme.getBetrag());
			ps.setLong(2, einnahme.getIdusers());
			ps.setString(3, einnahme.getNotiz());

			System.out.println(einnahme.toString());

			int row = ps.executeUpdate();

			if (row == 0)
				throw new SQLException("Unable to insert einnahme");

			try (ResultSet generatedKeys = ps.getGeneratedKeys())
			{
				if (generatedKeys.next())
					einnahme.setId(generatedKeys.getLong(1));
				else
					throw new SQLException("Unable to insert einnahme, no ID obtained.");
			}

		} catch (Exception e)
		{
			System.out.println("Couldn't insert einnahme");
		} finally
		{
			if (conn != null)
				conn.close();
		}

		return einnahme;
	}

	public static Einnahme updateEinnahme(Einnahme einnahme) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;
		DecimalFormat numFormat = new DecimalFormat("#.00");
		double betrag = einnahme.getBetrag();
		einnahme.setBetrag(Double.valueOf(numFormat.format(betrag)));

		try
		{
			conn = DataSource.getConnection();
			String query = "UPDATE einnahmen SET betrag = ?, idusers = ?, datum = now(), notiz = ? WHERE id = ?";
			ps = conn.prepareStatement(query);
			ps.setDouble(1, einnahme.getBetrag());
			ps.setLong(2, einnahme.getIdusers());
			ps.setString(3, einnahme.getNotiz());
			ps.setLong(4, einnahme.getId());
			ps.executeUpdate();

		} catch (Exception e)
		{
			System.out.println("Couldn't update einnahme");
		} finally
		{
			if (conn != null)
				conn.close();
		}

		return einnahme;
	}

	public static void deleteEinnahme(long id) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;

		try
		{
			conn = DataSource.getConnection();
			String query = "DELETE FROM einnahmen WHERE id = ?";
			ps = conn.prepareStatement(query);
			ps.setLong(1, id);
			ps.executeUpdate();

		} catch (Exception e)
		{
			System.out.println("Couldn't delete einnahme");
		} finally
		{
			if (conn != null)
				conn.close();
		}
	}

	public static List<Ausgabe> getAusgaben() throws SQLException
	{
		List<Ausgabe> list = new ArrayList<Ausgabe>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = DataSource.getConnection();
			ps = conn.prepareStatement("SELECT * FROM ausgaben");
			rs = ps.executeQuery();

			while (rs.next())
			{
				long id = rs.getLong("id");
				double betrag = rs.getDouble("betrag");
				long idusers = rs.getLong("idusers");
				String datum = rs.getString("datum");
				String notiz = rs.getString("notiz");
				list.add(new Ausgabe(id, betrag, idusers, datum, notiz));
			}

		} catch (Exception e)
		{

		} finally
		{
			if (conn != null)
				conn.close();
		}

		return list;
	}

	public static Ausgabe getAusgabe(long id) throws SQLException
	{
		Ausgabe ausgabe = new Ausgabe();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = DataSource.getConnection();
			ps = conn.prepareStatement("SELECT * FROM ausgaben WHERE id = ?");
			ps.setLong(1, id);
			rs = ps.executeQuery();

			while (rs.next())
			{
				ausgabe.setId(rs.getLong("id"));
				ausgabe.setBetrag(rs.getDouble("betrag"));
				ausgabe.setIdusers(rs.getLong("idusers"));
				ausgabe.setDatum(rs.getString("datum"));
				ausgabe.setNotiz(rs.getString("notiz"));
			}

		} catch (Exception e)
		{

		} finally
		{
			if (conn != null)
				conn.close();
		}

		return ausgabe;
	}

	public static Ausgabe insertAusgabe(Ausgabe ausgabe) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;
		DecimalFormat numFormat = new DecimalFormat("#.00");
		double betrag = ausgabe.getBetrag();
		ausgabe.setBetrag(Double.valueOf(numFormat.format(betrag)));

		try
		{
			conn = DataSource.getConnection();
			String query = "INSERT INTO ausgaben (betrag, idusers, datum, notiz) VALUES (?, ?, now(), ?)";
			ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setDouble(1, ausgabe.getBetrag());
			ps.setLong(2, ausgabe.getIdusers());
			ps.setString(3, ausgabe.getNotiz());
			int row = ps.executeUpdate();

			if (row == 0)
				throw new SQLException("Unable to insert ausgabe");

			try (ResultSet generatedKeys = ps.getGeneratedKeys())
			{
				if (generatedKeys.next())
					ausgabe.setId(generatedKeys.getLong(1));
				else
					throw new SQLException("Unable to insert ausgabe, no ID obtained.");
			}

		} catch (Exception e)
		{
			System.out.println("Couldn't insert ausgabe");
		} finally
		{
			if (conn != null)
				conn.close();
		}

		return ausgabe;
	}

	public static EinnahmeAusgabe insertEinnahmeAusgabe(EinnahmeAusgabe einnahmeAusgabe) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;
		DecimalFormat numFormat = new DecimalFormat("#.00");
		double betrag = einnahmeAusgabe.getBetrag();
		einnahmeAusgabe.setBetrag(Double.valueOf(numFormat.format(betrag)));
		double betragDivided = Double.valueOf(numFormat.format(betrag/einnahmeAusgabe.getDebtor().size()));
		long user = getUserByToken(einnahmeAusgabe.getToken());

		System.out.println(TAG + einnahmeAusgabe.getDebtor().size());

		for (Username username : einnahmeAusgabe.getDebtor())
		{
			System.out.println(TAG + username);
			Ausgabe newAusgabe = new Ausgabe();
			newAusgabe.setBetrag(betragDivided);
			newAusgabe.setNotiz(einnahmeAusgabe.getNotiz());
			newAusgabe.setIdusers(getUserId(username.getUsername()));
			insertAusgabe(newAusgabe);
		}

		try
		{
			conn = DataSource.getConnection();
			String query = "INSERT INTO einnahmen (betrag, idusers, datum, notiz) VALUES (?, ?, now(), ?)";
			ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setDouble(1, einnahmeAusgabe.getBetrag());
			ps.setLong(2, user);
			ps.setString(3, einnahmeAusgabe.getNotiz());
			int row = ps.executeUpdate();

			if (row == 0)
				throw new SQLException("Unable to insert ausgabe");

			try (ResultSet generatedKeys = ps.getGeneratedKeys())
			{
				if (generatedKeys.next())
					einnahmeAusgabe.setId(generatedKeys.getLong(1));
				else
					throw new SQLException("Unable to insert ausgabe, no ID obtained.");
			}

		} catch (Exception e)
		{
			System.out.println("Couldn't insert einnahmeAusgabe");
		} finally
		{
			if (conn != null)
				conn.close();
		}

		return einnahmeAusgabe;
	}

	private static long getUserId(String username) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		long idUser = 0;

		try
		{
			conn = DataSource.getConnection();
			ps = conn.prepareStatement("SELECT idusers FROM users WHERE username = ?");
			ps.setString(1, username);
			rs = ps.executeQuery();

			while (rs.next())
			{
				idUser = rs.getLong("idusers");
			}

		} catch (Exception e)
		{
			System.out.println("Couldn't get user from token.");
		} finally
		{
			if (conn != null)
				conn.close();
		}

		return idUser;
	}

	private static long getUserByToken(String token) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		long idUser = 0;

		try
		{
			conn = DataSource.getConnection();
			ps = conn.prepareStatement("SELECT iduser FROM token WHERE token = ?");
			ps.setString(1, token);
			rs = ps.executeQuery();

			while (rs.next())
			{
				idUser = rs.getLong("iduser");
			}

		} catch (Exception e)
		{
			System.out.println("Couldn't get user from token.");
		} finally
		{
			if (conn != null)
				conn.close();
		}

		return idUser;
	}

	public static Ausgabe updateAusgabe(Ausgabe ausgabe) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;
		DecimalFormat numFormat = new DecimalFormat("#.00");
		double betrag = ausgabe.getBetrag();
		ausgabe.setBetrag(Double.valueOf(numFormat.format(betrag)));

		try
		{
			conn = DataSource.getConnection();
			String query = "UPDATE ausgaben SET betrag = ?, idusers = ?, datum = now(), notiz = ? WHERE id = ?";
			ps = conn.prepareStatement(query);
			ps.setDouble(1, ausgabe.getBetrag());
			ps.setLong(2, ausgabe.getIdusers());
			ps.setString(3, ausgabe.getNotiz());
			ps.setLong(4, ausgabe.getId());
			ps.executeUpdate();

		} catch (Exception e)
		{
			System.out.println("Couldn't update ausgabe");
		} finally
		{
			if (conn != null)
				conn.close();
		}

		return ausgabe;
	}

	public static void deleteAusgabe(long id) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;

		try
		{
			conn = DataSource.getConnection();
			String query = "DELETE FROM ausgaben WHERE id = ?";
			ps = conn.prepareStatement(query);
			ps.setLong(1, id);
			ps.executeUpdate();

		} catch (Exception e)
		{
			System.out.println("Couldn't delete ausgabe");
		} finally
		{
			if (conn != null)
				conn.close();
		}
	}

	public static Users findUserByUsername(String username) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Users user = new Users();

		try
		{
			conn = DataSource.getConnection();
			String query = "SELECT * FROM users WHERE username = ?";
			ps = conn.prepareStatement(query);
			ps.setString(1, username);
			rs = ps.executeQuery();

			while (rs.next())
			{
				user.setIdusers(rs.getLong("idusers"));
				user.setFirst(rs.getString("first"));
				user.setLast(rs.getString("last"));
				user.setEmail(rs.getString("email"));
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
			}

		} catch (Exception e)
		{
			System.out.println("Couldn't get user");
		} finally
		{
			if (conn != null)
				conn.close();
		}

		return user;
	}

	public static boolean isTokenSet(Users user) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = DataSource.getConnection();
			ps = conn.prepareStatement("SELECT * FROM token WHERE iduser = ?");
			ps.setLong(1, user.getIdusers());
			rs = ps.executeQuery();

			return rs.next();

		} catch (Exception e)
		{
			System.out.println("Couldn't get user");
		} finally
		{
			if (conn != null)
				conn.close();
		}
		return false;
	}

	public static void setTokenToUser(Users user, AuthenticationToken token) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;

		try
		{
			conn = DataSource.getConnection();

			if (isTokenSet(user))
			{
				ps = conn.prepareStatement("UPDATE token SET token = ?, datum = now() WHERE iduser = ?");
				ps.setString(1, token.getToken());
				ps.setLong(2, user.getIdusers());
			} else
			{
				ps = conn.prepareStatement("INSERT INTO token (iduser, token, datum) VALUES (?, ?, now())");
				ps.setLong(1, user.getIdusers());
				ps.setString(2, token.getToken());
			}

			ps.executeUpdate();

		} catch (Exception e)
		{
			System.out.println("Couldn't insert or update token");
		} finally
		{
			if (conn != null)
				conn.close();
		}
	}

	public static boolean validateToken(AuthenticationToken token) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try
		{
			conn = DataSource.getConnection();
			System.out.println(token.getToken());
			ps = conn.prepareStatement("SELECT * FROM token WHERE token = ?");
			ps.setString(1, token.getToken());
			rs = ps.executeQuery();

			return rs.next();

		} catch (Exception e)
		{
			System.out.println("Couldn't get token");
		} finally
		{
			if (conn != null)
				conn.close();
		}
		return false;
	}

}
