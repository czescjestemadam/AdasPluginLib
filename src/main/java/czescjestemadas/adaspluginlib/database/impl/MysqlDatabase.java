package czescjestemadas.adaspluginlib.database.impl;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlDatabase extends SqlDatabase
{
	private final String address;
	private final String user;
	private final String password;

	public MysqlDatabase(String address, String user, String password)
	{
		this.address = address;
		this.user = user;
		this.password = password;
	}

	@Override
	public boolean connect()
	{
		try
		{
			connection = DriverManager.getConnection("jdbc:mysql:" + address, user, password);
			return true;
		}
		catch (SQLException e)
		{
			return false;
		}
	}

	@Override
	public Type getType()
	{
		return Type.SQL;
	}

	@Override
	public String toString()
	{
		return "MysqlDatabase{" +
				"address='" + address + '\'' +
				", user='" + user + '\'' +
				", connection=" + connection +
				'}';
	}
}
