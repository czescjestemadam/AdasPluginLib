package czescjestemadas.adaspluginlib.database;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public interface DBModel extends Cloneable
{
	default List<Field> getFields()
	{
		return getFields(getClass());
	}

	default Field getField(String name)
	{
		for (Field field : getFields())
		{
			if (field.getAnnotation(DBField.class).name().equals(name))
				return field;
		}

		return null;
	}

	default String getTable()
	{
		return getClass().getAnnotation(DBTable.class).value();
	}


	static List<Field> getFields(Class<? extends DBModel> cls)
	{
		final List<Field> fields = new ArrayList<>();

		for (Field field : cls.getFields())
		{
			if (field.getAnnotation(DBField.class) != null)
				fields.add(field);
		}

		return fields;
	}
}
