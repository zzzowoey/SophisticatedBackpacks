package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBlockPickResponseUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.common.lookup.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;

import javax.annotation.Nullable;

public class BlockPickMessage extends SimplePacketBase {
	private final ItemStack filter;

	public BlockPickMessage(ItemStack filter) {
		this.filter = filter;
	}

	public BlockPickMessage(FriendlyByteBuf buffer) { this(buffer.readItem()); }

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeItem(this.filter);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> handleMessage(context.getSender(), this));
		return true;
	}

	private static void handleMessage(@Nullable ServerPlayer player, BlockPickMessage msg) {
		if (player == null) {
			return;
		}

		PlayerInventoryProvider.get().runOnBackpacks(player, (backpack, inventoryHandlerName, identifier, slot) -> BackpackWrapperLookup.get(backpack)
				.map(wrapper -> {
					for (IBlockPickResponseUpgrade upgrade : wrapper.getUpgradeHandler().getWrappersThatImplement(IBlockPickResponseUpgrade.class)) {
						if (upgrade.pickBlock(player, msg.filter)) {
							return true;
						}
					}
					return false;
				}).orElse(false));
	}
}
