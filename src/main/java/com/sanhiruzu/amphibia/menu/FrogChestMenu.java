package com.sanhiruzu.amphibia.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;

public class FrogChestMenu extends ChestMenu {
    private final int rows;

    public FrogChestMenu(MenuType<?> type, int id, Inventory playerInventory, int rows) {
        this(type, id, playerInventory, new SimpleContainer(rows * 9), rows);
    }

    public FrogChestMenu(MenuType<?> type, int id, Inventory playerInventory, Container container, int rows) {
        super(type, id, playerInventory, container, rows);
        this.rows = rows;
    }

    @Override
    public int getRowCount() {
        return rows;
    }
}
