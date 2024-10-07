package czescjestemadas.adaspluginlib.util;

public abstract class EnumUtil
{
	public static <T extends Enum<T>> T valueOf(Class<T> enumCls, String name)
	{
		return valueOf(enumCls, name, null);
	}

	public static <T extends Enum<T>> T valueOf(Class<T> enumCls, String name, T dflt)
	{
		try
		{
			return Enum.valueOf(enumCls, name);
		}
		catch (IllegalArgumentException e)
		{
			return dflt;
		}
	}
}
