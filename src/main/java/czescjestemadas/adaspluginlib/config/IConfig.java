package czescjestemadas.adaspluginlib.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

public abstract class IConfig
{
	private final Plugin plugin;
	private final String filename;
	private final Map<Type, IConfigSerializer<?>> serializers = new HashMap<>();

	protected IConfig(Plugin plugin, String filename)
	{
		this.plugin = plugin;
		this.filename = filename;
		addSerializer(Component.class, IConfigSerializer.MINI_MESSAGE);
		addSerializer(Material.class, IConfigSerializer.MATERIAL);
		addSerializer(Sound.class , IConfigSerializer.SOUND);
		addSerializer(PotionType.class, IConfigSerializer.POTION_TYPE);
		addSerializer(PotionEffectType.class, IConfigSerializer.POTION_EFFECT_TYPE);
		addSerializer(Particle.class, IConfigSerializer.PARTICLE);
		addSerializer(List.class, IConfigSerializer.LIST);
		addSerializer(Optional.class, IConfigSerializer.OPTIONAL);
		addSerializer(NamedTextColor.class, IConfigSerializer.NAMED_TEXT_COLOR);
		addSerializer(Map.class, IConfigSerializer.MAP);
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
				field.set(this, getSerializer(field.getType()).deserialize(this, field, value));
			}
			catch (IllegalAccessException e)
			{
				plugin.getSLF4JLogger().error("cannot load {} in {}", path, filename, e);
			}
		}
	}

	public void save()
	{
		save(false);
	}

	public void save(boolean dflt)
	{
		final YamlConfiguration config = dflt ? YamlConfiguration.loadConfiguration(getFile()) : new YamlConfiguration();

		for (Field field : getConfigFields())
		{
			final String path = field.getAnnotation(Path.class).value();

			if (dflt && config.contains(path))
				continue;

			try
			{
				final Object value = field.get(this);
				config.set(path, getSerializer(field.getType()).serialize(this, field, value));

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
		catch (IOException | YAMLException e)
		{
			plugin.getSLF4JLogger().error("cannot save {}", filename, e);
		}
	}

	public String getFilename()
	{
		return filename;
	}

	protected IConfigSerializer<?> getSerializer(Type type)
	{
		return serializers.getOrDefault(type, IConfigSerializer.IDENTITY);
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
