package czescjestemadas.adaspluginlib.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class GuiButton extends GuiItem
{
	private final BiConsumer<Player, ClickType> onClick;

	public GuiButton(ItemStack itemStack, BiConsumer<Player, ClickType> onClick)
	{
		super(itemStack, true);
		this.onClick = onClick;
	}

	public void click(Player player, ClickType type)
	{
		onClick.accept(player, type);
	}
}
