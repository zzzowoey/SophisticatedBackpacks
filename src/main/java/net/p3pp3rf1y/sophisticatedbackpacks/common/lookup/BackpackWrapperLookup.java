package net.p3pp3rf1y.sophisticatedbackpacks.common.lookup;

import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.common.lookup.ItemStorage;
import team.reborn.energy.api.EnergyStorage;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BACKPACKS;

public class BackpackWrapperLookup {
    public static final ItemApiLookup<IBackpackWrapper, Void> ITEM = ItemApiLookup.get(SophisticatedBackpacks.getRL("item_backpack_wrapper"), IBackpackWrapper.class, Void.class);

    public static LazyOptional<IBackpackWrapper> maybeGet(ItemStack provider) {
        return LazyOptional.ofObject(ITEM.find(provider, null));
    }

    static {
        BackpackItem[] backpacks = BACKPACKS.stream().map(RegistryObject::get).toList().toArray(new BackpackItem[0]);

        BackpackWrapperLookup.ITEM.registerForItems((itemStack, context) -> IBackpackWrapper.of(itemStack), backpacks);

        ItemStorage.ITEM.registerForItems((itemStack, context) -> maybeGet(itemStack).lazyMap(IStorageWrapper::getInventoryForInputOutput).orElseGet(null), backpacks);
        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getBackpackWrapper().getInventoryForInputOutput(), ModBlocks.BACKPACK_TILE_TYPE.get());

        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.getBackpackWrapper().getEnergyStorage().orElse(EnergyStorage.EMPTY), ModBlocks.BACKPACK_TILE_TYPE.get());
    }
}
