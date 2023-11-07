package net.p3pp3rf1y.sophisticatedbackpacks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;

import java.util.Iterator;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
    @Inject(method = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;isWearingGold(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private static void sophisticatedbackpacks$isWearingGold(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir, Iterable iterable, Iterator var2, ItemStack itemStack) {
        if (itemStack.getItem() == ModItems.GOLD_BACKPACK) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
