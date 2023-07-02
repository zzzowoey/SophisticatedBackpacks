package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.common.components.IBackpackWrapper;

public class BackpackItemStackRenderer  {
	public static void renderByItem(ItemStack stack, ItemDisplayContext modelTransformationMode, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		//ItemRenderer.render does transformations that would need to be transformed against in complicated way so rather pop the pose here and push the new one with the same transforms
		// applied in the correct order with the getModel
		//poseStack.popPose();
		//poseStack.pushPose();
		Minecraft minecraft = Minecraft.getInstance();
		ItemRenderer itemRenderer = minecraft.getItemRenderer();
		BakedModel model = itemRenderer.getModel(stack, null, minecraft.player, 0);

		//boolean leftHand = minecraft.player != null && minecraft.player.getOffhandItem() == stack;
		//model = ForgeHooksClient.handleCameraTransforms(poseStack, model, itemDisplayContext, leftHand);
		RenderType rendertype = ItemBlockRenderTypes.getRenderType(stack, true);
		VertexConsumer ivertexbuilder = ItemRenderer.getFoilBufferDirect(buffer, rendertype, true, stack.hasFoil());
		itemRenderer.renderModelLists(model, stack, combinedLight, combinedOverlay, poseStack, ivertexbuilder);
		IBackpackWrapper.maybeGet(stack).ifPresent(backpackWrapper ->
				backpackWrapper.getRenderInfo().getItemDisplayRenderInfo().getDisplayItem().ifPresent(displayItem -> {
					poseStack.translate(0.5, 0.6, 0.25);
					poseStack.scale(0.5f, 0.5f, 0.5f);
					poseStack.mulPose(Axis.ZP.rotationDegrees(displayItem.getRotation()));
					itemRenderer.renderStatic(displayItem.getItem(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, buffer, minecraft.level, 0);
				}));
	}
}
