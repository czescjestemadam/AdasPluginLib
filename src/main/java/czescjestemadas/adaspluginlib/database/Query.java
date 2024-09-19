package czescjestemadas.adaspluginlib.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Query<T extends DBModel>
{
	private final Class<T> modelClass;
	private final List<Entry> entries = new ArrayList<>();

	public Query(Class<T> modelClass)
	{
		this.modelClass = modelClass;
	}

	public Class<T> getModelClass()
	{
		return modelClass;
	}

	public List<Entry> getEntries()
	{
		return entries;
	}

	public Query<T> withEntry(Entry entry)
	{
		entries.add(entry);
		return this;
	}

	public Query<T> withEquals(String fieldName, Object value)
	{
		return withEntry(new Entry(fieldName, CompareMethod.EQUALS, value));
	}

	public Query<T> withNotEquals(String fieldName, Object value)
	{
		return withEntry(new Entry(fieldName, CompareMethod.NOT_EQUALS, value));
	}

	public Query<T> withLess(String fieldName, Object value)
	{
		return withEntry(new Entry(fieldName, CompareMethod.LESS, value));
	}

	public Query<T> withLessEquals(String fieldName, Object value)
	{
		return withEntry(new Entry(fieldName, CompareMethod.LESS_EQUALS, value));
	}

	public Query<T> withGreater(String fieldName, Object value)
	{
		return withEntry(new Entry(fieldName, CompareMethod.GREATER, value));
	}

	public Query<T> withGreaterEquals(String fieldName, Object value)
	{
		return withEntry(new Entry(fieldName, CompareMethod.GREATER_EQUALS, value));
	}


	public PreparedStatement buildSqlQuery(String prefix, Connection connection) throws SQLException
	{
		final String sql = entries.stream().map(entry -> entry.fieldName + entry.compareMethod.sql + "?").collect(Collectors.joining(", "));
		final PreparedStatement statement = connection.prepareStatement(prefix + " WHERE " + sql);

		for (int i = 0; i < entries.size(); i++)
			statement.setObject(i + 1, entries.get(i).value);

		return statement;
	}


	public record Entry(String fieldName, CompareMethod compareMethod, Object value)
	{
	}

	public enum CompareMethod
	{
		EQUALS("="),
		NOT_EQUALS("!="),
		LESS("<"),
		LESS_EQUALS("<="),
		GREATER(">"),
		GREATER_EQUALS(">="),
		;

		private final String sql;

		CompareMethod(String sql)
		{
			this.sql = sql;
		}
	}
}
