package com.sanhiruzu.amphibia.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sanhiruzu.amphibia.duck.IFrogportDNA;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
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
    private static final ThreadLocal<FrogGenome> CURRENT_GENOME = new ThreadLocal<>();

    @Inject(
        method = "renderSafe(Lcom/simibubi/create/content/logistics/packagePort/frogport/FrogportBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
        at = @At("HEAD")
    )
    private void amphibia$captureGenome(FrogportBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
        if (blockEntity instanceof IFrogportDNA duck) {
            CURRENT_GENOME.set(duck.amphibia$getGenome());
        } else {
            CURRENT_GENOME.remove();
        }
    }

    @Redirect(
        method = "renderSafe(Lcom/simibubi/create/content/logistics/packagePort/frogport/FrogportBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
        at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/render/SuperByteBuffer;renderInto(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V")
    )
    private void amphibia$colorFrogParts(SuperByteBuffer instance, PoseStack ms, VertexConsumer buffer) {
        int color = 0xFF80FFC8; // Default color

        FrogGenome genome = CURRENT_GENOME.get();
        if (genome != null) {
            color = com.sanhiruzu.amphibia.infrastructure.FrogDNADisplayHelper.getGenomeColor(genome);
        }

        instance.color(color);
        instance.renderInto(ms, buffer);
    }
}
