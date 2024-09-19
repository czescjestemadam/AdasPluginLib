package czescjestemadas.adaspluginlib.config;

import java.util.List;

public final class ConfigManager
{
	private final List<Config> configs;

	public ConfigManager(Config... configs)
	{
		this.configs = List.of(configs);
	}

	public void load()
	{
		for (Config config : configs)
			config.load();
	}

	public void save()
	{
		for (Config config : configs)
			config.save();
	}

	public <T extends Config> T get(Class<T> cls)
	{
		for (Config config : configs)
		{
			if (cls.isInstance(config))
				return cls.cast(config);
		}

		return null;
	}
}
