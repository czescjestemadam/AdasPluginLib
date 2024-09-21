package czescjestemadas.adaspluginlib.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class Config
{
	private final Plugin plugin;
	private final String filename;

	protected Config(Plugin plugin, String filename)
	{
		this.plugin = plugin;
		this.filename = filename;
	}

	public void load()
	{
		final YamlConfiguration config = YamlConfiguration.loadConfiguration(getFile());

		for (Field field : getConfigFields())
		{
			final String path = field.getAnnotation(Path.class).value();

			if (!config.contains(path))
			{
				plugin.getSLF4JLogger().warn("missing {} in {}, using default values", path, filename);
				continue;
			}

			try
			{
				final Object value = config.get(path);
				final ValueLoader valueLoader = field.getAnnotation(ValueLoader.class);
				field.set(this, valueLoader == null ? value : valueLoader.value().load(value));
			}
			catch (IllegalAccessException e)
			{
				plugin.getSLF4JLogger().error("cannot load {} in {}", path, filename, e);
			}
		}
	}

	public void save()
	{
		final YamlConfiguration config = new YamlConfiguration();

		for (Field field : getConfigFields())
		{
			final String path = field.getAnnotation(Path.class).value();

			try
			{
				final Object value = field.get(this);
				final ValueLoader valueLoader = field.getAnnotation(ValueLoader.class);
				config.set(path, valueLoader == null ? value : valueLoader.value().save(value));

				final Comment comment = field.getAnnotation(Comment.class);
				if (comment != null)
					config.setComments(path, List.of(comment.value()));
			}
			catch (IllegalAccessException e)
			{
				plugin.getSLF4JLogger().error("cannot save {} in {}", path, filename, e);
			}
		}

		try
		{
			config.save(getFile());
		}
		catch (IOException e)
		{
			plugin.getSLF4JLogger().error("cannot save {}", filename);
		}
	}


	private File getFile()
	{
		return new File(plugin.getDataFolder(), filename);
	}

	private List<Field> getConfigFields()
	{
		final List<Field> fields = new ArrayList<>();

		for (Field field : getClass().getFields())
		{
			if (field.getAnnotation(Path.class) != null)
				fields.add(field);
		}

		return fields;
	}
}
