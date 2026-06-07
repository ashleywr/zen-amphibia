package com.sanhiruzu.amphibia.compat.create;

import com.sanhiruzu.amphibia.register.AmphibiaItems;
import net.minecraft.world.item.ItemStack;

public final class FrogportCompat {
    private FrogportCompat() {
    }

    public static boolean isWorkerFrogport(ItemStack stack) {
        return stack.is(AmphibiaItems.WORKER_FROGPORT.get());
    }
}
