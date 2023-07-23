package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.common.lookup.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.network.RequestBackpackInventoryContentsMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.network.SBPPacketHandler;
import net.p3pp3rf1y.sophisticatedcore.client.render.ClientStorageContentsTooltip;

import java.util.UUID;

public class ClientBackpackContentsTooltip extends ClientStorageContentsTooltip {
	private final ItemStack backpack;

	public static void onWorldLoad(Minecraft client, ClientLevel world) {
		refreshContents();
		lastRequestTime = 0;
	}

	@Override
	public void renderImage(Font font, int leftX, int topY, PoseStack poseStack, ItemRenderer itemRenderer) {
		BackpackWrapperLookup.maybeGet(backpack).ifPresent(wrapper -> renderTooltip(wrapper, font, leftX, topY, poseStack, itemRenderer));
	}

	public ClientBackpackContentsTooltip(BackpackItem.BackpackContentsTooltip tooltip) {
		backpack = tooltip.getBackpack();
	}

	@Override
	protected void sendInventorySyncRequest(UUID uuid) {
		SBPPacketHandler.sendToServer(new RequestBackpackInventoryContentsMessage(uuid));
	}
}
