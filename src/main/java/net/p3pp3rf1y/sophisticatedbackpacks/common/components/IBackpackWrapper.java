package net.p3pp3rf1y.sophisticatedbackpacks.common.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackSettingsHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.util.NoopStorageWrapper;

import java.util.UUID;
import java.util.function.IntConsumer;

import static net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacksComponents.BACKPACK_WRAPPER;

public interface IBackpackWrapper extends IStorageWrapper, Component {
	static IBackpackWrapper get(Object provider) {
		return BACKPACK_WRAPPER.get(provider);
	}
	static LazyOptional<IBackpackWrapper> maybeGet(Object provider) {
		return LazyOptional.ofObject(BACKPACK_WRAPPER.getNullable(provider));
	}

	@Override
	BackpackSettingsHandler getSettingsHandler();

	ItemStack getBackpack();

	ItemStack cloneBackpack();

	void copyDataTo(IStorageWrapper otherStorageWrapper);

	void setSlotNumbers(int numberOfInventorySlots, int numberOfUpgradeSlots);

	void setLoot(ResourceLocation lootTableName, float lootPercentage);

	void setContentsUuid(UUID storageUuid);

	default void removeContentsUuid() {
		//noop by default
	}

	default void removeContentsUUIDTag() {
		//noop
	}

	default void registerOnSlotsChangeListener(IntConsumer onSlotsChange) {
		//noop
	}

	default void unregisterOnSlotsChangeListener() {
		//noop
	}

	default void registerOnInventoryHandlerRefreshListener(Runnable onInventoryHandlerRefresh) {
		//noop
	}

	default void unregisterOnInventoryHandlerRefreshListener() {
		//noop
	}

	@Deprecated
	@Override
	default void readFromNbt(CompoundTag tag) {
		// NO-OP
	}

	@Deprecated
	@Override
	default void writeToNbt(CompoundTag tag) {
		// NO-OP
	}

	class Noop extends NoopStorageWrapper implements IBackpackWrapper {
		public static final Noop INSTANCE = new Noop();

		private final ItemStack backpack = new ItemStack(ModItems.BACKPACK.get());
		private final BackpackSettingsHandler settingsHandler = new BackpackSettingsHandler(this, new CompoundTag(), () -> {});

		@Override
		public BackpackSettingsHandler getSettingsHandler() {
			return settingsHandler;
		}

		@Override
		public ItemStack getBackpack() {
			return backpack;
		}

		@Override
		public ItemStack cloneBackpack() {
			return backpack;
		}

		@Override
		public void copyDataTo(IStorageWrapper otherStorageWrapper) {
			//noop
		}
	}
}
