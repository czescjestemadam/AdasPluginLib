package czescjestemadas.adaspluginlib.util;

public abstract class StrUtil
{
	public static int countOccurrences(String str, String substr)
	{
		int count = 0;

		int idx = 0;
		while (true)
		{
			idx = str.indexOf(substr, idx);
			if (idx == -1)
				break;

			count++;
			idx += substr.length();
		}

		return count;
	}
}
