package net.p3pp3rf1y.sophisticatedbackpacks.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.recipe.EmiSmithingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.LegacyUpgradeRecipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackSettingsScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.common.lookup.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.common.DyeRecipesMaker;
import net.p3pp3rf1y.sophisticatedbackpacks.crafting.BackpackUpgradeRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.crafting.SmithingBackpackUpgradeRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.client.gui.SettingsScreen;
import net.p3pp3rf1y.sophisticatedcore.compat.common.ClientRecipeHelper;
import net.p3pp3rf1y.sophisticatedcore.compat.emi.EmiGridMenuInfo;
import net.p3pp3rf1y.sophisticatedcore.compat.emi.EmiSettingsGhostDragDropHandler;
import net.p3pp3rf1y.sophisticatedcore.compat.emi.EmiStorageGhostDragDropHandler;

import java.util.Collection;

public class EmiCompat implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        registry.addExclusionArea(BackpackScreen.class, (screen, consumer) -> {
            screen.getUpgradeSlotsRectangle().ifPresent(r -> consumer.accept(new Bounds(r.getX(), r.getY(), r.getWidth(), r.getHeight())));
            screen.getUpgradeSettingsControl().getTabRectangles().forEach(r -> consumer.accept(new Bounds(r.getX(), r.getY(), r.getWidth(), r.getHeight())));
            screen.getSortButtonsRectangle().ifPresent(r -> consumer.accept(new Bounds(r.getX(), r.getY(), r.getWidth(), r.getHeight())));
        });

        registry.addExclusionArea(BackpackSettingsScreen.class, (screen, consumer) -> screen.getSettingsTabControl().getTabRectangles().forEach(r -> consumer.accept(new Bounds(r.getX(), r.getY(), r.getWidth(), r.getHeight()))));

        registry.addDragDropHandler(BackpackScreen.class, new EmiStorageGhostDragDropHandler<>());
        registry.addDragDropHandler(SettingsScreen.class, new EmiSettingsGhostDragDropHandler<>());

        registerCraftingRecipes(registry, DyeRecipesMaker.getRecipes());
        registerCraftingRecipes(registry, ClientRecipeHelper.getAndTransformAvailableRecipes(BackpackUpgradeRecipe.REGISTERED_RECIPES, ShapedRecipe.class, ClientRecipeHelper::copyShapedRecipe));
        registerSmithingRecipes(registry, ClientRecipeHelper.getAndTransformAvailableRecipes(SmithingBackpackUpgradeRecipe.REGISTERED_RECIPES, LegacyUpgradeRecipe.class, this::copyUpgradeRecipe));

        Comparison compareColor = Comparison.of((a, b) ->
            BackpackWrapperLookup.maybeGet(a.getItemStack())
                .map(stackA -> BackpackWrapperLookup.maybeGet(b.getItemStack())
                    .map(stackB -> stackA.getMainColor() == stackB.getMainColor() && stackA.getAccentColor() == stackB.getAccentColor())
                    .orElse(false))
                .orElse(false));

        registry.setDefaultComparison(EmiStack.of(ModItems.BACKPACK.get()), compareColor);

        registry.addRecipeHandler(ModItems.BACKPACK_CONTAINER_TYPE.get(), new EmiGridMenuInfo<>());
    }

    private LegacyUpgradeRecipe copyUpgradeRecipe(LegacyUpgradeRecipe recipe) {
        return new LegacyUpgradeRecipe(recipe.getId(), recipe.base, recipe.addition, recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
    }

    private static void registerCraftingRecipes(EmiRegistry registry, Collection<CraftingRecipe> recipes) {
        recipes.forEach(r -> registry.addRecipe(
            new EmiCraftingRecipe(
                r.getIngredients().stream().map(EmiIngredient::of).toList(),
                EmiStack.of(r.getResultItem(null)),
                r.getId())
            )
        );
    }

    private static void registerSmithingRecipes(EmiRegistry registry, Collection<SmithingRecipe> recipes) {
        recipes.forEach(r -> registry.addRecipe(new EmiSmithingRecipe(r)));
    }
}
