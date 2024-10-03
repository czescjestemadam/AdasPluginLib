package czescjestemadas.adaspluginlib.util;

import java.util.Random;

public class ChanceCalculator
{
	private final Random random = new Random();

	public boolean calculate(int percent)
	{
		return calculate(percent / 100.0);
	}

	public boolean calculate(double percent)
	{
		return random.nextFloat() < percent;
	}
}
