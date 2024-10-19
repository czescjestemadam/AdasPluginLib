package czescjestemadas.adaspluginlib.config;

import czescjestemadas.adaspluginlib.util.EnumUtil;
import czescjestemadas.adaspluginlib.util.TypeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IConfigSerializer<T>
{
	Object serialize(IConfig config, Field field, Object value);

	T deserialize(IConfig config, Field field, Object object);


	IConfigSerializer<Object> IDENTITY = new IConfigSerializer<>()
	{
		@Override
		public Object serialize(IConfig config, Field field, Object value)
		{
			return value;
		}

		@Override
		public Object deserialize(IConfig config, Field field, Object object)
		{
			return object;
		}
	};

	IConfigSerializer<Component> MINI_MESSAGE = new IConfigSerializer<>()
	{
		@Override
		public Object serialize(IConfig config, Field field, Object value)
		{
			return MiniMessage.miniMessage().serialize((Component)value);
		}

		@Override
		public Component deserialize(IConfig config, Field field, Object object)
		{
			return MiniMessage.miniMessage().deserialize(String.valueOf(object));
		}
	};

	IConfigSerializer<Material> MATERIAL = new IConfigSerializer<>()
	{
		@Override
		public Object serialize(IConfig config, Field field, Object value)
		{
			return value.toString();
		}

		@Override
		public Material deserialize(IConfig config, Field field, Object object)
		{
			return Material.matchMaterial(String.valueOf(object));
		}
	};

	IConfigSerializer<Sound> SOUND = new IConfigSerializer<>()
	{
		@Override
		public Object serialize(IConfig config, Field field, Object value)
		{
			return value.toString();
		}

		@Override
		public Sound deserialize(IConfig config, Field field, Object object)
		{
			return EnumUtil.valueOf(Sound.class, String.valueOf(object));
		}
	};

	IConfigSerializer<PotionType> POTION_TYPE = new IConfigSerializer<>()
	{
		@Override
		public Object serialize(IConfig config, Field field, Object value)
		{
			return value.toString();
		}

		@Override
		public PotionType deserialize(IConfig config, Field field, Object object)
		{
			return EnumUtil.valueOf(PotionType.class, String.valueOf(object));
		}
	};

	IConfigSerializer<PotionEffectType> POTION_EFFECT_TYPE = new IConfigSerializer<>()
	{
		@Override
		public Object serialize(IConfig config, Field field, Object value)
		{
			return ((PotionEffectType)value).getName();
		}

		@Override
		public PotionEffectType deserialize(IConfig config, Field field, Object object)
		{
			return PotionEffectType.getByName(String.valueOf(object));
		}
	};

	IConfigSerializer<Particle> PARTICLE = new IConfigSerializer<>()
	{
		@Override
		public Object serialize(IConfig config, Field field, Object value)
		{
			return value.toString();
		}

		@Override
		public Particle deserialize(IConfig config, Field field, Object object)
		{
			return EnumUtil.valueOf(Particle.class, String.valueOf(object));
		}
	};

	IConfigSerializer<List> LIST = new IConfigSerializer<>()

	{
		@Override
		public Object serialize(IConfig config, Field field, Object value)
		{
			final IConfigSerializer<?> serializer = config.getSerializer(TypeUtil.getParamType(field));
			return ((List<?>)value).stream().map(o -> serializer.serialize(config, field, o)).toList();
		}

		@Override
		public List deserialize(IConfig config, Field field, Object object)
		{
			final IConfigSerializer<?> serializer = config.getSerializer(TypeUtil.getParamType(field));
			return ((List<?>)object).stream().map(o -> serializer.deserialize(config, field, o)).toList();
		}
	};

	IConfigSerializer<Optional> OPTIONAL = new IConfigSerializer<>()
	{
		@Override
		public Object serialize(IConfig config, Field field, Object value)
		{
			final Optional optional = (Optional)value;
			if (optional.isEmpty())
				return null;
			return config.getSerializer(TypeUtil.getParamType(field)).serialize(config, field, optional.get());
		}

		@Override
		public Optional deserialize(IConfig config, Field field, Object object)
		{
			return Optional.ofNullable(
					config.getSerializer(TypeUtil.getParamType(field)).deserialize(config, field, object)
			);
		}
	};

	IConfigSerializer<NamedTextColor> NAMED_TEXT_COLOR = new IConfigSerializer<>()
	{
		@Override
		public Object serialize(IConfig config, Field field, Object value)
		{
			return value.toString();
		}

		@Override
		public NamedTextColor deserialize(IConfig config, Field field, Object object)
		{
			return NamedTextColor.NAMES.value(String.valueOf(object));
		}
	};

	IConfigSerializer<Map> MAP = new IConfigSerializer<>()
	{
		@Override
		public Object serialize(IConfig config, Field field, Object value)
		{
			final MemoryConfiguration cfg = new MemoryConfiguration();

			final Map<String, ?> map = (Map<String, ?>)value;
			for (Map.Entry<String, ?> entry : map.entrySet())
			{
				final Object v = entry.getValue();
				cfg.set(entry.getKey(), config.getSerializer(v.getClass()).serialize(config, field, v));
			}

			return cfg;
		}

		@Override
		public Map deserialize(IConfig config, Field field, Object object)
		{
			final Map<String, Object> map = new HashMap<>();

			ConfigurationSection cfg = (ConfigurationSection)object;
			for (String key : cfg.getKeys(false))
				map.put(key, config.getSerializer(TypeUtil.getParamType(field, 1)).deserialize(config, field, cfg.get(key)));

			return map;
		}
	};
}
