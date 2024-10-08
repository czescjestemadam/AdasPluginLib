package czescjestemadas.adaspluginlib.command.node;

import czescjestemadas.adaspluginlib.command.ICommand;
import czescjestemadas.adaspluginlib.command.node.annotation.*;
import czescjestemadas.adaspluginlib.util.EnumUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

public abstract class INodeCommand extends ICommand
{
	private final Map<Type, ArgParser<?>> parsers = new HashMap<>();

	protected INodeCommand(String name)
	{
		super(name);
		addParser(Integer.class, Integer::parseInt);
		addParser(int.class, Integer::parseInt);
		addParser(Float.class, Float::parseFloat);
		addParser(float.class, Float::parseFloat);
		addParser(Double.class, Double::parseDouble);
		addParser(double.class, Double::parseDouble);
		addParser(Player.class, Bukkit::getPlayer);
		addParser(OfflinePlayer.class, Bukkit::getOfflinePlayerIfCached);
		addParser(UUID.class, UUID::fromString);
		addParser(Material.class, Material::getMaterial);
		addParser(Sound.class, arg -> EnumUtil.valueOf(Sound.class, arg));
		addParser(PotionType.class, arg -> EnumUtil.valueOf(PotionType.class, arg));
		addParser(Particle.class, arg -> EnumUtil.valueOf(Particle.class, arg));
		addParser(NamedTextColor.class, NamedTextColor.NAMES::value);
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
	{
		if (args.length == 0)
		{
			final Method rootNode = getRootNode();
			if (rootNode == null)
				return false;

			final Parameter[] parameters = rootNode.getParameters();
			if (checkParams(args, new String[0], parameters, true))
				return false;

			if (checkPlayerOnly(sender, rootNode, true))
				return false;

			try
			{
				rootNode.invoke(this, sender);
			}
			catch (IllegalAccessException | InvocationTargetException e)
			{
				throw new RuntimeException(e);
			}

			return true;
		}

		for (Method method : getClass().getMethods())
		{
			final Node node = method.getAnnotation(Node.class);
			if (node == null)
				continue;

			final String[] nodePathArgs = node.value().split("\\s+");
			final Parameter[] parameters = method.getParameters();
			if (checkParams(args, nodePathArgs, parameters, false))
				continue;

			final List<String> nodeStrArgs = fillStrArgsIfMatches(args, nodePathArgs);
			if (nodeStrArgs == null)
				continue;

			final Permission permission = method.getAnnotation(Permission.class);
			if (permission != null && !sender.hasPermission(permission.value()))
			{
				sender.sendRichMessage(permission.errorMessage());
				return false;
			}

			if (checkPlayerOnly(sender, method, true))
				return false;

			final Object[] nodeArgs = parseNodeArgs(parameters, nodeStrArgs);
			nodeArgs[0] = sender;

			try
			{
				method.invoke(this, nodeArgs);
			}
			catch (IllegalAccessException | InvocationTargetException e)
			{
				throw new RuntimeException(e);
			}

			return true;
		}

		return false;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args)
	{
		if (args.length == 1)
		{
			final Method completer = getRootNodeCompleter();
			if (completer == null)
				return List.of();

			final Parameter[] parameters = completer.getParameters();
			if (checkParams(args, new String[0], parameters, true))
				return List.of();

			if (checkPlayerOnly(sender, completer, false))
				return List.of();

			try
			{
				return (List<String>)completer.invoke(this, sender);
			}
			catch (IllegalAccessException | InvocationTargetException e)
			{
				e.printStackTrace();
				return List.of();
			}
		}

		final String[] searchArgs = args.length == 0 ? new String[0] : Arrays.copyOfRange(args, 0, args.length - 1);

		for (Method method : getClass().getMethods())
		{
			final NodeCompleter node = method.getAnnotation(NodeCompleter.class);
			if (node == null)
				continue;

			final String[] nodePathArgs = node.value().split("\\s+");
			final Parameter[] parameters = method.getParameters();
			if (checkParams(searchArgs, nodePathArgs, parameters, false))
				continue;

			final List<String> nodeStrArgs = fillStrArgsIfMatches(searchArgs, nodePathArgs);
			if (nodeStrArgs == null)
				continue;

			final Permission permission = method.getAnnotation(Permission.class);
			if (permission != null && !sender.hasPermission(permission.value()))
				return List.of();

			if (checkPlayerOnly(sender, method, false))
				return List.of();

			final Object[] nodeArgs = parseNodeArgs(parameters, nodeStrArgs);
			nodeArgs[0] = sender;

			try
			{
				return retMatches(args[args.length - 1], (List<String>)method.invoke(this, nodeArgs));
			}
			catch (IllegalAccessException | InvocationTargetException e)
			{
				e.printStackTrace();
				return List.of();
			}
		}

		return List.of();
	}

	protected ArgParser<?> getParser(Type type)
	{
		return parsers.getOrDefault(type, arg -> arg);
	}

	protected <T> void addParser(Class<T> cls, ArgParser<T> parser)
	{
		parsers.put(cls, parser);
	}

	private Method getRootNode()
	{
		for (Method method : getClass().getMethods())
		{
			if (method.isAnnotationPresent(RootNode.class))
				return method;
		}

		return null;
	}

	private Method getRootNodeCompleter()
	{
		for (Method method : getClass().getMethods())
		{
			if (method.isAnnotationPresent(RootNodeCompleter.class))
				return method;
		}

		return null;
	}

	/// true - failed
	private static boolean checkParams(String[] args, String[] nodePathArgs, Parameter[] parameters, boolean root)
	{
		if (parameters.length < 1 || !CommandSender.class.isAssignableFrom(parameters[0].getType()))
			return true;

		if (root && parameters.length != 1)
			return true;

		if (!root && args.length < nodePathArgs.length - 1)
			return true;

		if (!root && parameters.length != Arrays.stream(nodePathArgs).filter(str -> str.equals("{}") || str.equals("[]")).count() + 1)
			return true;

		return false;
	}

	/// null == not matching
	private static List<String> fillStrArgsIfMatches(String[] args, String[] nodePathArgs)
	{
		final List<String> nodeStrArgs = new ArrayList<>();

		int argIdx = 0;
		for (int nodeArgIdx = 0; nodeArgIdx < nodePathArgs.length; nodeArgIdx++)
		{
			// Single word placeholder
			if (nodePathArgs[nodeArgIdx].equals("{}"))
			{
				if (argIdx < args.length)
					nodeStrArgs.add(args[argIdx++]);
				else
					return null;
			}
			// Multiple words placeholder
			else if (nodePathArgs[nodeArgIdx].equals("[]"))
			{
				final StringBuilder wide = new StringBuilder();

				while (argIdx < args.length && (nodeArgIdx + 1 >= nodePathArgs.length || !args[argIdx].equals(nodePathArgs[nodeArgIdx + 1])))
				{
					if (!wide.isEmpty())
						wide.append(" ");
					wide.append(args[argIdx++]);
				}

				nodeStrArgs.add(wide.toString());
			}
			// Fixed parts of the pattern
			else
			{
				if (argIdx < args.length && nodePathArgs[nodeArgIdx].equals(args[argIdx]))
					argIdx++;
				else
					return null;
			}
		}

		return nodeStrArgs;
	}

	/// true - failed
	private static boolean checkPlayerOnly(CommandSender sender, Method node, boolean send)
	{
		final PlayerOnlyNode annotation = node.getAnnotation(PlayerOnlyNode.class);
		if (annotation == null)
			return false;

		if (sender instanceof Player)
			return false;

		if (send)
		{
			final String errorMessage = annotation.errorMessage();
			if (!errorMessage.isEmpty())
				sender.sendRichMessage(errorMessage);
		}

		return true;
	}

	private Object[] parseNodeArgs(Parameter[] parameters, List<String> nodeStrArgs)
	{
		final Object[] nodeArgs = new Object[parameters.length];
		for (int i = 1; i < nodeArgs.length; i++)
			nodeArgs[i] = getParser(parameters[i].getType()).parse(nodeStrArgs.get(i - 1));

		return nodeArgs;
	}
}
