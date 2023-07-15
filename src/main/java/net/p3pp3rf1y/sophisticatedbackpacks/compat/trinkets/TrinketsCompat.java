package net.p3pp3rf1y.sophisticatedbackpacks.compat.trinkets;

import dev.emi.trinkets.TrinketsMain;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.compat.ICompat;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;


public class TrinketsCompat implements ICompat {

    private static final BackpackTrinket TRINKET_BACKPACK = new BackpackTrinket();
    private static final ItemStack BACKPACK = new ItemStack(ModItems.BACKPACK.get());

    public TrinketsCompat() {
        TrinketsApi.registerTrinket(ModItems.BACKPACK.get(), TRINKET_BACKPACK);
        TrinketsApi.registerTrinket(ModItems.IRON_BACKPACK.get(), TRINKET_BACKPACK);
        TrinketsApi.registerTrinket(ModItems.GOLD_BACKPACK.get(), TRINKET_BACKPACK);
        TrinketsApi.registerTrinket(ModItems.DIAMOND_BACKPACK.get(), TRINKET_BACKPACK);
        TrinketsApi.registerTrinket(ModItems.NETHERITE_BACKPACK.get(), TRINKET_BACKPACK);

        EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> {
            TrinketRendererRegistry.registerRenderer(ModItems.BACKPACK.get(), TRINKET_BACKPACK);
            TrinketRendererRegistry.registerRenderer(ModItems.IRON_BACKPACK.get(), TRINKET_BACKPACK);
            TrinketRendererRegistry.registerRenderer(ModItems.GOLD_BACKPACK.get(), TRINKET_BACKPACK);
            TrinketRendererRegistry.registerRenderer(ModItems.DIAMOND_BACKPACK.get(), TRINKET_BACKPACK);
            TrinketRendererRegistry.registerRenderer(ModItems.NETHERITE_BACKPACK.get(), TRINKET_BACKPACK);
        });


        PlayerInventoryProvider.get().addPlayerInventoryHandler(TrinketsMain.MOD_ID, this::getTrinketTags,
                (player, identifier) -> getFromTrinketInventory(player, identifier, TrinketInventory::getContainerSize, 0),
                (player, identifier, slot) -> getFromTrinketInventory(player, identifier, ti -> ti.getItem(slot), ItemStack.EMPTY),
                false, true, true ,true);
    }

    private Set<String> backpackTrinketIdentifiers = new HashSet<>();
    private long lastTagsRefresh = -1;
    private static final int TAGS_REFRESH_COOLDOWN = 100;

    private Set<String> getTrinketTags(Player player, long gameTime) {
        if (lastTagsRefresh + TAGS_REFRESH_COOLDOWN < gameTime) {
            lastTagsRefresh = gameTime;

            backpackTrinketIdentifiers = new HashSet<>();
            TrinketsApi.getTrinketComponent(player).ifPresent(comp -> {
                for (Map.Entry<String, Map<String, TrinketInventory>> group : comp.getInventory().entrySet()) {
                    for (Map.Entry<String, TrinketInventory> inventory : group.getValue().entrySet()) {
                        TrinketInventory trinketInventory = inventory.getValue();
                        SlotType slotType = trinketInventory.getSlotType();

                        for (int i = 0; i < trinketInventory.getContainerSize(); i++) {
                            SlotReference ref = new SlotReference(trinketInventory, i);
                            if (TrinketsApi.evaluatePredicateSet(slotType.getTooltipPredicates(), BACKPACK, ref, player)) {
                                backpackTrinketIdentifiers.add(group.getKey() + "/" + inventory.getKey());
                            }
                        }
                    }
                }
            });
        }
        return backpackTrinketIdentifiers;
    }

    public static <T> T getFromTrinketInventory(Player player, String identifier, Function<TrinketInventory, T> getFromHandler, T defaultValue) {
        String[] identifiers = identifier.split("/");
        return TrinketsApi.getTrinketComponent(player).map(comp ->
                getFromHandler.apply(comp.getInventory().get(identifiers[0]).get(identifiers[1]))).orElse(defaultValue);
    }

    @Override
    public void setup() {
        // noop
    }
}
