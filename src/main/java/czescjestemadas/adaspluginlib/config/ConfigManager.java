package czescjestemadas.adaspluginlib.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager
{
	private final Map<Class<? extends IConfig>, IConfig> configs = new HashMap<>();

	public ConfigManager(IConfig... configs)
	{
		for (IConfig config : configs)
			this.configs.put(config.getClass(), config);
	}

	public void load()
	{
		for (IConfig config : configs.values())
			config.load();
	}

	public void save()
	{
		for (IConfig config : configs.values())
			config.save();
	}

	public Collection<? extends IConfig> getConfigs()
	{
		return configs.values();
	}

	public <T extends IConfig> T get(Class<T> cls)
	{
		final IConfig config = configs.get(cls);
		return config == null ? null : cls.cast(config);
	}
}
