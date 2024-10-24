package czescjestemadas.adaspluginlib.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Function;

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

	public static <T, R> R safeGet(T obj, Function<T, R> getter)
	{
		return safeGet(obj, getter, null);
	}

	public static <T, R> R safeGet(T obj, Function<T, R> getter, R dflt)
	{
		return obj == null ? dflt : getter.apply(obj);
	}
}
