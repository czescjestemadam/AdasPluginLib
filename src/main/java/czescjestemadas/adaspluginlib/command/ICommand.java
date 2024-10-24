package czescjestemadas.adaspluginlib.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	public abstract boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args);

	public abstract @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args);

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
