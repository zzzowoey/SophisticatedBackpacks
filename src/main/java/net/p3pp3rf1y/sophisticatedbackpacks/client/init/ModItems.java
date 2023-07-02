package net.p3pp3rf1y.sophisticatedbackpacks.client.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.common.components.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackItemStackRenderer;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.*;

@Environment(EnvType.CLIENT)
public class ModItems {

	public static void register() {
		registerRenderers();
		registerItemColorHandlers();
	}

	private static void registerRenderers() {
		BuiltinItemRendererRegistry.INSTANCE.register(net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BACKPACK.get(), BackpackItemStackRenderer::renderByItem);
		BuiltinItemRendererRegistry.INSTANCE.register(net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.IRON_BACKPACK.get(), BackpackItemStackRenderer::renderByItem);
		BuiltinItemRendererRegistry.INSTANCE.register(net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.GOLD_BACKPACK.get(), BackpackItemStackRenderer::renderByItem);
		BuiltinItemRendererRegistry.INSTANCE.register(net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.DIAMOND_BACKPACK.get(), BackpackItemStackRenderer::renderByItem);
		BuiltinItemRendererRegistry.INSTANCE.register(net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.NETHERITE_BACKPACK.get(), BackpackItemStackRenderer::renderByItem);
	}

	private static void registerItemColorHandlers() {
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
