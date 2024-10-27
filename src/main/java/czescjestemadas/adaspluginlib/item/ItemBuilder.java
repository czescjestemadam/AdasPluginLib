package czescjestemadas.adaspluginlib.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

	public ItemBuilder meta(Consumer<ItemMeta> func)
	{
		return meta(ItemMeta.class, func);
	}

	public <T extends ItemMeta> ItemBuilder meta(Class<T> metaClass, Consumer<T> func)
	{
		item.editMeta(metaClass, func);
		return this;
	}

	public ItemBuilder name(Component component)
	{
		return meta(meta -> meta.displayName(component));
	}

	public ItemBuilder lore(Component... components)
	{
		return lore(List.of(components));
	}

	public ItemBuilder lore(List<Component> components)
	{
		return meta(meta -> meta.lore(components));
	}

	public ItemBuilder appendLore(Component component)
	{
		return meta(meta -> {
			if (meta.lore() == null)
				meta.lore(List.of(component));
			else
				meta.lore().add(component);
		});
	}

	public ItemBuilder pdc(Consumer<PersistentDataContainer> func)
	{
		return meta(meta -> func.accept(meta.getPersistentDataContainer()));
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
