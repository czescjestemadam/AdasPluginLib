package czescjestemadas.adaspluginlib;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Scheduler
{
	private final Plugin plugin;

	public Scheduler(Plugin plugin)
	{
		this.plugin = plugin;
	}

	public CompletableFuture<BukkitTask> later(int ticks)
	{
		return later(Function.identity(), ticks);
	}

	public <T> CompletableFuture<T> later(Function<BukkitTask, T> func, int ticks)
	{
		final CompletableFuture<T> future = new CompletableFuture<>();
		plugin.getServer().getScheduler().runTaskLater(plugin, task -> future.complete(func.apply(task)), ticks);
		return future;
	}

	public void timer(Consumer<BukkitTask> func, int ticks)
	{
		plugin.getServer().getScheduler().runTaskTimer(plugin, func, ticks, ticks);
	}

	public CompletableFuture<BukkitTask> async()
	{
		return async(Function.identity());
	}

	public <T> CompletableFuture<T> async(Function<BukkitTask, T> func)
	{
		final CompletableFuture<T> future = new CompletableFuture<>();
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task -> future.complete(func.apply(task)));
		return future;
	}

	public CompletableFuture<BukkitTask> asyncLater(int ticks)
	{
		return asyncLater(Function.identity(), ticks);
	}

	public <T> CompletableFuture<T> asyncLater(Function<BukkitTask, T> func, int ticks)
	{
		final CompletableFuture<T> future = new CompletableFuture<>();
		plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, task -> future.complete(func.apply(task)), ticks);
		return future;
	}

	public void asyncTimer(Consumer<BukkitTask> func, int ticks)
	{
		plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, func, ticks, ticks);
	}
}
