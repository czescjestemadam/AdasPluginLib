package czescjestemadas.adaspluginlib;

import czescjestemadas.adaspluginlib.gui.GuiManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class AdasPluginLib extends JavaPlugin
{
	private static AdasPluginLib INST;

	private Scheduler scheduler;
	private GuiManager guiManager;

	@Override
	public void onEnable()
	{
		INST = this;

		scheduler = new Scheduler(this);

		guiManager = new GuiManager(this);
		guiManager.register();
	}

	public Scheduler getScheduler()
	{
		return scheduler;
	}

	public GuiManager getGuiManager()
	{
		return guiManager;
	}


	public static AdasPluginLib get()
	{
		return INST;
	}

	public static void registerListeners(Plugin plugin, Listener... listeners)
	{
		for (Listener listener : listeners)
			plugin.getServer().getPluginManager().registerEvents(listener, plugin);
	}
}
