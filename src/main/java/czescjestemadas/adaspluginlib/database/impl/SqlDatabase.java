package czescjestemadas.adaspluginlib.database.impl;

import czescjestemadas.adaspluginlib.database.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SqlDatabase implements Database
{
	protected Connection connection;

	@Override
	public void disconnect()
	{
		if (connection == null)
			return;

		try
		{
			connection.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		connection = null;
	}

	@Override
	public void registerTable(Class<? extends DBModel> cls)
	{
		checkConnection();

		final String table = cls.getAnnotation(DBTable.class).value();

		try (final Statement statement = connection.createStatement())
		{
			final String sql = "CREATE TABLE IF NOT EXISTS " + table;
			final String fields = DBModel.getFields(cls).stream().map(field -> {
						final DBField dbField = field.getAnnotation(DBField.class);
						return dbField.name() + " " + dbField.type();
					})
					.collect(Collectors.joining(", ", "(", ")"));

			statement.execute(sql + fields);
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T extends DBModel> List<T> select(Query<T> query)
	{
		checkConnection();

		final Class<T> modelClass = query.getModelClass();
		final List<Field> fields = DBModel.getFields(modelClass);

		final List<T> ret = new ArrayList<>();

		try (final PreparedStatement statement = query.buildSqlQuery("SELECT * FROM " + modelClass.getAnnotation(DBTable.class).value(), connection))
		{
			try (final ResultSet resultSet = statement.executeQuery())
			{
				while (resultSet.next())
				{
					try
					{
						final T model = modelClass.getConstructor().newInstance();

						for (Field field : fields)
							field.set(model, resultSet.getObject(field.getAnnotation(DBField.class).name()));

						ret.add(model);
					}
					catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e)
					{
						throw new RuntimeException(e);
					}
				}
			}
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}

		return ret;
	}

	@Override
	public boolean insert(DBModel model)
	{
		checkConnection();

		final List<Field> fields = model.getFields();

		final String prep = fields.stream().map(field -> "?").collect(Collectors.joining(",", "(", ")"));
		try (final PreparedStatement statement = connection.prepareStatement("INSERT INTO " + model.getTable() + " VALUES " + prep))
		{
			for (int i = 0; i < fields.size(); i++)
				statement.setObject(i + 1, fields.get(i).get(model));

			return statement.executeUpdate() > 0;
		}
		catch (SQLException | IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public int insert(List<? extends DBModel> models)
	{
		checkConnection();

		int insertCount = 0;

		for (DBModel model : models)
		{
			if (insert(model))
				insertCount++;
		}

		return insertCount;
	}

	@Override
	public int update(Query<? extends DBModel> query, Map<String, Object> values)
	{
		checkConnection();

		if (values.isEmpty())
			return 0;

		final List<Map.Entry<String, Object>> valueEntries = new ArrayList<>(values.entrySet());

		final String sql = "UPDATE " + query.getModelClass().getAnnotation(DBTable.class).value() +
				" SET " + valueEntries.stream().map(e -> e.getKey() + " = ?").collect(Collectors.joining(", "));

		try (final PreparedStatement statement = query.buildSqlQuery(sql, connection))
		{
			for (int i = 0; i < valueEntries.size(); i++)
				statement.setObject(i + 1, valueEntries.get(i));

			return statement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public int delete(Query<? extends DBModel> query)
	{
		checkConnection();

		try (final PreparedStatement statement = query.buildSqlQuery("DELETE FROM " + query.getModelClass().getAnnotation(DBTable.class).value(), connection))
		{
			return statement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isConnected()
	{
		if (connection == null)
			return false;

		try
		{
			return connection.isValid(5000);
		}
		catch (SQLException e)
		{
			return false;
		}
	}

	protected void checkConnection()
	{
		if (!isConnected())
			throw new IllegalStateException(this + " is not connected");
	}
}
