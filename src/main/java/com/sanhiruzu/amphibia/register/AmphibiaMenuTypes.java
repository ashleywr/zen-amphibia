package com.sanhiruzu.amphibia.register;

import com.sanhiruzu.amphibia.menu.FrogChestMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.flag.FeatureFlags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AmphibiaMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, "zen_amphibia");

    public static final DeferredHolder<MenuType<?>, MenuType<FrogChestMenu>> FROG_CHEST_3_ROWS = registerFrogChest("frog_chest_3_rows", 3);
    public static final DeferredHolder<MenuType<?>, MenuType<FrogChestMenu>> FROG_CHEST_4_ROWS = registerFrogChest("frog_chest_4_rows", 4);
    public static final DeferredHolder<MenuType<?>, MenuType<FrogChestMenu>> FROG_CHEST_5_ROWS = registerFrogChest("frog_chest_5_rows", 5);
    public static final DeferredHolder<MenuType<?>, MenuType<FrogChestMenu>> FROG_CHEST_6_ROWS = registerFrogChest("frog_chest_6_rows", 6);
    public static final DeferredHolder<MenuType<?>, MenuType<FrogChestMenu>> FROG_CHEST_7_ROWS = registerFrogChest("frog_chest_7_rows", 7);
    public static final DeferredHolder<MenuType<?>, MenuType<FrogChestMenu>> FROG_CHEST_8_ROWS = registerFrogChest("frog_chest_8_rows", 8);
    public static final DeferredHolder<MenuType<?>, MenuType<FrogChestMenu>> FROG_CHEST_9_ROWS = registerFrogChest("frog_chest_9_rows", 9);

    private static DeferredHolder<MenuType<?>, MenuType<FrogChestMenu>> registerFrogChest(String name, int rows) {
        return MENU_TYPES.register(name, () -> new MenuType<>(
                (id, playerInventory) -> new FrogChestMenu(menuTypeForRows(rows), id, playerInventory, rows),
                FeatureFlags.DEFAULT_FLAGS
        ));
    }

    public static MenuType<FrogChestMenu> menuTypeForRows(int rows) {
        return switch (rows) {
            case 4 -> FROG_CHEST_4_ROWS.get();
            case 5 -> FROG_CHEST_5_ROWS.get();
            case 6 -> FROG_CHEST_6_ROWS.get();
            case 7 -> FROG_CHEST_7_ROWS.get();
            case 8 -> FROG_CHEST_8_ROWS.get();
            case 9 -> FROG_CHEST_9_ROWS.get();
            default -> FROG_CHEST_3_ROWS.get();
        };
    }

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
