package net.p3pp3rf1y.sophisticatedbackpacks.common.lookup;

import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlockEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.common.lookup.ItemStorage;
import team.reborn.energy.api.EnergyStorage;

import java.util.Optional;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BACKPACKS;

public class BackpackWrapperLookup {
    public static final ItemApiLookup<IBackpackWrapper, Void> ITEM = ItemApiLookup.get(SophisticatedBackpacks.getRL("item_backpack_wrapper"), IBackpackWrapper.class, Void.class);

    public static Optional<IBackpackWrapper> get(ItemStack provider) {
        return Optional.ofNullable(ITEM.find(provider, null));
    }

    private static boolean isBlockAllowed(BackpackBlockEntity blockEntity, Direction side) {
        return side == null || blockEntity.getLevel() == null || !Config.SERVER.noConnectionBlocks.isBlockConnectionDisallowed(blockEntity.getLevel().getBlockState(blockEntity.getBlockPos().relative(side)).getBlock());
    }

    static {
        BackpackItem[] backpacks = BACKPACKS.stream().map(RegistryObject::get).toList().toArray(new BackpackItem[0]);

        BackpackWrapperLookup.ITEM.registerForItems((itemStack, context) -> IBackpackWrapper.of(itemStack), backpacks);
        ItemStorage.ITEM.registerForItems((itemStack, context) -> get(itemStack).map(IStorageWrapper::getInventoryForInputOutput).orElse(null), backpacks);

        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> {
            if (!isBlockAllowed(blockEntity, direction))
                return null;

            return blockEntity.getBackpackWrapper().getInventoryForInputOutput();
        }, ModBlocks.BACKPACK_TILE_TYPE.get());
        FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> {
            if (!isBlockAllowed(blockEntity, direction))
                return null;

            return blockEntity.getBackpackWrapper().getFluidHandler().orElse(null);
        }, ModBlocks.BACKPACK_TILE_TYPE.get());
        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> {
            if (!isBlockAllowed(blockEntity, direction))
                return null;

            return blockEntity.getBackpackWrapper().getEnergyStorage().orElse(null);
        }, ModBlocks.BACKPACK_TILE_TYPE.get());
    }
}
