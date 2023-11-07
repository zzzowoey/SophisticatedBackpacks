package net.p3pp3rf1y.sophisticatedbackpacks.network;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;

import java.util.function.Function;

public class SBPPacketHandler {
	private static int index = 0;
	private static SimpleChannel channel;

	public static final ResourceLocation CHANNEL_NAME = SophisticatedBackpacks.getRL("channel");

	public static void init() {
		channel = new SimpleChannel(CHANNEL_NAME);

		registerC2SMessage(BackpackOpenMessage.class, BackpackOpenMessage::new);
		registerC2SMessage(UpgradeToggleMessage.class, UpgradeToggleMessage::new);
		registerC2SMessage(RequestBackpackInventoryContentsMessage.class, RequestBackpackInventoryContentsMessage::new);
		registerC2SMessage(InventoryInteractionMessage.class, InventoryInteractionMessage::new);
		registerC2SMessage(BlockToolSwapMessage.class, BlockToolSwapMessage::new);
		registerC2SMessage(EntityToolSwapMessage.class, EntityToolSwapMessage::new);
		registerC2SMessage(BackpackCloseMessage.class, BackpackCloseMessage::new);
		registerC2SMessage(AnotherPlayerBackpackOpenMessage.class, AnotherPlayerBackpackOpenMessage::new);
		registerC2SMessage(BlockPickMessage.class, BlockPickMessage::new);

		registerS2CMessage(BackpackContentsMessage.class, BackpackContentsMessage::new);
		registerS2CMessage(SyncClientInfoMessage.class, SyncClientInfoMessage::new);
	}

	public static <T extends SimplePacketBase> void registerC2SMessage(Class<T> type, Function<FriendlyByteBuf, T> factory) {
		getChannel().registerC2SPacket(type, index++, factory);
	}
	public static <T extends SimplePacketBase> void registerS2CMessage(Class<T> type, Function<FriendlyByteBuf, T> factory) {
		getChannel().registerS2CPacket(type, index++, factory);
	}

	public static SimpleChannel getChannel() {
		return channel;
	}

	public static void sendToServer(Object message) {
		getChannel().sendToServer((C2SPacket) message);
	}

	public static void sendToClient(ServerPlayer player, Object message) {
		getChannel().sendToClient((S2CPacket) message, player);
	}

	public static void sendToAllNear(ServerLevel world, BlockPos pos, int range, Object message) {
		getChannel().sendToClientsAround((S2CPacket) message, world, pos, range);
	}
	public static void sendToAllNear(ServerLevel world, Vec3 pos, int range, Object message) {
		getChannel().sendToClientsAround((S2CPacket) message, world, pos, range);
	}

}
