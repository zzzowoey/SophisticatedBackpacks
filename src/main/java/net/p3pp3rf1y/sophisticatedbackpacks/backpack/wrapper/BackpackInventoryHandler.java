package net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.common.components.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.inception.InceptionUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;

public class BackpackInventoryHandler extends InventoryHandler {
	public BackpackInventoryHandler(int numberOfInventorySlots, IStorageWrapper storageWrapper, CompoundTag contentsNbt, Runnable saveHandler, int slotLimit) {
		super(numberOfInventorySlots, storageWrapper, contentsNbt, saveHandler, slotLimit, Config.SERVER.stackUpgrade);
	}

	@Override
	protected boolean isAllowed(ItemVariant resource, long amount) {
		return !Config.SERVER.disallowedItems.isItemDisallowed(resource.getItem())
				&& (!(resource.getItem() instanceof BackpackItem) || (hasInceptionUpgrade() && isBackpackWithoutInceptionUpgrade(resource.toStack())));
	}

/*	@Override
	protected boolean isAllowed(ItemStack stack) {
		return !Config.SERVER.disallowedItems.isItemDisallowed(stack.getItem())
				&& (!(stack.getItem() instanceof BackpackItem) || (hasInceptionUpgrade() && isBackpackWithoutInceptionUpgrade(stack)));
	}*/

	private boolean hasInceptionUpgrade() {
		return storageWrapper.getUpgradeHandler().hasUpgrade(InceptionUpgradeItem.TYPE);
	}

	private boolean isBackpackWithoutInceptionUpgrade(ItemStack stack) {
		return (stack.getItem() instanceof BackpackItem) && !IBackpackWrapper.maybeGet(stack)
				.map(w -> w.getUpgradeHandler().hasUpgrade(InceptionUpgradeItem.TYPE)).orElse(false);
	}
}
