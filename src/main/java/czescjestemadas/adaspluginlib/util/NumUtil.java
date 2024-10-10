package czescjestemadas.adaspluginlib.util;

public abstract class NumUtil
{
	public static <T extends Comparable<T>> boolean isInRange(T val, T min, T max)
	{
		return val.compareTo(min) <= 0 && 0 <= val.compareTo(max);
	}

	public static <T extends Comparable<T>> T clamp(T val, T min, T max)
	{
		return val.compareTo(min) < 0 ? min : val.compareTo(max) > 0 ? max : val;
	}
}
