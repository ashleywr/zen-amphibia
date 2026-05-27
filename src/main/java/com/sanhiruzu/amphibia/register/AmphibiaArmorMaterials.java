package com.sanhiruzu.amphibia.register;

import com.sanhiruzu.amphibia.Amphibia;
import java.util.EnumMap;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AmphibiaArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, Amphibia.MOD_ID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> FROG_SLIME = ARMOR_MATERIALS.register(
            "frog_slime",
            () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), defenses -> {
                        defenses.put(ArmorItem.Type.BOOTS, 2);
                        defenses.put(ArmorItem.Type.LEGGINGS, 5);
                        defenses.put(ArmorItem.Type.CHESTPLATE, 6);
                        defenses.put(ArmorItem.Type.HELMET, 2);
                        defenses.put(ArmorItem.Type.BODY, 5);
                    }),
                    18,
                    SoundEvents.ARMOR_EQUIP_TURTLE,
                    () -> Ingredient.of(AmphibiaItems.FROG_SLIME_INGOT.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Amphibia.MOD_ID, "frog_slime"))),
                    0.5F,
                    0.0F
            )
    );

    public static Holder<ArmorMaterial> frogSlime() {
        return FROG_SLIME;
    }

    public static void register(IEventBus eventBus) {
        ARMOR_MATERIALS.register(eventBus);
    }
}
