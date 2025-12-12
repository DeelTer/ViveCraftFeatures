package ru.deelter.vr.viveCraftFeatures;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ViveItems {

	public static boolean isClimbingClaws(@NotNull ItemStack item) {
		if (!item.getType().equals(Material.SHEARS)) return false;
		return item.getItemMeta().isUnbreakable();
	}
}
