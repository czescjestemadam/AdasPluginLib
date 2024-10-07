package czescjestemadas.adaspluginlib.config;

import net.kyori.adventure.text.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface IConfigSerializer<T>
{
	IConfigSerializer.Identity IDENTITY = new IConfigSerializer.Identity();
	IConfigSerializer.MiniMessage MINI_MESSAGE = new IConfigSerializer.MiniMessage();
	IConfigSerializer.Material MATERIAL = new IConfigSerializer.Material();
	IConfigSerializer.Sound SOUND = new IConfigSerializer.Sound();
	IConfigSerializer.PotionType POTION_TYPE = new IConfigSerializer.PotionType();
	IConfigSerializer.Particle PARTICLE = new IConfigSerializer.Particle();
	IConfigSerializer.List LIST = new IConfigSerializer.List();
	IConfigSerializer.Optional OPTIONAL = new IConfigSerializer.Optional();


	Object serialize(IConfig config, Field field, Object value);

	T deserialize(IConfig config, Field field, Object object);


	class Identity implements IConfigSerializer<Object>
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
	}

	class MiniMessage implements IConfigSerializer<Component>
	{
		@Override
		public Object serialize(IConfig config, Field field, Object value)
		{
			return net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().serialize((Component)value);
		}

		@Override
		public Component deserialize(IConfig config, Field field, Object object)
		{
			return net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(String.valueOf(object));
		}
	}

	class Material implements IConfigSerializer<org.bukkit.Material>
	{
		@Override
		public Object serialize(IConfig config, Field field, Object value)
		{
			return value.toString();
		}

		@Override
		public org.bukkit.Material deserialize(IConfig config, Field field, Object object)
		{
			return org.bukkit.Material.getMaterial(String.valueOf(object));
		}
	}

	class Sound implements IConfigSerializer<org.bukkit.Sound>
	{
		@Override
		public Object serialize(IConfig config, Field field, Object value)
		{
			return value.toString();
		}

		@Override
		public org.bukkit.Sound deserialize(IConfig config, Field field, Object object)
		{
			try
			{
				return org.bukkit.Sound.valueOf(String.valueOf(object));
			}
			catch (IllegalArgumentException e)
			{
				return null;
			}
		}
	}

	class PotionType implements IConfigSerializer<org.bukkit.potion.PotionType>
	{
		@Override
		public Object serialize(IConfig config, Field field, Object value)
		{
			return value.toString();
		}

		@Override
		public org.bukkit.potion.PotionType deserialize(IConfig config, Field field, Object object)
		{
			try
			{
				return org.bukkit.potion.PotionType.valueOf(String.valueOf(object));
			}
			catch (IllegalArgumentException e)
			{
				return null;
			}
		}
	}

	class Particle implements IConfigSerializer<org.bukkit.Particle>
	{
		@Override
		public Object serialize(IConfig config, Field field, Object value)
		{
			return value.toString();
		}

		@Override
		public org.bukkit.Particle deserialize(IConfig config, Field field, Object object)
		{
			try
			{
				return org.bukkit.Particle.valueOf(String.valueOf(object));
			}
			catch (IllegalArgumentException e)
			{
				return null;
			}
		}
	}

	class List implements IConfigSerializer<java.util.List>
	{
		@Override
		public Object serialize(IConfig config, Field field, Object value)
		{
			final IConfigSerializer<?> serializer = config.getSerializer(IConfigSerializer.getParamType(field));
			return ((java.util.List<?>)value).stream().map(o -> serializer.serialize(config, field, o)).toList();
		}

		@Override
		public java.util.List deserialize(IConfig config, Field field, Object object)
		{
			final IConfigSerializer<?> serializer = config.getSerializer(IConfigSerializer.getParamType(field));
			return ((java.util.List<?>)object).stream().map(o -> serializer.deserialize(config, field, o)).toList();
		}
	}

	class Optional implements IConfigSerializer<java.util.Optional>
	{
		@Override
		public Object serialize(IConfig config, Field field, Object value)
		{
			java.util.Optional optional = (java.util.Optional)value;
			if (optional.isEmpty())
				return null;
			return config.getSerializer(IConfigSerializer.getParamType(field)).serialize(config, field, optional.get());
		}

		@Override
		public java.util.Optional deserialize(IConfig config, Field field, Object object)
		{
			return java.util.Optional.ofNullable(
					config.getSerializer(IConfigSerializer.getParamType(field)).deserialize(config, field, object)
			);
		}
	}

	private static Type getParamType(Field field)
	{
		return ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
	}
}
