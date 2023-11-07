package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeHandler;

import java.util.UUID;

public class RequestBackpackInventoryContentsMessage extends SimplePacketBase {
	private final UUID backpackUuid;

	public RequestBackpackInventoryContentsMessage(UUID backpackUuid) {
		this.backpackUuid = backpackUuid;
	}

	public RequestBackpackInventoryContentsMessage(FriendlyByteBuf buffer) { this(buffer.readUUID()); }

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUUID(this.backpackUuid);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if (player == null) {
				return;
			}

			CompoundTag backpackContents = BackpackStorage.get().getOrCreateBackpackContents(backpackUuid);

			CompoundTag inventoryContents = new CompoundTag();
			Tag inventoryNbt = backpackContents.get(InventoryHandler.INVENTORY_TAG);
			if (inventoryNbt != null) {
				inventoryContents.put(InventoryHandler.INVENTORY_TAG, inventoryNbt);
			}
			Tag upgradeNbt = backpackContents.get(UpgradeHandler.UPGRADE_INVENTORY_TAG);
			if (upgradeNbt != null) {
				inventoryContents.put(UpgradeHandler.UPGRADE_INVENTORY_TAG, upgradeNbt);
			}

			SBPPacketHandler.sendToClient(player, new BackpackContentsMessage(backpackUuid, inventoryContents));
		});
		return true;
	}
}
