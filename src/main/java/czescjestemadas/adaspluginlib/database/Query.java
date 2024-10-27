package czescjestemadas.adaspluginlib.database;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Query<T extends DBModel>
{
	private final Class<T> modelClass;
	private final List<Entry> entries = new ArrayList<>();
	private String orderBy;
	private int limit = 0;

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

	public PreparedStatement buildSqlQuery(String prefix, Connection connection) throws SQLException
	{
		final StringBuilder sql = new StringBuilder(prefix);

		if (!entries.isEmpty())
			sql.append(" WHERE ");

		for (int i = 0; i < entries.size(); i++)
		{
			final Entry entry = entries.get(i);
			final EntryJoinMethod joinMethod = entry.joinMethod == null ? EntryJoinMethod.AND : entry.joinMethod;

			if (i > 0)
				sql.append(' ').append(joinMethod).append(' ');

			sql.append(entry.fieldName).append(entry.compareMethod.sql).append("?");
		}

		if (orderBy != null)
			sql.append(" ORDER BY ").append(orderBy);

		if (limit > 0)
			sql.append(" LIMIT ").append(limit);

		final String sqlString = sql.toString();

		try
		{
			final PreparedStatement statement = connection.prepareStatement(sqlString);

			for (int i = 0; i < entries.size(); i++)
				statement.setObject(i + 1, entries.get(i).value);

			return statement;
		}
		catch (SQLException e)
		{
			JavaPlugin.getProvidingPlugin(modelClass).getSLF4JLogger().error("sql: {}", sqlString);
			throw e;
		}
	}


	public record Entry(EntryJoinMethod joinMethod, String fieldName, CompareMethod compareMethod, Object value)
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

	public enum EntryJoinMethod
	{
		AND,
		OR
	}

	public static class Builder<R extends DBModel>
	{
		private final Query<R> query;

		public Builder(Class<R> modelClass)
		{
			this.query = new Query<>(modelClass);
		}

		public Builder<R> equals(String fieldName, Object value)
		{
			query.withEntry(new Entry(null, fieldName, CompareMethod.EQUALS, value));
			return this;
		}

		public Builder<R> notEquals(String fieldName, Object value)
		{
			query.withEntry(new Entry(null, fieldName, CompareMethod.NOT_EQUALS, value));
			return this;
		}

		public Builder<R> less(String fieldName, Object value)
		{
			query.withEntry(new Entry(null, fieldName, CompareMethod.LESS, value));
			return this;
		}

		public Builder<R> lessEquals(String fieldName, Object value)
		{
			query.withEntry(new Entry(null, fieldName, CompareMethod.LESS_EQUALS, value));
			return this;
		}

		public Builder<R> greater(String fieldName, Object value)
		{
			query.withEntry(new Entry(null, fieldName, CompareMethod.GREATER, value));
			return this;
		}

		public Builder<R> greaterEquals(String fieldName, Object value)
		{
			query.withEntry(new Entry(null, fieldName, CompareMethod.GREATER_EQUALS, value));
			return this;
		}


		public Builder<R> andEquals(String fieldName, Object value)
		{
			query.withEntry(new Entry(EntryJoinMethod.AND, fieldName, CompareMethod.EQUALS, value));
			return this;
		}

		public Builder<R> andNotEquals(String fieldName, Object value)
		{
			query.withEntry(new Entry(EntryJoinMethod.AND, fieldName, CompareMethod.NOT_EQUALS, value));
			return this;
		}

		public Builder<R> andLess(String fieldName, Object value)
		{
			query.withEntry(new Entry(EntryJoinMethod.AND, fieldName, CompareMethod.LESS, value));
			return this;
		}

		public Builder<R> andLessEquals(String fieldName, Object value)
		{
			query.withEntry(new Entry(EntryJoinMethod.AND, fieldName, CompareMethod.LESS_EQUALS, value));
			return this;
		}

		public Builder<R> andGreater(String fieldName, Object value)
		{
			query.withEntry(new Entry(EntryJoinMethod.AND, fieldName, CompareMethod.GREATER, value));
			return this;
		}

		public Builder<R> andGreaterEquals(String fieldName, Object value)
		{
			query.withEntry(new Entry(EntryJoinMethod.AND, fieldName, CompareMethod.GREATER_EQUALS, value));
			return this;
		}


		public Builder<R> orEquals(String fieldName, Object value)
		{
			query.withEntry(new Entry(EntryJoinMethod.OR, fieldName, CompareMethod.EQUALS, value));
			return this;
		}

		public Builder<R> orNotEquals(String fieldName, Object value)
		{
			query.withEntry(new Entry(EntryJoinMethod.OR, fieldName, CompareMethod.NOT_EQUALS, value));
			return this;
		}

		public Builder<R> orLess(String fieldName, Object value)
		{
			query.withEntry(new Entry(EntryJoinMethod.OR, fieldName, CompareMethod.LESS, value));
			return this;
		}

		public Builder<R> orLessEquals(String fieldName, Object value)
		{
			query.withEntry(new Entry(EntryJoinMethod.OR, fieldName, CompareMethod.LESS_EQUALS, value));
			return this;
		}

		public Builder<R> orGreater(String fieldName, Object value)
		{
			query.withEntry(new Entry(EntryJoinMethod.OR, fieldName, CompareMethod.GREATER, value));
			return this;
		}

		public Builder<R> orGreaterEquals(String fieldName, Object value)
		{
			query.withEntry(new Entry(EntryJoinMethod.OR, fieldName, CompareMethod.GREATER_EQUALS, value));
			return this;
		}


		public Builder<R> orderBy(String column)
		{
			query.orderBy = column;
			return this;
		}

		public Builder<R> limit(int limit)
		{
			query.limit = limit;
			return this;
		}

		public Query<R> build()
		{
			return query;
		}
	}
}
