package net.p3pp3rf1y.sophisticatedbackpacks.backpack;

import io.github.fabricators_of_create.porting_lib.block.ChunkUnloadListeningBlockEntity;
import io.github.fabricators_of_create.porting_lib.block.CustomDataPacketHandlingBlockEntity;
import io.github.fabricators_of_create.porting_lib.extensions.extensions.BlockEntityExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.p3pp3rf1y.sophisticatedbackpacks.common.components.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.controller.ControllerBlockEntityBase;
import net.p3pp3rf1y.sophisticatedcore.controller.IControllableStorage;
import net.p3pp3rf1y.sophisticatedcore.renderdata.RenderInfo;
import net.p3pp3rf1y.sophisticatedcore.renderdata.TankPosition;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ITickableUpgrade;
import net.p3pp3rf1y.sophisticatedcore.util.WorldHelper;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

import static net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock.*;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.BACKPACK_TILE_TYPE;
import static net.p3pp3rf1y.sophisticatedcore.common.components.Components.*;
import static net.p3pp3rf1y.sophisticatedcore.common.components.Components.ITEM_HANDLER;

public class BackpackBlockEntity extends BlockEntity implements IControllableStorage, BlockEntityExtensions, CustomDataPacketHandlingBlockEntity, ChunkUnloadListeningBlockEntity {
	@Nullable
	private BlockPos controllerPos = null;
	private IBackpackWrapper backpackWrapper = IBackpackWrapper.Noop.INSTANCE;
	private boolean updateBlockRender = true;

	private boolean chunkBeingUnloaded = false;

	public BackpackBlockEntity(BlockPos pos, BlockState state) {
		super(BACKPACK_TILE_TYPE.get(), pos, state);
	}

	public void setBackpack(ItemStack backpack) {
		backpackWrapper = IBackpackWrapper.maybeGet(backpack).orElse(IBackpackWrapper.Noop.INSTANCE);
		backpackWrapper.setSaveHandler(() -> {
			setChanged();
			updateBlockRender = false;
			WorldHelper.notifyBlockUpdate(this);
		});
		backpackWrapper.setInventorySlotChangeHandler(this::setChanged);
		backpackWrapper.setUpgradeCachesInvalidatedHandler(this::invalidateBackpackCaps);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		setBackpackFromNbt(tag);
		loadControllerPos(tag);

		if (level != null && !level.isClientSide()) {
			removeControllerPos();
			tryToAddToController();
		}

		WorldHelper.notifyBlockUpdate(this);
	}



	@Override
	public void onLoad() {
		registerWithControllerOnLoad();
	}

	private void setBackpackFromNbt(CompoundTag nbt) {
		setBackpack(ItemStack.of(nbt.getCompound("backpackData")));
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		writeBackpack(tag);
		saveControllerPos(tag);
	}

	private void writeBackpack(CompoundTag ret) {
		ItemStack backpackCopy = backpackWrapper.getBackpack().copy();
		backpackCopy.setTag(backpackCopy.getTag());
		ret.put("backpackData", backpackCopy.save(new CompoundTag()));
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag ret = super.getUpdateTag();
		writeBackpack(ret);
		ret.putBoolean("updateBlockRender", updateBlockRender);
		updateBlockRender = true;
		return ret;
	}

	@Nullable
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		CompoundTag tag = pkt.getTag();
		if (tag == null) {
			return;
		}

		setBackpackFromNbt(tag);
		if (tag.getBoolean("updateBlockRender")) {
			WorldHelper.notifyBlockUpdate(this);
		}
	}

	public IBackpackWrapper getBackpackWrapper() {
		return backpackWrapper;
	}

	@Override
	public void invalidateCaps() {
		invalidateBackpackCaps();
	}

	private void invalidateBackpackCaps() {
		getComponent(ITEM_HANDLER).invalidate();
	}

	public void refreshRenderState() {
		BlockState state = getBlockState();
		state = state.setValue(LEFT_TANK, false);
		state = state.setValue(RIGHT_TANK, false);
		RenderInfo renderInfo = backpackWrapper.getRenderInfo();
		for (TankPosition pos : renderInfo.getTankRenderInfos().keySet()) {
			if (pos == TankPosition.LEFT) {
				state = state.setValue(LEFT_TANK, true);
			} else if (pos == TankPosition.RIGHT) {
				state = state.setValue(RIGHT_TANK, true);
			}
		}
		state = state.setValue(BATTERY, renderInfo.getBatteryRenderInfo().isPresent());
		Level l = Objects.requireNonNull(level);
		l.setBlockAndUpdate(worldPosition, state);
		l.updateNeighborsAt(worldPosition, state.getBlock());
		WorldHelper.notifyBlockUpdate(this);
	}

	public static void serverTick(Level level, BlockPos blockPos, BackpackBlockEntity backpackBlockEntity) {
		backpackBlockEntity.backpackWrapper.getUpgradeHandler().getWrappersThatImplement(ITickableUpgrade.class).forEach(upgrade -> upgrade.tick(null, level, blockPos));
	}

	@Override
	public IStorageWrapper getStorageWrapper() {
		return backpackWrapper;
	}

	@Override
	public void setControllerPos(BlockPos controllerPos) {
		this.controllerPos = controllerPos;
		setChanged();
	}

	@Override
	public Optional<BlockPos> getControllerPos() {
		return Optional.ofNullable(controllerPos);
	}

	@Override
	public void removeControllerPos() {
		controllerPos = null;
	}

	@Override
	public BlockPos getStorageBlockPos() {
		return getBlockPos();
	}

	@Override
	public Level getStorageBlockLevel() {
		return Objects.requireNonNull(getLevel());
	}

	@Override
	public boolean canConnectStorages() {
		return false;
	}

	@Override
	public void unregisterController() {
		IControllableStorage.super.unregisterController();
		backpackWrapper.unregisterOnSlotsChangeListener();
		backpackWrapper.unregisterOnInventoryHandlerRefreshListener();
	}

	@Override
	public void registerController(ControllerBlockEntityBase controllerBlockEntity) {
		IControllableStorage.super.registerController(controllerBlockEntity);
		backpackWrapper.registerOnSlotsChangeListener(this::changeSlots);
		backpackWrapper.registerOnInventoryHandlerRefreshListener(this::registerInventoryStackListeners);
	}

	@Override
	public void onChunkUnloaded() {
		chunkBeingUnloaded = true;
	}

	@Override
	public void setRemoved() {
		if (!chunkBeingUnloaded && level != null) {
			removeFromController();
		}
		super.setRemoved();
	}
}
