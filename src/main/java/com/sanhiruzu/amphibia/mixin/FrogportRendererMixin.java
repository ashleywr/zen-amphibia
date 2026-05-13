package com.sanhiruzu.amphibia.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sanhiruzu.amphibia.duck.IFrogportDNA;
import com.sanhiruzu.amphibia.genetics.FrogDNA;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportRenderer;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FrogportRenderer.class, remap = false)
public abstract class FrogportRendererMixin {

    @Unique
    private static final ThreadLocal<FrogDNA> CURRENT_DNA = new ThreadLocal<>();

    @Inject(
        method = "renderSafe(Lcom/simibubi/create/content/logistics/packagePort/frogport/FrogportBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
        at = @At("HEAD")
    )
    private void amphibia$captureDNA(FrogportBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay, CallbackInfo ci) {
        if (blockEntity instanceof IFrogportDNA duck) {
            CURRENT_DNA.set(duck.amphibia$getDna());
        } else {
            CURRENT_DNA.remove();
        }
    }

    @Redirect(
        method = "renderSafe(Lcom/simibubi/create/content/logistics/packagePort/frogport/FrogportBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
        at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/render/SuperByteBuffer;renderInto(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V")
    )
    private void amphibia$colorFrogParts(SuperByteBuffer instance, PoseStack ms, VertexConsumer buffer) {
        int color = 0xFF80FFC8; // Default color

        FrogDNA dna = CURRENT_DNA.get();
        if (dna != null) {
            color = com.sanhiruzu.amphibia.infrastructure.FrogDNADisplayHelper.getDNAColor(dna);
        }

        instance.color(color);
        instance.renderInto(ms, buffer);
    }
}
