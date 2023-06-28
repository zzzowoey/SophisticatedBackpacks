package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedcore.client.render.ClientStorageContentsTooltip;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;

import javax.annotation.Nullable;
import java.util.UUID;

public class BackpackContentsMessage extends SimplePacketBase {
	private final UUID backpackUuid;
	@Nullable
	private final CompoundTag backpackContents;

	public BackpackContentsMessage(UUID backpackUuid, @Nullable CompoundTag backpackContents) {
		this.backpackUuid = backpackUuid;
		this.backpackContents = backpackContents;
	}
	public BackpackContentsMessage(FriendlyByteBuf buffer) { this(buffer.readUUID(), buffer.readNbt()); }

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUUID(this.backpackUuid);
		buffer.writeNbt(this.backpackContents);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> handleMessage(this));
		return true;
	}

	private static void handleMessage(BackpackContentsMessage msg) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || msg.backpackContents == null) {
			return;
		}

		BackpackStorage.get().setBackpackContents(msg.backpackUuid, msg.backpackContents);
		ClientStorageContentsTooltip.refreshContents();
	}
}
