package com.sanhiruzu.amphibia.client.render;

import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FrogRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.frog.Frog;

public class AmphibiaFrogRenderer extends FrogRenderer {
    public AmphibiaFrogRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected float getShadowRadius(Frog frog) {
        Float scale = frog.getData(AmphibiaAttachments.FROG_SCALE);
        if (scale == null) {
            return super.getShadowRadius(frog);
        }

        return super.getShadowRadius(frog) * Mth.clamp(scale, 0.5f, 2.5f);
    }
}
