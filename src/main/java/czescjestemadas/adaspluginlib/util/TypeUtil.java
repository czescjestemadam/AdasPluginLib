package czescjestemadas.adaspluginlib.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeUtil
{
	public static Type getParamType(Field field)
	{
		return getParamType(field, 0);
	}

	public static Type getParamType(Field field, int idx)
	{
		return ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[idx];
	}
}
