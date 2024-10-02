package czescjestemadas.adaspluginlib.util;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;

public class CooldownTracker<T>
{
	private final Map<T, Instant> lastUpdates = new HashMap<>();
	private final Duration cooldown;

	public CooldownTracker(Duration cooldown)
	{
		this.cooldown = cooldown;
	}

	public Map<T, Instant> getLastUpdates()
	{
		return lastUpdates;
	}

	public Instant getLastUpdate(T key)
	{
		return lastUpdates.getOrDefault(key, Instant.MIN);
	}

	public long getRemainingCooldown(T key, TemporalUnit unit)
	{
		return Instant.now().until(getLastUpdate(key).plus(cooldown), unit);
	}

	public Duration getCooldown()
	{
		return cooldown;
	}

	public void update(T key)
	{
		lastUpdates.put(key, Instant.now());
	}

	public boolean hasPassed(T key)
	{
		return Instant.now().isAfter(getLastUpdate(key).plus(cooldown));
	}


	@Override
	public String toString()
	{
		return "CooldownTracker{" +
				"lastUpdates=" + lastUpdates +
				", cooldown=" + cooldown +
				'}';
	}
}
