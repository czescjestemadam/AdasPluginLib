package czescjestemadas.adaspluginlib.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class Gui implements InventoryHolder
{
	protected final Inventory inventory;
	protected final Map<Integer, GuiItem> guiItems = new HashMap<>();
	protected boolean locked;

	protected Gui(int size, Component title, boolean locked)
	{
		this.inventory = Bukkit.createInventory(this, size, title);
		this.locked = locked;
	}

	public void onClose()
	{
	}

	public void onClick(InventoryClickEvent e)
	{
		if (locked)
			e.setCancelled(true);

		final GuiItem guiItem = getGuiItem(e.getSlot());
		if (guiItem.isLocked())
			e.setCancelled(true);

		if (guiItem instanceof GuiButton button)
			button.click((Player)e.getWhoClicked(), e.getClick());
	}

	@Override
	public @NotNull Inventory getInventory()
	{
		return inventory;
	}

	public void updateInventoryItems()
	{
		final ItemStack[] items = new ItemStack[inventory.getSize()];

		for (Map.Entry<Integer, GuiItem> entry : guiItems.entrySet())
		{
			final int slot = entry.getKey();
			if (slot < 0 || slot >= items.length)
				continue;

			items[slot] = entry.getValue().getItemStack();
		}

		inventory.setContents(items);
	}

	public Map<Integer, GuiItem> getGuiItems()
	{
		return guiItems;
	}

	public GuiItem getGuiItem(int slot)
	{
		return guiItems.get(slot);
	}

	public void setGuiItem(int slot, GuiItem item)
	{
		guiItems.put(slot, item);
	}

	public boolean isLocked()
	{
		return locked;
	}

	public void setLocked(boolean locked)
	{
		this.locked = locked;
	}
}
