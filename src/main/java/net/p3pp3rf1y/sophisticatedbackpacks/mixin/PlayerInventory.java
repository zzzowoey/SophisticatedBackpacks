package net.p3pp3rf1y.sophisticatedbackpacks.mixin;

import dev.emi.trinkets.api.TrinketsApi;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


@Mixin(Inventory.class)
public class PlayerInventory {
    @Shadow @Final public NonNullList<ItemStack> armor;

    @Shadow @Final public Player player;

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void sophisticatedbackpacks$tick(CallbackInfo ci) {
        armor.forEach(a -> a.onArmorTick(player.level, player));
        // Additional call into trinkets for armor ticks needed
        TrinketsApi.getTrinketComponent(player).ifPresent(comp -> comp.forEach(((slotReference, stack) -> stack.onArmorTick(player.level, player))));
    }
}
