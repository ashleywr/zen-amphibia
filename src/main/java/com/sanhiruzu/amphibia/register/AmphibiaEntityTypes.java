package com.sanhiruzu.amphibia.register;

import com.sanhiruzu.amphibia.entity.CricketEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AmphibiaEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(Registries.ENTITY_TYPE, "zen_amphibia");

    public static final DeferredHolder<EntityType<?>, EntityType<CricketEntity>> CRICKET =
        ENTITY_TYPES.register("cricket", () ->
            EntityType.Builder.of(CricketEntity::new, MobCategory.CREATURE)
                .sized(0.3f, 0.3f)
                .clientTrackingRange(8)
                .build("zen_amphibia:cricket")
        );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
