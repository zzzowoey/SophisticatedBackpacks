package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBlockToolSwapUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.common.components.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlockToolSwapMessage extends SimplePacketBase {
	private final BlockPos pos;

	public BlockToolSwapMessage(BlockPos pos) {
		this.pos = pos;
	}

	public BlockToolSwapMessage(FriendlyByteBuf buffer) { this(buffer.readBlockPos()); }

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(this.pos);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> handleMessage(this, context.getSender()));
		return true;
	}

	private static void handleMessage(BlockToolSwapMessage msg, @Nullable ServerPlayer sender) {
		if (sender == null) {
			return;
		}
		AtomicBoolean result = new AtomicBoolean(false);
		AtomicBoolean anyUpgradeCanInteract = new AtomicBoolean(false);
		PlayerInventoryProvider.get().runOnBackpacks(sender, (backpack, inventoryName, identifier, slot) -> IBackpackWrapper.maybeGet(backpack)
				.map(backpackWrapper -> {
							backpackWrapper.getUpgradeHandler().getWrappersThatImplement(IBlockToolSwapUpgrade.class)
									.forEach(upgrade -> {
										if (!upgrade.canProcessBlockInteract() || result.get()) {
											return;
										}
										anyUpgradeCanInteract.set(true);

										result.set(upgrade.onBlockInteract(sender.level, msg.pos, sender.level.getBlockState(msg.pos), sender));
									});
							return result.get();
						}
				).orElse(false)
		);

		if (!anyUpgradeCanInteract.get()) {
			sender.displayClientMessage(Component.translatable("gui.sophisticatedbackpacks.status.no_tool_swap_upgrade_present"), true);
			return;
		}
		if (!result.get()) {
			sender.displayClientMessage(Component.translatable("gui.sophisticatedbackpacks.status.no_tool_found_for_block"), true);
		}
	}
}
