package czescjestemadas.adaspluginlib.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RegistrableICommand extends Command
{
	private final Plugin plugin;
	private final ICommand command;

	public RegistrableICommand(Plugin plugin, ICommand command)
	{
		super(command.name);
		if (command.permission != null)
			this.setPermission(command.permission);
		this.plugin = plugin;
		this.command = command;
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args)
	{
		boolean success;

		if (!this.plugin.isEnabled())
			throw new CommandException("Cannot execute command '" + commandLabel + "' in plugin " + this.plugin.getDescription().getFullName() + " - plugin is disabled.");

		else if (!this.testPermission(sender))
			return true;

		else
		{
			try
			{
				success = this.command.onCommand(sender, this, commandLabel, args);
			}
			catch (Throwable var9)
			{
				throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin " + this.plugin.getDescription().getFullName(), var9);
			}

			if (!success && !this.usageMessage.isEmpty())
			{
				final String[] var10 = this.usageMessage.replace("<command>", commandLabel).split("\n");

				for (String line : var10)
					sender.sendMessage(line);
			}

			return success;
		}
	}

	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException
	{
		final List<String> completions;

		try
		{
			completions = this.command.onTabComplete(sender, this, alias, args);
		}
		catch (Throwable var11)
		{
			final StringBuilder message = new StringBuilder();
			message.append("Unhandled exception during tab completion for command '/").append(alias).append(' ');

			for (String arg : args)
				message.append(arg).append(' ');

			message.deleteCharAt(message.length() - 1).append("' in plugin ").append(this.plugin.getDescription().getFullName());
			throw new CommandException(message.toString(), var11);
		}

		return completions == null ? super.tabComplete(sender, alias, args) : completions;
	}
}
