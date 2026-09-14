package me.matl114.gui;

import com.google.common.collect.ImmutableMap;
import java.awt.Color;
import java.util.List;
import java.util.Map;
import me.matl114.utils.ChatUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public interface KalamaHelperHelperB {
    Identifier g = new Identifier("kalama", "gui/editor");
    Identifier h = new Identifier("kalama", "gui/remove");
    Identifier k = new Identifier("kalama", "gui/triangle_90");
    Identifier d = new Identifier("kalama", "gui/search");
    Identifier j = new Identifier("kalama", "gui/triangle");
    int c = -2130706433;
    Text i = Text.translatable("widget.gui.constants.open-list-edit");
    Map<EquipmentSlot, Identifier> l = ImmutableMap.<EquipmentSlot, Identifier>builder()
            .put(EquipmentSlot.MAINHAND, new Identifier("kalama", "gui/empty_main_hand_slot"))
            .put(EquipmentSlot.OFFHAND, new Identifier("kalama", "gui/empty_armor_slot_shield"))
            .put(EquipmentSlot.FEET, new Identifier("kalama", "gui/empty_armor_slot_boots"))
            .put(EquipmentSlot.LEGS, new Identifier("kalama", "gui/empty_armor_slot_leggings"))
            .put(EquipmentSlot.CHEST, new Identifier("kalama", "gui/empty_armor_slot_chestplate"))
            .put(EquipmentSlot.HEAD, new Identifier("kalama", "gui/empty_armor_slot_helmet"))
            .build();
    Identifier e = new Identifier("kalama", "gui/format");
    Color a = new Color(139, 139, 139);
    Color b = new Color(128, 128, 128);
    Identifier f = new Identifier("kalama", "gui/list_tag");

    static List<Text> c() {
        return ChatUtils.parseTranslation("widget.gui.constants.open-list-preview.tooltips", "");
    }

    static List<Text> a() {
        return ChatUtils.parseTranslation("widget.gui.constants.search-registry.tooltips", "");
    }

    static List<Text> b() {
        return ChatUtils.parseTranslation("widget.gui.constants.open-list-edit.tooltips", "");
    }
}
