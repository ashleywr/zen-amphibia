package com.sanhiruzu.amphibia.event;

import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = "amphibia")
public class FrogCollisionHandler {

    @SubscribeEvent
    public static void onFrogTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Frog frog)) return;
        if (frog.level().isClientSide) return;

        Float scale = frog.getData(AmphibiaAttachments.FROG_SCALE);
        if (scale != null && Math.abs(scale - 1.0f) > 0.01f) {
            updateFrogCollisionBox(frog, scale);
        }
    }

    private static void updateFrogCollisionBox(Frog frog, float scale) {
        AABB originalBox = frog.getBoundingBox();
        double centerX = (originalBox.minX + originalBox.maxX) / 2.0;
        double centerZ = (originalBox.minZ + originalBox.maxZ) / 2.0;
        double originalWidth = originalBox.maxX - originalBox.minX;
        double originalHeight = originalBox.maxY - originalBox.minY;
        double originalDepth = originalBox.maxZ - originalBox.minZ;

        double scaledWidth = originalWidth * scale;
        double scaledHeight = originalHeight * scale;
        double scaledDepth = originalDepth * scale;

        AABB scaledBox = new AABB(
            centerX - scaledWidth / 2.0,
            originalBox.minY,
            centerZ - scaledDepth / 2.0,
            centerX + scaledWidth / 2.0,
            originalBox.minY + scaledHeight,
            centerZ + scaledDepth / 2.0
        );

        frog.setBoundingBox(scaledBox);
    }
}
