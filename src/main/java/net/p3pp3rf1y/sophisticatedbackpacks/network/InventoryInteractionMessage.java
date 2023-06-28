package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.p3pp3rf1y.sophisticatedbackpacks.util.InventoryInteractionHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;

import javax.annotation.Nullable;

public class InventoryInteractionMessage extends SimplePacketBase {
	private final BlockPos pos;
	private final Direction face;

	public InventoryInteractionMessage(BlockPos pos, Direction face) {
		this.pos = pos;
		this.face = face;
	}

	public InventoryInteractionMessage(FriendlyByteBuf buffer) { this(buffer.readBlockPos(), buffer.readEnum(Direction.class)); }

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeLong(this.pos.asLong());
		buffer.writeEnum(this.face);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> handleMessage(this, context.getSender()));
		return true;
	}

	private static void handleMessage(InventoryInteractionMessage msg, @Nullable ServerPlayer sender) {
		if (sender == null) {
			return;
		}
		PlayerInventoryProvider.get().runOnBackpacks(sender, (backpack, inventoryName, identifier, slot) -> {
			InventoryInteractionHelper.tryInventoryInteraction(msg.pos, sender.level, backpack, msg.face, sender);
			sender.swing(InteractionHand.MAIN_HAND, true);
			return true;
		});
	}
}
