package czescjestemadas.adaspluginlib.config;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IConfig
{
	private final Plugin plugin;
	private final String filename;
	private final Map<Class<?>, IConfigSerializer<?>> serializers = new HashMap<>();

	protected IConfig(Plugin plugin, String filename)
	{
		this.plugin = plugin;
		this.filename = filename;
		addSerializer(Component.class, new IConfigSerializer.MiniMessage());
		addSerializer(Material.class, new IConfigSerializer.Material());
		addSerializer(Sound.class , new IConfigSerializer.Sound());
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
				final IConfigSerializer<?> serializer = serializers.get(field.getType());
				field.set(this, serializer == null ? value : serializer.deserialize(value));
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
				final IConfigSerializer<?> serializer = serializers.get(field.getType());
				config.set(path, serializer == null ? value : serializer.serialize(value));

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

	protected <T> void addSerializer(Class<T> cls, IConfigSerializer<T> serializer)
	{
		serializers.put(cls, serializer);
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
