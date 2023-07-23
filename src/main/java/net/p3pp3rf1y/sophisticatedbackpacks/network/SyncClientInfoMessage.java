package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.lookup.BackpackWrapperLookup;
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
	public boolean handle(Context context) {
		context.enqueueWork(() -> handleMessage(this));
		return true;
	}

	private static void handleMessage(SyncClientInfoMessage msg) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || msg.renderInfoNbt == null || !(player.containerMenu instanceof BackpackContainer)) {
			return;
		}
		ItemStack backpack = player.getInventory().items.get(msg.slotIndex);
		BackpackWrapperLookup.maybeGet(backpack).ifPresent(backpackWrapper -> {
			backpackWrapper.getRenderInfo().deserializeFrom(msg.renderInfoNbt);
			backpackWrapper.setColumnsTaken(msg.columnsTaken, false);
		});
	}
}
