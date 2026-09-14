package me.matl114.bridge;

import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.Items;

public class ItemBridge {
    public static Item TESTITEM = Items.register("myitem", new Item(new Settings()));

    public static void init() {}
}
