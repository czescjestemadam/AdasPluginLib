package czescjestemadas.adaspluginlib.database;

import java.util.List;
import java.util.Map;

public interface Database
{
	boolean connect();
	void disconnect();

	void registerTable(Class<? extends DBModel> cls);

	<T extends DBModel> List<T> select(Query<T> query);

	boolean insert(DBModel model);
	int insert(List<? extends DBModel> models);

	int update(Query<? extends DBModel> query, Map<String, Object> values);
	int delete(Query<? extends DBModel> query);

	boolean isConnected();
	Type getType();

	enum Type
	{
		SQL,
		SQLITE
	}
}
