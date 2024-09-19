package czescjestemadas.adaspluginlib.database.impl;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteDatabase extends SqlDatabase
{
	private final File file;

	public SqliteDatabase(File file)
	{
		this.file = file;
	}

	@Override
	public boolean connect()
	{
		try
		{
			connection = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());
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
		return Type.SQLITE;
	}

	@Override
	public String toString()
	{
		return "SqliteDatabase{" +
				"file=" + file +
				", connection=" + connection +
				'}';
	}
}
