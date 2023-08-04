package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;

import javax.annotation.Nullable;

public class SyncClientInfoMessage extends SimplePacketBase {
	private final int slotIndex;
	@Nullable
	private final CompoundTag renderInfoNbt;
	private final int columnsTaken;

	public SyncClientInfoMessage(int slotNumber, @Nullable CompoundTag renderInfoNbt, int columnsTaken) {
		slotIndex = slotNumber;
		this.renderInfoNbt = renderInfoNbt;
		this.columnsTaken = columnsTaken;
	}

	public SyncClientInfoMessage(FriendlyByteBuf buffer) { this(buffer.readInt(), buffer.readNbt(), buffer.readInt()); }

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(this.slotIndex);
		buffer.writeNbt(this.renderInfoNbt);
		buffer.writeInt(this.columnsTaken);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			Player player = context.getClientPlayer();
			if (player == null || renderInfoNbt == null || !(player.containerMenu instanceof BackpackContainer)) {
				return;
			}
			ItemStack backpack = player.getInventory().items.get(slotIndex);
			BackpackWrapperLookup.get(backpack).ifPresent(backpackWrapper -> {
				backpackWrapper.getRenderInfo().deserializeFrom(renderInfoNbt);
				backpackWrapper.setColumnsTaken(columnsTaken, false);
			});
		});
		return true;
	}

}
