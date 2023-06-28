package net.p3pp3rf1y.sophisticatedbackpacks.client.init;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.*;

public class ModItemColors {
	private ModItemColors() {}

	public static void registerItemColorHandlers() {
		ColorProviderRegistry.ITEM.register((backpack, layer) -> {
			if (layer > 1 || !(backpack.getItem() instanceof BackpackItem)) {
				return -1;
			}
			return IBackpackWrapper.maybeGet(backpack).map(backpackWrapper -> {
				if (layer == 0) {
					return backpackWrapper.getMainColor();
				} else if (layer == 1) {
					return backpackWrapper.getAccentColor();
				}
				return -1;
			}).orElse(BackpackWrapper.DEFAULT_CLOTH_COLOR);
		}, BACKPACK.get(), IRON_BACKPACK.get(), GOLD_BACKPACK.get(), DIAMOND_BACKPACK.get(), NETHERITE_BACKPACK.get());
	}
}
