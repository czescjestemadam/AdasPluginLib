package czescjestemadas.adaspluginlib.config;

import java.util.List;

public final class ConfigManager
{
	private final List<IConfig> configs;

	public ConfigManager(IConfig... configs)
	{
		this.configs = List.of(configs);
	}

	public void load()
	{
		for (IConfig config : configs)
			config.load();
	}

	public void save()
	{
		for (IConfig config : configs)
			config.save();
	}

	public <T extends IConfig> T get(Class<T> cls)
	{
		for (IConfig config : configs)
		{
			if (cls.isInstance(config))
				return cls.cast(config);
		}

		return null;
	}
}
