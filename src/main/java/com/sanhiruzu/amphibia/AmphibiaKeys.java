package com.sanhiruzu.amphibia;

import org.lwjgl.glfw.GLFW;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = "amphibia", value = Dist.CLIENT)
public class AmphibiaKeys {

    public static final KeyMapping SHOW_FROG_DNA = new KeyMapping(
        "key.amphibia.show_frog_dna",
        GLFW.GLFW_KEY_LEFT_SHIFT,
        "Amphibia"
    );

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(SHOW_FROG_DNA);
    }

    public static boolean showFrogDNA() {
        return SHOW_FROG_DNA.isDown();
    }
}
