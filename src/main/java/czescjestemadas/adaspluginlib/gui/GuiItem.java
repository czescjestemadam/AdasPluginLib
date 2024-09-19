package czescjestemadas.adaspluginlib.gui;

import org.bukkit.inventory.ItemStack;

public class GuiItem
{
	protected final ItemStack itemStack;
	protected boolean locked;

	public GuiItem(ItemStack itemStack, boolean locked)
	{
		this.itemStack = itemStack;
		this.locked = locked;
	}

	public ItemStack getItemStack()
	{
		return itemStack;
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
