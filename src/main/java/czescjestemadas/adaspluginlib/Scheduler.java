package czescjestemadas.adaspluginlib;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public final class Scheduler
{
	private final Plugin plugin;

	public Scheduler(Plugin plugin)
	{
		this.plugin = plugin;
	}

	public <T> CompletableFuture<T> later(Function<BukkitTask, T> func, int ticks)
	{
		final CompletableFuture<T> future = new CompletableFuture<>();
		plugin.getServer().getScheduler().runTaskLater(plugin, task -> future.complete(func.apply(task)), ticks);
		return future;
	}

	public <T> CompletableFuture<T> timer(Function<BukkitTask, T> func, int ticks)
	{
		final CompletableFuture<T> future = new CompletableFuture<>();
		plugin.getServer().getScheduler().runTaskTimer(plugin, task -> future.complete(func.apply(task)), ticks, ticks);
		return future;
	}

	public <T> CompletableFuture<T> async(Function<BukkitTask, T> func)
	{
		final CompletableFuture<T> future = new CompletableFuture<>();
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task -> future.complete(func.apply(task)));
		return future;
	}

	public <T> CompletableFuture<T> asyncLater(Function<BukkitTask, T> func, int ticks)
	{
		final CompletableFuture<T> future = new CompletableFuture<>();
		plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, task -> future.complete(func.apply(task)), ticks);
		return future;
	}

	public <T> CompletableFuture<T> asyncTimer(Function<BukkitTask, T> func, int ticks)
	{
		final CompletableFuture<T> future = new CompletableFuture<>();
		plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task -> future.complete(func.apply(task)), ticks, ticks);
		return future;
	}
}
