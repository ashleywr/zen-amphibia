package com.sanhiruzu.amphibia.client.render;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.world.entity.animal.frog.Frog;
import com.mojang.blaze3d.vertex.PoseStack;

@EventBusSubscriber(modid = "zen_amphibia", value = Dist.CLIENT)
public class FrogScaleLayer {
    @SubscribeEvent
    @SuppressWarnings("rawtypes")
    public static void onFrogRender(RenderLivingEvent.Pre event) {
        if (!(event.getEntity() instanceof Frog frog)) return;

        Float scale = frog.getData(AmphibiaAttachments.FROG_SCALE);
        if (scale != null && Math.abs(scale - 1.0f) > 0.01f) {
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();
            poseStack.translate(0, Math.max(0.0f, scale - 1.0f) * 0.25f, 0);
            poseStack.scale(scale, scale, scale);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("rawtypes")
    public static void onFrogRenderPost(RenderLivingEvent.Post event) {
        if (!(event.getEntity() instanceof Frog frog)) return;

        Float scale = frog.getData(AmphibiaAttachments.FROG_SCALE);
        if (scale != null && Math.abs(scale - 1.0f) > 0.01f) {
            event.getPoseStack().popPose();
        }
    }
}
