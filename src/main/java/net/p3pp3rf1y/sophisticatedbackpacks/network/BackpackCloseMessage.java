package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;

import javax.annotation.Nullable;

@SuppressWarnings("java:S1118")
public class BackpackCloseMessage  extends SimplePacketBase {
	public BackpackCloseMessage() {}

	public BackpackCloseMessage(FriendlyByteBuf buffer) {}

	@Override
	public void write(FriendlyByteBuf buffer) {
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> handleMessage(context.getSender()));
		return true;
	}

	private static void handleMessage(@Nullable ServerPlayer player) {
		if (player == null) {
			return;
		}

		if (player.containerMenu instanceof BackpackContainer) {
			player.closeContainer();
		}
	}
}
