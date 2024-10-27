package czescjestemadas.adaspluginlib.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.Objects;

public class ComponentTemplate
{
	private final String format;

	public ComponentTemplate(String format)
	{
		this.format = format;
	}

	public Component get(TagResolver... resolvers)
	{
		return MiniMessage.miniMessage().deserialize(format, resolvers);
	}

	@Override
	public final boolean equals(Object object)
	{
		if (this == object) return true;
		if (!(object instanceof ComponentTemplate that)) return false;

		return format.equals(that.format);
	}

	@Override
	public int hashCode()
	{
		return format.hashCode();
	}

	@Override
	public String toString()
	{
		return format;
	}


	public static ComponentTemplate of(String format)
	{
		return new ComponentTemplate(Objects.requireNonNull(format));
	}
}
