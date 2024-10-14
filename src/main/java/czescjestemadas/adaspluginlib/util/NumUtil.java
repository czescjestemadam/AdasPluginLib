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

	public static int parseIntOr(String str, int dflt)
	{
		try
		{
			return Integer.parseInt(str);
		}
		catch (NumberFormatException e)
		{
			return dflt;
		}
	}

	public static double parseDoubleOr(String str, double dflt)
	{
		try
		{
			return Double.parseDouble(str);
		}
		catch (NumberFormatException e)
		{
			return dflt;
		}
	}
}
