package net.p3pp3rf1y.sophisticatedbackpacks.api;

import io.github.fabricators_of_create.porting_lib.transfer.item.SlotExposedStorage;
import net.minecraft.world.entity.player.Player;

public interface IItemHandlerInteractionUpgrade {
	void onHandlerInteract(SlotExposedStorage itemHandler, Player player);
}
