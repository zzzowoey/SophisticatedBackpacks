package net.p3pp3rf1y.sophisticatedbackpacks.common.components;

import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import io.github.fabricators_of_create.porting_lib.transfer.item.SlotExposedStorage;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlockEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedcore.common.components.IComponentWrapper;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import team.reborn.energy.api.EnergyStorage;

import static net.p3pp3rf1y.sophisticatedcore.common.components.Components.ENERGY;
import static net.p3pp3rf1y.sophisticatedcore.common.components.Components.ITEM_HANDLER;

public class Components implements ItemComponentInitializer, BlockComponentInitializer {
	public static final ComponentKey<IBackpackWrapper> BACKPACK_WRAPPER = ComponentRegistry.getOrCreate(SophisticatedBackpacks.getRL("backpack_wrapper_component"), IBackpackWrapper.class);

	@Override
	public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
		registry.register((item) -> item instanceof BackpackItem, BACKPACK_WRAPPER, BackpackWrapper::new);

		registry.registerTransient((item) -> item instanceof BackpackItem, ITEM_HANDLER, Components::createItemHandlerComponent);
		//registry.registerTransient((item) -> item instanceof BackpackItem, FLUID_HANDLER_ITEM, Components::createFluidComponent);
		registry.registerTransient((item) -> item instanceof BackpackItem, ENERGY, Components::createEnergyComponent);
	}

	@Override
	public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
		registry.registerFor(BackpackBlockEntity.class, ITEM_HANDLER, Components::createItemHandlerComponent);
		//registry.registerFor(BackpackBlockEntity.class, FLUID_HANDLER_ITEM, Components::createFluidComponent);
		registry.registerFor(BackpackBlockEntity.class, ENERGY, Components::createEnergyComponent);
	}

	private static IComponentWrapper.SimpleComponentWrapper<SlotExposedStorage, ItemStack> createItemHandlerComponent(ItemStack itemStack) {
		return new IComponentWrapper.SimpleComponentWrapper<>(itemStack) {
			@Override
			public LazyOptional<SlotExposedStorage> getWrapped() {
				if (wrapped == null) {
					wrapped = IBackpackWrapper.maybeGet(this.object).lazyMap(IStorageWrapper::getInventoryForInputOutput);
				}
				return wrapped;
			}
		};
	}
	private static IComponentWrapper.SimpleComponentWrapper<SlotExposedStorage, BackpackBlockEntity> createItemHandlerComponent(BackpackBlockEntity entity) {
		return new IComponentWrapper.SimpleComponentWrapper<>(entity) {
			@Override
			public LazyOptional<SlotExposedStorage> getWrapped() {
				if (wrapped == null) {
					wrapped =  LazyOptional.of(object.getBackpackWrapper()::getInventoryForInputOutput);
				}
				return wrapped;
			}
		};
	}


/*	private static IComponentWrapper<FluidHandler, ItemStack> createFluidComponent(ItemStack itemStack) {
		if (Boolean.TRUE.equals(Config.SERVER.itemFluidHandlerEnabled.get())) {
			return new IComponentWrapper.SimpleComponentWrapper<>(itemStack) {
				@Override
				public LazyOptional<SlotExposedStorage> getWrapped() {
					if (wrapped == null) {
						wrapped = LazyOptional.fromOptional(IBackpackWrapper.get(this.object).getFluidHandler().map(FluidHandler.class::cast));
					}
					return wrapped;
				}
			};
		}

		return IComponentWrapper.empty();
	}*/
/*	private static IComponentWrapper<FluidHandler, BackpackBlockEntity> createFluidComponent(BackpackBlockEntity entity) {
		return new IComponentWrapper.SimpleComponentWrapper<>(entity) {
			@Override
			public LazyOptional<SlotExposedStorage> getWrapped() {
				if (wrapped == null) {
					wrapped = LazyOptional.fromOptional(object.getBackpackWrapper().getFluidHandler().map(FluidHandler.class::cast));
				}
				return wrapped;
			}
		};
	}*/

	private static IComponentWrapper.SimpleComponentWrapper<EnergyStorage, ItemStack> createEnergyComponent(ItemStack itemStack) {
		return new IComponentWrapper.SimpleComponentWrapper<>(itemStack) {
			@Override
			public LazyOptional<EnergyStorage> getWrapped() {
				if (wrapped == null) {
					wrapped = LazyOptional.fromOptional(IBackpackWrapper.get(this.object).getEnergyStorage());
				}
				return wrapped;
			}
		};
	}
	private static IComponentWrapper.SimpleComponentWrapper<EnergyStorage, BackpackBlockEntity> createEnergyComponent(BackpackBlockEntity entity) {
		return new IComponentWrapper.SimpleComponentWrapper<>(entity) {
			@Override
			public LazyOptional<EnergyStorage> getWrapped() {
				if (wrapped == null) {
					wrapped = LazyOptional.fromOptional(object.getBackpackWrapper().getEnergyStorage());
				}
				return wrapped;
			}
		};
	}
}
