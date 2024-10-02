package czescjestemadas.adaspluginlib.config;

import net.kyori.adventure.text.Component;

public interface IConfigSerializer<T>
{
	Object serialize(Object value);

	T deserialize(Object object);


	class MiniMessage implements IConfigSerializer<Component>
	{
		@Override
		public Object serialize(Object value)
		{
			return net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().serialize((Component)value);
		}

		@Override
		public Component deserialize(Object object)
		{
			return net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(String.valueOf(object));
		}
	}

	class Material implements IConfigSerializer<org.bukkit.Material>
	{
		@Override
		public Object serialize(Object value)
		{
			return value.toString();
		}

		@Override
		public org.bukkit.Material deserialize(Object object)
		{
			return org.bukkit.Material.getMaterial(String.valueOf(object));
		}
	}

	class Sound implements IConfigSerializer<org.bukkit.Sound>
	{
		@Override
		public Object serialize(Object value)
		{
			return value.toString();
		}

		@Override
		public org.bukkit.Sound deserialize(Object object)
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
		public Object serialize(Object value)
		{
			return value.toString();
		}

		@Override
		public org.bukkit.potion.PotionType deserialize(Object object)
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
}
