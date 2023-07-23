package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.lookup.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IUpgradeWrapper;

import javax.annotation.Nullable;
import java.util.Map;

public class UpgradeToggleMessage extends SimplePacketBase {
	private final int upgradeSlot;

	public UpgradeToggleMessage(int upgradeSlot) {
		this.upgradeSlot = upgradeSlot;
	}

	public UpgradeToggleMessage(FriendlyByteBuf buffer) { this(buffer.readInt()); }

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(this.upgradeSlot);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> handleMessage(context.getSender(), this));
		return true;
	}

	private static void handleMessage(@Nullable ServerPlayer player, UpgradeToggleMessage msg) {
		if (player == null) {
			return;
		}

		PlayerInventoryProvider.get().runOnBackpacks(player, (backpack, inventoryName, identifier, slot) -> {
			BackpackWrapperLookup.maybeGet(backpack).ifPresent(w -> {
				Map<Integer, IUpgradeWrapper> slotWrappers = w.getUpgradeHandler().getSlotWrappers();
				if (slotWrappers.containsKey(msg.upgradeSlot)) {
					IUpgradeWrapper upgradeWrapper = slotWrappers.get(msg.upgradeSlot);
					if (upgradeWrapper.canBeDisabled()) {
						upgradeWrapper.setEnabled(!upgradeWrapper.isEnabled());
						String translKey = upgradeWrapper.isEnabled() ? "gui.sophisticatedbackpacks.status.upgrade_switched_on" : "gui.sophisticatedbackpacks.status.upgrade_switched_off";
						player.displayClientMessage(Component.translatable(translKey, upgradeWrapper.getUpgradeStack().getHoverName()), true);
					}
				}
			});
			return true;
		});
	}
}