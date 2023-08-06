package net.p3pp3rf1y.sophisticatedbackpacks.util;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContext;

import javax.annotation.Nullable;

public class MenuProviderHelper {
    public static ExtendedScreenHandlerFactory createMenuProvider(MenuConstructor menuConstructor, BackpackContext backpackContext, Component name) {
        return new ExtendedScreenHandlerFactory() {
            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                return menuConstructor.createMenu(i, backpackContext, player);
            }

            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                backpackContext.toBuffer(buf);
            }

            @Override
            public Component getDisplayName() {
                return name;
            }
        };
    }

    @FunctionalInterface
    public interface MenuConstructor {
        @Nullable
        AbstractContainerMenu createMenu(int i, BackpackContext backpackContext, Player player);
    }
}
