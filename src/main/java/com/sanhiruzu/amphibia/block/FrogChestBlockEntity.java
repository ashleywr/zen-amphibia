package com.sanhiruzu.amphibia.block;

import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.menu.FrogChestMenu;
import com.sanhiruzu.amphibia.register.AmphibiaBlockEntities;
import com.sanhiruzu.amphibia.register.AmphibiaDataComponents;
import com.sanhiruzu.amphibia.register.AmphibiaMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FrogChestBlockEntity extends ChestBlockEntity {
    private FrogGenome genome = FrogGenome.createDefault();
    private NonNullList<ItemStack> items = NonNullList.withSize(rowsFromGenome(FrogGenome.createDefault()) * 9, ItemStack.EMPTY);

    public FrogChestBlockEntity(BlockPos pos, BlockState state) {
        super(AmphibiaBlockEntities.FROG_CHEST.get(), pos, state);
    }

    public FrogGenome getGenome() {
        return genome;
    }

    public int getRows() {
        return rowsFromGenome(genome);
    }

    public void setGenome(FrogGenome genome) {
        this.genome = genome == null ? FrogGenome.createDefault() : genome;
        resizeInventory(this.getRows() * 9);
        setChanged();
    }

    public static int rowsFromGenome(FrogGenome genome) {
        return Mth.clamp(3 + Math.round((genome.getScale() - 0.5F) * 3.0F), 3, 9);
    }

    private void resizeInventory(int size) {
        if (items.size() == size) {
            return;
        }

        NonNullList<ItemStack> resized = NonNullList.withSize(size, ItemStack.EMPTY);
        for (int i = 0; i < Math.min(items.size(), resized.size()); i++) {
            resized.set(i, items.get(i));
        }
        items = resized;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.zen_amphibia.frog_chest");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("FrogGenome")) {
            FrogGenome.CODEC.parse(NbtOps.INSTANCE, tag.get("FrogGenome")).result().ifPresent(this::setGenome);
        } else {
            resizeInventory(getRows() * 9);
        }

        this.items = NonNullList.withSize(getRows() * 9, ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("FrogGenome", FrogGenome.CODEC.encodeStart(NbtOps.INSTANCE, genome).getOrThrow());
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, registries);
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        int rows = getRows();
        return new FrogChestMenu(AmphibiaMenuTypes.menuTypeForRows(rows), id, player, this, rows);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        FrogGenome componentGenome = componentInput.get(AmphibiaDataComponents.FROG_DNA.get());
        if (componentGenome != null) {
            setGenome(componentGenome);
        }
    }

    @Override
    protected void collectImplicitComponents(net.minecraft.core.component.DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(AmphibiaDataComponents.FROG_DNA.get(), genome);
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove("FrogGenome");
    }
}
