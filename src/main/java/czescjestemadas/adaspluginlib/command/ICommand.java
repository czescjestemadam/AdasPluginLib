package czescjestemadas.adaspluginlib.command;

import org.bukkit.command.TabExecutor;

import java.util.Collection;
import java.util.List;

public abstract class ICommand implements TabExecutor
{
	protected final String name;
	protected final String permission;

	protected ICommand(String name)
	{
		this(name, null);
	}

	protected ICommand(String name, String permission)
	{
		this.name = name;
		this.permission = permission;
	}

	public String getName()
	{
		return name;
	}

	protected List<String> retMatches(String arg, String... args)
	{
		return retMatches(arg, List.of(args));
	}

	protected List<String> retMatches(String arg, Collection<String> args)
	{
		return args.stream().filter(s -> s.length() >= arg.length() && s.regionMatches(true, 0, arg, 0, arg.length())).toList();
	}
}
