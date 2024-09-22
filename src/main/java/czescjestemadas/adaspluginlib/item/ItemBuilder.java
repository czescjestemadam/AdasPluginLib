package czescjestemadas.adaspluginlib.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;
import java.util.function.Consumer;

public class ItemBuilder
{
	private final ItemStack item;

	public ItemBuilder(ItemStack item)
	{
		this.item = item;
	}

	public ItemBuilder(Material material)
	{
		this.item = new ItemStack(material);
	}

	public ItemBuilder(ItemBuilder builder)
	{
		this.item = builder.item.clone();
	}

	public ItemBuilder amount(int amount)
	{
		item.setAmount(amount);
		return this;
	}

	public ItemBuilder name(Component component)
	{
		item.editMeta(meta -> meta.displayName(component));
		return this;
	}

	public ItemBuilder lore(Component... components)
	{
		return lore(List.of(components));
	}

	public ItemBuilder lore(List<Component> components)
	{
		item.editMeta(meta -> meta.lore(components));
		return this;
	}

	public ItemBuilder appendLore(Component component)
	{
		item.editMeta(meta -> {
			if (meta.lore() == null)
				meta.lore(List.of(component));
			else
				meta.lore().add(component);
		});
		return this;
	}

	public ItemBuilder pdc(Consumer<PersistentDataContainer> func)
	{
		item.editMeta(meta -> func.accept(meta.getPersistentDataContainer()));
		return this;
	}

	public ItemStack build()
	{
		return item;
	}

	@Override
	public String toString()
	{
		return "ItemBuilder{" +
				"item=" + item +
				'}';
	}
}
