package czescjestemadas.adaspluginlib.command.node;

import czescjestemadas.adaspluginlib.command.ICommand;
import czescjestemadas.adaspluginlib.command.node.annotation.Node;
import czescjestemadas.adaspluginlib.command.node.annotation.Permission;
import czescjestemadas.adaspluginlib.command.node.annotation.PlayerOnlyNode;
import czescjestemadas.adaspluginlib.command.node.annotation.RootNode;
import czescjestemadas.adaspluginlib.util.EnumUtil;
import czescjestemadas.adaspluginlib.util.StrUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
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
		addParser(OfflinePlayer.class, Bukkit::getOfflinePlayerIfCached)
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
			if (parameters.length != 1 || !CommandSender.class.isAssignableFrom(parameters[0].getType()))
				return false;

			if (checkPlayerOnly(sender, rootNode))
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

			final Parameter[] parameters = method.getParameters();

			final String nodePath = node.value();
			final String[] nodePathArgs = nodePath.split("\\s+");

			if (args.length < nodePathArgs.length - 1)
				continue;

			if (parameters.length != countNeededParamCount(nodePath) + 1)
				continue;

			System.out.println("node = " + node);
			final List<String> nodeStrArgs = new ArrayList<>();

			boolean matches = true;

			int argIdx = 0;
			for (int nodeArgIdx = 0; nodeArgIdx < nodePathArgs.length; nodeArgIdx++)
			{
				if (nodePathArgs[nodeArgIdx].equals("{}"))
				{
					// Single word placeholder
					if (argIdx < args.length)
					{
						nodeStrArgs.add(args[argIdx++]);
					}
					else
					{
						matches = false;
						break;
					}
				}
				else if (nodePathArgs[nodeArgIdx].equals("[]"))
				{
					final StringBuilder wide = new StringBuilder();

					// Multiple words placeholder
					while (argIdx < args.length && (nodeArgIdx + 1 >= nodePathArgs.length || !args[argIdx].equals(nodePathArgs[nodeArgIdx + 1])))
					{
						if (!wide.isEmpty())
							wide.append(" ");
						wide.append(args[argIdx++]);
					}

					nodeStrArgs.add(wide.toString());
				}
				else
				{
					// Fixed parts of the pattern
					if (argIdx < args.length && nodePathArgs[nodeArgIdx].equals(args[argIdx]))
					{
						argIdx++;
					}
					else
					{
						matches = false;
						break;
					}
				}
			}

			System.out.println("nodeStrArgs = " + nodeStrArgs);

			if (!matches)
				continue;

			System.out.println("method = " + method);

			final Permission permission = method.getAnnotation(Permission.class);
			if (permission != null && !sender.hasPermission(permission.value()))
			{
				sender.sendRichMessage(permission.errorMessage());
				return false;
			}

			if (checkPlayerOnly(sender, method))
				return false;

			final Object[] nodeArgs = new Object[parameters.length];
			nodeArgs[0] = sender;
			for (int i = 1; i < nodeArgs.length; i++)
				nodeArgs[i] = getParser(parameters[i].getType()).parse(nodeStrArgs.get(i - 1));

			System.out.println("nodeArgs = " + Arrays.toString(nodeArgs));

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

	/// true - failed
	private static boolean checkPlayerOnly(CommandSender sender, Method node)
	{
		final PlayerOnlyNode annotation = node.getAnnotation(PlayerOnlyNode.class);
		if (annotation == null)
			return false;

		if (sender instanceof Player)
			return false;

		final String errorMessage = annotation.errorMessage();
		if (!errorMessage.isEmpty())
			sender.sendRichMessage(errorMessage);
		return true;
	}

	private static int countNeededParamCount(String nodePath)
	{
		return StrUtil.countOccurrences(nodePath, "{}") + StrUtil.countOccurrences(nodePath, "[]");
	}

	private static List<String> getMatchingArgs(String[] args, String nodePath, boolean ignoreCase)
	{
		final String[] nodePathArgs = nodePath.split("\\s+");

		final List<String> nodeStrArgs = new ArrayList<>();

		int argIdx = 0;
		for (int nodeArgIdx = 0; nodeArgIdx < nodePathArgs.length; nodeArgIdx++)
		{
			if (nodePathArgs[nodeArgIdx].equals("{}"))
			{
				nodeStrArgs.add(args[argIdx]);
				argIdx++;
			}
			else if (nodePathArgs[nodeArgIdx].equals("[]"))
			{
				final StringBuilder replacement = new StringBuilder();
				while (argIdx < args.length && (nodeArgIdx + 1 >= nodePathArgs.length || !args[argIdx].equals(nodePathArgs[nodeArgIdx + 1])))
				{
					if (!replacement.isEmpty())
						replacement.append(" ");
					replacement.append(args[argIdx]);
					argIdx++;
				}
				nodeStrArgs.add(replacement.toString());
			}
			else
				argIdx++;
		}

		return nodeStrArgs;
	}
}
