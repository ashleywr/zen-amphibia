package com.sanhiruzu.amphibia.genetics;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-dimension registry of frog genetics at world positions.
 * Used when raw genetic fluid is placed in the world (stores genome at position)
 * and when fluid is drained/picked up (retrieves genome from position).
 */
public class WildGeneticsRegistry {
    private static final Map<String, WildGeneticsRegistry> INSTANCES = new ConcurrentHashMap<>();

    private final Map<BlockPos, CompoundTag> entries = new ConcurrentHashMap<>();

    public static WildGeneticsRegistry get(ServerLevel level) {
        return INSTANCES.computeIfAbsent(
            level.dimension().location().toString(),
            k -> new WildGeneticsRegistry()
        );
    }

    public void put(BlockPos pos, CompoundTag tag) {
        entries.put(pos.immutable(), tag);
    }

    @Nullable
    public CompoundTag remove(BlockPos pos) {
        return entries.remove(pos);
    }

    @Nullable
    public CompoundTag get(BlockPos pos) {
        return entries.get(pos);
    }

    public static void unload(ServerLevel level) {
        INSTANCES.remove(level.dimension().location().toString());
    }
}
