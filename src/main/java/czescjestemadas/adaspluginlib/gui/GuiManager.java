package czescjestemadas.adaspluginlib.gui;

import czescjestemadas.adaspluginlib.AdasPluginLib;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public final class GuiManager implements Listener
{
	private final Plugin plugin;
	private boolean registered = false;

	public GuiManager(Plugin plugin)
	{
		this.plugin = plugin;
	}

	public boolean isRegistered()
	{
		return registered;
	}

	public void register()
	{
		if (!registered)
		{
			AdasPluginLib.registerListeners(plugin, this);
			registered = true;
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	private void onClick(InventoryClickEvent e)
	{
		final Inventory inventory = e.getClickedInventory();
		if (inventory != null && inventory.getHolder() instanceof Gui gui)
			gui.onClick(e);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onClose(InventoryCloseEvent e)
	{
		if (e.getInventory().getHolder() instanceof Gui gui)
			gui.onClose();
	}
}
