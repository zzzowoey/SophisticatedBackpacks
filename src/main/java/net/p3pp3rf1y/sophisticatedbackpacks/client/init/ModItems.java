package net.p3pp3rf1y.sophisticatedbackpacks.client.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;

import static net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper.DEFAULT_CLOTH_COLOR;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BACKPACKS;

@Environment(EnvType.CLIENT)
public class ModItems {
	public static void register() {
		ColorProviderRegistry.ITEM.register((backpack, layer) -> {
			if (layer > 1 || !(backpack.getItem() instanceof BackpackItem)) {
				return -1;
			}
			return BackpackWrapperLookup.get(backpack).map(backpackWrapper -> {
				if (layer == 0) {
					return backpackWrapper.getMainColor();
				} else if (layer == 1) {
					return backpackWrapper.getAccentColor();
				}
				return -1;
			}).orElse(DEFAULT_CLOTH_COLOR);
		}, BACKPACKS);
	}
}
