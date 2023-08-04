package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IUpgradeWrapper;

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
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if (player == null) {
				return;
			}

			PlayerInventoryProvider.get().runOnBackpacks(player, (backpack, inventoryName, identifier, slot) -> {
				BackpackWrapperLookup.get(backpack).ifPresent(w -> {
					Map<Integer, IUpgradeWrapper> slotWrappers = w.getUpgradeHandler().getSlotWrappers();
					if (slotWrappers.containsKey(upgradeSlot)) {
						IUpgradeWrapper upgradeWrapper = slotWrappers.get(upgradeSlot);
						if (upgradeWrapper.canBeDisabled()) {
							upgradeWrapper.setEnabled(!upgradeWrapper.isEnabled());
							String translKey = upgradeWrapper.isEnabled() ? "gui.sophisticatedbackpacks.status.upgrade_switched_on" : "gui.sophisticatedbackpacks.status.upgrade_switched_off";
							player.displayClientMessage(Component.translatable(translKey, upgradeWrapper.getUpgradeStack().getHoverName()), true);
						}
					}
				});
				return true;
			});
		});
		return true;
	}
}