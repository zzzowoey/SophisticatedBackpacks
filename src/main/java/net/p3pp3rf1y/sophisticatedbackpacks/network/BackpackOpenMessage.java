package net.p3pp3rf1y.sophisticatedbackpacks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.InventoryMenu;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContext;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.IContextAwareContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;
import net.p3pp3rf1y.sophisticatedcore.util.MenuProviderHelper;

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
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if (player == null) {
				return;
			}

			if (player.containerMenu instanceof BackpackContainer backpackContainer) {
				BackpackContext backpackContext = backpackContainer.getBackpackContext();
				if (slotIndex == -1) {
					openBackpack(player, backpackContext.getParentBackpackContext());
				} else if (backpackContainer.isStorageInventorySlot(slotIndex)) {
					openBackpack(player, backpackContext.getSubBackpackContext(slotIndex));
				}
			} else if (player.containerMenu instanceof IContextAwareContainer contextAwareContainer) {
				BackpackContext backpackContext = contextAwareContainer.getBackpackContext();
				openBackpack(player, backpackContext);
			} else if (slotIndex > -1 && player.containerMenu instanceof InventoryMenu) {
				int slotIndex1 = slotIndex;
				String inventoryProvider = PlayerInventoryProvider.MAIN_INVENTORY;
				if (slotIndex == CHEST_SLOT) {
					inventoryProvider = PlayerInventoryProvider.ARMOR_INVENTORY;
				} else if (slotIndex == OFFHAND_SLOT) {
					inventoryProvider = PlayerInventoryProvider.OFFHAND_INVENTORY;
					slotIndex1 = 0;
				}

				BackpackContext.Item backpackContext = new BackpackContext.Item(inventoryProvider, identifier, slotIndex1, true);
				openBackpack(player, backpackContext);
			} else {
				PlayerInventoryProvider.get().runOnBackpacks(player, (backpack, inventoryName, identifier1, slot) -> {
					BackpackContext.Item backpackContext = new BackpackContext.Item(inventoryName, identifier1, slot);
					player.openMenu(MenuProviderHelper.createMenuProvider((w, bpc, pl) -> new BackpackContainer(w, pl, backpackContext), backpackContext, backpack.getHoverName()));
					return true;
				});
			}
		});
		return true;
	}

	private static void openBackpack(ServerPlayer player, BackpackContext backpackContext) {
		player.openMenu(MenuProviderHelper.createMenuProvider((w, bpc, pl) -> new BackpackContainer(w, pl, backpackContext), backpackContext, backpackContext.getDisplayName(player)));
	}
}
