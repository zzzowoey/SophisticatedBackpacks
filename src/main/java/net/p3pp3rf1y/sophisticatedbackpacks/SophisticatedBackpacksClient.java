package net.p3pp3rf1y.sophisticatedbackpacks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.client.ClientEventHandler;
//import net.p3pp3rf1y.sophisticatedbackpacks.client.KeybindHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.ClientBackpackContentsTooltip;
import net.p3pp3rf1y.sophisticatedbackpacks.network.SBPPacketHandler;

public class SophisticatedBackpacksClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
/*		KeybindHandler.registerKeyMappings();
		KeybindHandler.register();*/
		ClientEventHandler.registerHandlers();

		TooltipComponentCallback.EVENT.register(SophisticatedBackpacksClient::registerTooltipComponent);

		SBPPacketHandler.getChannel().initClientListener();
	}
	private static ClientTooltipComponent registerTooltipComponent(TooltipComponent data) {
		if (data instanceof BackpackItem.BackpackContentsTooltip) {
			return new ClientBackpackContentsTooltip((BackpackItem.BackpackContentsTooltip) data);
		}
		return null;
	}

/*	private static void registerTooltipComponent(RegisterClientTooltipComponentFactoriesEvent event) {
		event.register(BackpackItem.BackpackContentsTooltip.class, ClientBackpackContentsTooltip::new);

	}*/
}
