package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.common.BackpackWrapperLookup;

public class BackpackItemStackRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
	private final Minecraft minecraft = Minecraft.getInstance();

	@Override
	public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrixStack, MultiBufferSource vertexConsumers, int light, int overlay) {
		matrixStack.pushPose();
		ItemRenderer itemRenderer = minecraft.getItemRenderer();
		BakedModel model = itemRenderer.getModel(stack, null, minecraft.player, 0);

		// boolean leftHand = minecraft.player != null && minecraft.player.getOffhandItem() == stack;
		// model = ForgeHooksClient.handleCameraTransforms(matrixStack, model, transformType, leftHand);
		// matrixStack.translate(-0.5D, -0.5D, -0.5D);

		RenderType rendertype = ItemBlockRenderTypes.getRenderType(stack, true);
		VertexConsumer ivertexbuilder = ItemRenderer.getFoilBufferDirect(vertexConsumers, rendertype, true, stack.hasFoil());
		itemRenderer.renderModelLists(model, stack, light, overlay, matrixStack, ivertexbuilder);
		BackpackWrapperLookup.get(stack).flatMap(backpackWrapper -> backpackWrapper.getRenderInfo().getItemDisplayRenderInfo().getDisplayItem()).ifPresent(displayItem -> {
			matrixStack.translate(0.5, 0.6, 0.25);
			matrixStack.scale(0.5f, 0.5f, 0.5f);
			matrixStack.mulPose(Axis.ZP.rotationDegrees(displayItem.getRotation()));
			itemRenderer.renderStatic(displayItem.getItem(), ItemDisplayContext.FIXED, light, overlay, matrixStack, vertexConsumers, minecraft.level, 0);
		});
		matrixStack.popPose();
	}
}
