package net.p3pp3rf1y.sophisticatedbackpacks.network;

import io.github.fabricators_of_create.porting_lib.util.NetworkDirection;
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
import net.p3pp3rf1y.sophisticatedcore.network.PacketHandler;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;

import java.util.function.Function;

import static io.github.fabricators_of_create.porting_lib.util.NetworkDirection.PLAY_TO_CLIENT;
import static io.github.fabricators_of_create.porting_lib.util.NetworkDirection.PLAY_TO_SERVER;

public class SBPPacketHandler {
	public static final ResourceLocation CHANNEL_NAME = SophisticatedBackpacks.getRL("channel");
	private static SimpleChannel channel;

	public static void init() {
		channel = new SimpleChannel(CHANNEL_NAME);

		registerMessage(BackpackOpenMessage.class, BackpackOpenMessage::new, PLAY_TO_SERVER);
		registerMessage(UpgradeToggleMessage.class, UpgradeToggleMessage::new, PLAY_TO_SERVER);
		registerMessage(RequestBackpackInventoryContentsMessage.class, RequestBackpackInventoryContentsMessage::new, PLAY_TO_SERVER);
		registerMessage(BackpackContentsMessage.class, BackpackContentsMessage::new, PLAY_TO_CLIENT);
		registerMessage(InventoryInteractionMessage.class, InventoryInteractionMessage::new, PLAY_TO_SERVER);
		registerMessage(BlockToolSwapMessage.class, BlockToolSwapMessage::new, PLAY_TO_SERVER);
		registerMessage(EntityToolSwapMessage.class, EntityToolSwapMessage::new, PLAY_TO_SERVER);
		registerMessage(BackpackCloseMessage.class, BackpackCloseMessage::new, PLAY_TO_SERVER);
		registerMessage(SyncClientInfoMessage.class, SyncClientInfoMessage::new, PLAY_TO_CLIENT);
		registerMessage(AnotherPlayerBackpackOpenMessage.class, AnotherPlayerBackpackOpenMessage::new, PLAY_TO_SERVER);
		registerMessage(BlockPickMessage.class, BlockPickMessage::new, PLAY_TO_SERVER);
	}

	public static <T extends SimplePacketBase> void registerMessage(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
		PacketType<T> packet = new PacketType<>(type, factory, direction);
		packet.register();
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

	public static class PacketType<T extends SimplePacketBase> {
		private static int index = 0;

		private Function<FriendlyByteBuf, T> decoder;
		private Class<T> type;
		private NetworkDirection direction;

		public PacketType(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
			decoder = factory;
			this.type = type;
			this.direction = direction;
		}

		public void register() {
			switch (direction) {
				case PLAY_TO_CLIENT -> getChannel().registerS2CPacket(type, index++, decoder);
				case PLAY_TO_SERVER -> getChannel().registerC2SPacket(type, index++, decoder);
			}
		}
	}
}
