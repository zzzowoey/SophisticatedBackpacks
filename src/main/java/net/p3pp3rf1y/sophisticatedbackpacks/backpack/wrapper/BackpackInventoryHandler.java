package net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IInsertResponseUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.settings.memory.MemorySettingsCategory;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.inception.InceptionUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.util.IItemHandlerSimpleInserter;
import net.p3pp3rf1y.sophisticatedbackpacks.util.ISlotTracker;
import net.p3pp3rf1y.sophisticatedbackpacks.util.InventoryHandlerSlotTracker;
import net.p3pp3rf1y.sophisticatedbackpacks.util.InventoryHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;

public class BackpackInventoryHandler extends ItemStackHandler implements IItemHandlerSimpleInserter {
	public static final String INVENTORY_TAG = "inventory";
	private static final String REAL_COUNT_TAG = "realCount";
	private final IBackpackWrapper backpackWrapper;
	private final CompoundTag contentsNbt;
	private final Runnable backpackSaveHandler;
	private final List<IntConsumer> onContentsChangedListeners = new ArrayList<>();
	private boolean persistent = true;
	private final Map<Integer, CompoundTag> stackNbts = new LinkedHashMap<>();

	private ISlotTracker slotTracker = new ISlotTracker.Noop();

	private int slotLimit;
	private int maxStackSizeMultiplier;
	private boolean isInitializing;

	public BackpackInventoryHandler(int numberOfInventorySlots, IBackpackWrapper backpackWrapper, CompoundTag contentsNbt, Runnable backpackSaveHandler, int slotLimit) {
		super(numberOfInventorySlots);
		isInitializing = true;
		this.backpackWrapper = backpackWrapper;
		this.contentsNbt = contentsNbt;
		this.backpackSaveHandler = backpackSaveHandler;
		setSlotLimit(slotLimit);
		deserializeNBT(contentsNbt.getCompound(INVENTORY_TAG));
		initStackNbts();
		isInitializing = false;
	}

	public ISlotTracker getSlotTracker() {
		initSlotTracker();
		return slotTracker;
	}

	@Override
	public void setSize(int size) {
		super.setSize(stacks.size());
	}

	private void initStackNbts() {
		for (int slot = 0; slot < stacks.size(); slot++) {
			ItemStack slotStack = stacks.get(slot);
			if (!slotStack.isEmpty()) {
				stackNbts.put(slot, getSlotsStackNbt(slot, slotStack));
			}
		}
	}

	@Override
	public void onContentsChanged(int slot) {
		super.onContentsChanged(slot);
		if (persistent && updateSlotNbt(slot)) {
			saveInventory();
			for (IntConsumer onContentsChangedListener : onContentsChangedListeners) {
				onContentsChangedListener.accept(slot);
			}
		}
	}

	@SuppressWarnings("java:S3824")
	//compute use here would be difficult as then there's no way of telling that value was newly created vs different than the one that needs to be set
	private boolean updateSlotNbt(int slot) {
		ItemStack slotStack = getStackInSlot(slot);
		if (slotStack.isEmpty()) {
			if (stackNbts.containsKey(slot)) {
				stackNbts.remove(slot);
				return true;
			}
		} else {
			CompoundTag itemTag = getSlotsStackNbt(slot, slotStack);
			if (!stackNbts.containsKey(slot) || !stackNbts.get(slot).equals(itemTag)) {
				stackNbts.put(slot, itemTag);
				return true;
			}
		}
		return false;
	}

	private CompoundTag getSlotsStackNbt(int slot, ItemStack slotStack) {
		CompoundTag itemTag = new CompoundTag();
		itemTag.putInt("Slot", slot);
		itemTag.putInt(REAL_COUNT_TAG, slotStack.getCount());
		slotStack.save(itemTag);
		return itemTag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		slotTracker.clear();
		setSize(nbt.contains("Size", Constants.NBT.TAG_INT) ? nbt.getInt("Size") : stacks.size());
		ListTag tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundTag itemTags = tagList.getCompound(i);
			int slot = itemTags.getInt("Slot");

			if (slot >= 0 && slot < stacks.size()) {
				ItemStack slotStack = ItemStack.of(itemTags);
				if (itemTags.contains(REAL_COUNT_TAG)) {
					slotStack.setCount(itemTags.getInt(REAL_COUNT_TAG));
				}
				stacks.set(slot, slotStack);
			}
		}
		slotTracker.refreshSlotIndexesFrom(this);
		onLoad();
	}

	@Override
	public int getSlotLimit(int slot) {
		return slotLimit;
	}

	@Override
	public int getStackLimit(int slot, ItemStack stack) {
		int adjustedMaxStackSizeMultiplier = maxStackSizeMultiplier > 1 && Config.COMMON.stackUpgrade.canItemStack(stack.getItem()) ? maxStackSizeMultiplier : 1;

		return Math.min(slotLimit, stack.getMaxStackSize() * adjustedMaxStackSizeMultiplier);
	}

	public void setSlotLimit(int slotLimit) {
		this.slotLimit = slotLimit;
		maxStackSizeMultiplier = slotLimit / 64;

		if (!isInitializing) {
			slotTracker.refreshSlotIndexesFrom(this);
		}
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0) {
			return ItemStack.EMPTY;
		}

		validateSlotIndex(slot);
		ItemStack existing = stacks.get(slot);

		if (existing.isEmpty()) {
			return ItemStack.EMPTY;
		}

		if (existing.getCount() <= amount) {
			if (!simulate) {
				stacks.set(slot, ItemStack.EMPTY);
				slotTracker.removeAndSetSlotIndexes(this, slot, ItemStack.EMPTY);
				onContentsChanged(slot);
				return existing;
			} else {
				return existing.copy();
			}
		} else {
			if (!simulate) {
				ItemStack newStack = ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - amount);
				stacks.set(slot, newStack);
				slotTracker.removeAndSetSlotIndexes(this, slot, newStack);
				onContentsChanged(slot);
			}

			return ItemHandlerHelper.copyStackWithSize(existing, amount);
		}
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		initSlotTracker();
		return slotTracker.insertItemIntoHandler(this, this::insertItemInternal, slot, stack, simulate);
	}

	private void initSlotTracker() {
		if (!(slotTracker instanceof InventoryHandlerSlotTracker)) {
			slotTracker = new InventoryHandlerSlotTracker(backpackWrapper.getSettingsHandler().getTypeCategory(MemorySettingsCategory.class));
			slotTracker.refreshSlotIndexesFrom(this);
		}
	}

	private ItemStack insertItemInternal(int slot, ItemStack stack, boolean simulate) {
		ItemStack ret = runOnBeforeInsert(slot, stack, simulate, this, backpackWrapper);
		if (ret.isEmpty()) {
			return ret;
		}

		ret = super.insertItem(slot, ret, simulate);

		if (ret == stack) {
			return ret;
		}

		runOnAfterInsert(slot, simulate, this, backpackWrapper);

		return ret;
	}

	private void runOnAfterInsert(int slot, boolean simulate, IItemHandlerSimpleInserter handler, IBackpackWrapper backpackWrapper) {
		if (!simulate) {
			backpackWrapper.getUpgradeHandler().getWrappersThatImplementFromMainBackpack(IInsertResponseUpgrade.class).forEach(u -> u.onAfterInsert(handler, slot));
		}
	}

	private ItemStack runOnBeforeInsert(int slot, ItemStack stack, boolean simulate, IItemHandlerSimpleInserter handler, IBackpackWrapper backpackWrapper) {
		List<IInsertResponseUpgrade> wrappers = backpackWrapper.getUpgradeHandler().getWrappersThatImplementFromMainBackpack(IInsertResponseUpgrade.class);
		ItemStack remaining = stack;
		for (IInsertResponseUpgrade upgrade : wrappers) {
			remaining = upgrade.onBeforeInsert(handler, slot, remaining, simulate);
			if (remaining.isEmpty()) {
				return ItemStack.EMPTY;
			}
		}
		return remaining;
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		super.setStackInSlot(slot, stack);
		slotTracker.removeAndSetSlotIndexes(this, slot, stack);
	}

	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return (!(stack.getItem() instanceof BackpackItem) || (hasInceptionUpgrade() && isBackpackWithoutInceptionUpgrade(stack)))
				&& backpackWrapper.getSettingsHandler().getTypeCategory(MemorySettingsCategory.class).matchesFilter(slot, stack);
	}

	private boolean hasInceptionUpgrade() {
		return backpackWrapper.getUpgradeHandler().hasUpgrade(InceptionUpgradeItem.TYPE);
	}

	private boolean isBackpackWithoutInceptionUpgrade(ItemStack stack) {
		return (stack.getItem() instanceof BackpackItem) && !stack.getCapability(CapabilityBackpackWrapper.getCapabilityInstance())
				.map(w -> w.getUpgradeHandler().hasUpgrade(InceptionUpgradeItem.TYPE)).orElse(false);
	}

	public void saveInventory() {
		contentsNbt.put(INVENTORY_TAG, serializeNBT());
		backpackSaveHandler.run();
	}

	public void copyStacksTo(BackpackInventoryHandler otherHandler) {
		InventoryHelper.copyTo(this, otherHandler);
	}

	public void addListener(IntConsumer onContentsChanged) {
		onContentsChangedListeners.add(onContentsChanged);
	}

	public void clearListeners() {
		onContentsChangedListeners.clear();
	}

	@Override
	public CompoundTag serializeNBT() {
		ListTag nbtTagList = new ListTag();
		nbtTagList.addAll(stackNbts.values());
		CompoundTag nbt = new CompoundTag();
		nbt.put("Items", nbtTagList);
		nbt.putInt("Size", getSlots());
		return nbt;
	}

	public int getStackSizeMultiplier() {
		return maxStackSizeMultiplier;
	}

	@Override
	public ItemStack insertItem(ItemStack stack, boolean simulate) {
		initSlotTracker();
		return slotTracker.insertItemIntoHandler(this, this::insertItemInternal, stack, simulate);
	}
}
