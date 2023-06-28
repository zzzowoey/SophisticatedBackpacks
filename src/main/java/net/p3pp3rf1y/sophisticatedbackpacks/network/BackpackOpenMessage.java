package net.p3pp3rf1y.sophisticatedbackpacks.network;

import io.github.fabricators_of_create.porting_lib.util.NetworkUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.InventoryMenu;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContext;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.IContextAwareContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;

import javax.annotation.Nullable;

public class BackpackOpenMessage extends SimplePacketBase {
	private static final int CHEST_SLOT = 38;
	private static final int OFFHAND_SLOT = 40;
	private final int slotIndex;
	private final String identifier;

	public BackpackOpenMessage() {
		this(-1);
	}

	public BackpackOpenMessage(int backpackSlot) {
		this(backpackSlot, "");
	}

	public BackpackOpenMessage(int backpackSlot, String identifier) {
		slotIndex = backpackSlot;
		this.identifier = identifier;
	}

	public BackpackOpenMessage(FriendlyByteBuf buffer) { this(buffer.readInt(), buffer.readUtf()); }

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(this.slotIndex);
		buffer.writeUtf(this.identifier);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> handleMessage(context.getSender(), this));
		return true;
	}

	private static void handleMessage(@Nullable ServerPlayer player, BackpackOpenMessage msg) {
		if (player == null) {
			return;
		}

		if (player.containerMenu instanceof BackpackContainer backpackContainer) {
			BackpackContext backpackContext = backpackContainer.getBackpackContext();
			if (msg.slotIndex == -1) {
				openBackpack(player, backpackContext.getParentBackpackContext());
			} else if (backpackContainer.isStorageInventorySlot(msg.slotIndex)) {
				openBackpack(player, backpackContext.getSubBackpackContext(msg.slotIndex));
			}
		} else if (player.containerMenu instanceof IContextAwareContainer contextAwareContainer) {
			BackpackContext backpackContext = contextAwareContainer.getBackpackContext();
			openBackpack(player, backpackContext);
		} else if (msg.slotIndex > -1 && player.containerMenu instanceof InventoryMenu) {
			int slotIndex = msg.slotIndex;
			String inventoryProvider = PlayerInventoryProvider.MAIN_INVENTORY;
			if (msg.slotIndex == CHEST_SLOT) {
				inventoryProvider = PlayerInventoryProvider.ARMOR_INVENTORY;
			} else if (msg.slotIndex == OFFHAND_SLOT) {
				inventoryProvider = PlayerInventoryProvider.OFFHAND_INVENTORY;
				slotIndex = 0;
			}

			BackpackContext.Item backpackContext = new BackpackContext.Item(inventoryProvider, msg.identifier, slotIndex, true);
			openBackpack(player, backpackContext);
		} else {
			findAndOpenFirstBackpack(player);
		}
	}

	private static void findAndOpenFirstBackpack(ServerPlayer player) {
		PlayerInventoryProvider.get().runOnBackpacks(player, (backpack, inventoryName, identifier, slot) -> {
			BackpackContext.Item backpackContext = new BackpackContext.Item(inventoryName, identifier, slot);
			NetworkUtil.openGui(player, new SimpleMenuProvider((w, p, pl) -> new BackpackContainer(w, pl, backpackContext), backpack.getHoverName()), backpackContext::toBuffer);
			return true;
		});
	}

	private static void openBackpack(ServerPlayer player, BackpackContext backpackContext) {
		NetworkUtil.openGui(player, new SimpleMenuProvider((w, p, pl) -> new BackpackContainer(w, pl, backpackContext), backpackContext.getDisplayName(player)), backpackContext::toBuffer);
	}
}
