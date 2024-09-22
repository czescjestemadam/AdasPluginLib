package czescjestemadas.adaspluginlib.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueLoader
{
	Type value();

	enum Type
	{
		MINIMESSAGE
				{
					@Override
					protected Object load(Object value)
					{
						return MiniMessage.miniMessage().deserialize(String.valueOf(value));
					}

					@Override
					protected Object save(Object value)
					{
						return MiniMessage.miniMessage().serialize((Component)value);
					}
				},
		MATERIAL
				{
					@Override
					protected Object load(Object value)
					{
						return Material.getMaterial(String.valueOf(value));
					}

					@Override
					protected Object save(Object value)
					{
						return value.toString();
					}
				},
		SOUND
				{
					@Override
					protected Object load(Object value)
					{
						try
						{
							return Sound.valueOf(String.valueOf(value).toUpperCase());
						}
						catch (IllegalArgumentException e)
						{
							return null;
						}
					}

					@Override
					protected Object save(Object value)
					{
						return value.toString();
					}
				},
		;

		protected abstract Object load(Object value);

		protected abstract Object save(Object value);
	}
}
