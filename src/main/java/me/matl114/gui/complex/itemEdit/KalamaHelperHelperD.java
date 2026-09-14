package me.matl114.gui.complex.itemEdit;

import com.mojang.authlib.properties.PropertyMap;
import me.matl114.bukkit.BukkitItemStackUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.versioned.api.VRecord;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

public class KalamaHelperHelperD extends KalamaHelperHelperQ {
    ProfileComponent bn;
    protected static final int BASIC_DKEY = 50;
    AttrKeyValue<Integer> bl;
    AttrKeyValue<Item> bi;
    AttrKeyValue<String> bm;
    AttrKeyValue<String> bo;
    AttrKeyValue<Boolean> bk;
    AttrKeyValue<Integer> bj;
    KalamaHelperHelperF bp;

    protected KalamaHelperHelperD(final KalamaHelperHelperL this$1) {
        super(this$1);
        this.M = this$1;
        this.init();
    }

    @Override
    protected void ah() {}

    protected void init() {
        this.bi = AttrKeyValue.registry(
                "widget.gui.item-edit-screen.nbt-editor.generic.item-id", Registries.ITEM, this.M.dO.getItem());
        this.bj = AttrKeyValue.integer("widget.gui.item-edit-screen.nbt-editor.generic.count", this.M.dO.getCount());
        this.bl = AttrKeyValue.integer(
                "widget.gui.item-edit-screen.nbt-editor.generic.durability", this.M.dO.getDamage());
        String var1 = ItemStackUtils.aa(this.M.dO);
        this.bm = AttrKeyValue.str("widget.gui.item-edit-screen.nbt-editor.generic.sf-id", var1 == null ? "" : var1);
        this.bk = AttrKeyValue.bool(
                "widget.gui.item-edit-screen.nbt-editor.generic.unbreakable-flag",
                ItemStackUtils.getIsUnbreakable(this.M.dO));
        this.bp = new KalamaHelperHelperF(this.M.dO);
        ProfileComponent var2 = ItemStackUtils.getInPatch(this.M.dO, DataComponentTypes.PROFILE);
        this.bn = var2;
        String var3 = "";
        if (var2 != null) {
            var3 = BukkitItemStackUtils.getHashFromProfile(var2);
        }

        if (var3 == null) {
            var3 = "";
        }

        this.bo = AttrKeyValue.str("widget.gui.item-edit-screen.nbt-editor.generic.skull-hash", var3);
        new me.matl114.gui.complex.config.KalamaHelperHelperE<>(30, 0, 240, 20, 50, this.bi).addToSub(this);
        new me.matl114.gui.complex.config.KalamaHelperHelperE<>(30, 30, 240, 20, 50, this.bj).addToSub(this);
        new me.matl114.gui.complex.config.KalamaHelperHelperE<>(30, 60, 240, 20, 50, this.bl).addToSub(this);
        new me.matl114.gui.complex.config.KalamaHelperHelperE<>(30, 90, 240, 20, 50, this.bm).addToSub(this);
        new me.matl114.gui.complex.config.KalamaHelperHelperE<>(30, 120, 240, 20, 50, this.bk).addToSub(this);
        this.bp.factory(30, 150).addToSub(this);
        new me.matl114.gui.complex.config.KalamaHelperHelperE<>(30, 180, 240, 20, 50, this.bo).addToSub(this);
    }

    protected void saveChanges() {
        if (this.M.dO.getItem() != this.bi.getOriginValue()) {
            this.M.dO = ItemStackUtils.withTypeChange(this.M.dO, this.bi.getOriginValue());
        }

        this.M.dO.setCount(this.bj.getOriginValue());
        ItemStackUtils.setDamage(this.M.dO, this.bl.getOriginValue());
        ItemStackUtils.Z(this.M.dO, this.bm.getOriginValue());
        ItemStackUtils.setUnbreakable(this.M.dO, this.bk.getOriginValue());
        this.bp.applyChange(this.M.dO);
        String var1 = this.bo.getOriginValue();
        if (var1 != null && !var1.isEmpty()) {
            if (this.bn != null) {
                PropertyMap var2 =
                        BukkitItemStackUtils.buildPropertyMap(VRecord.getGameProfileProperties(this.bn), var1);
                ItemStackUtils.setOrRemoveChange(
                        this.M.dO, DataComponentTypes.PROFILE, VRecord.withProperty(this.bn, var2));
            } else {
                ItemStackUtils.setOrRemoveChange(this.M.dO, DataComponentTypes.PROFILE, BukkitItemStackUtils.e(var1));
            }
        } else if (this.bn != null) {
            ItemStackUtils.setOrRemoveChange(
                    this.M.dO, DataComponentTypes.PROFILE, VRecord.withProperty(this.bn, VRecord.k()));
        } else {
            ItemStackUtils.setOrRemoveChange(this.M.dO, DataComponentTypes.PROFILE, null);
        }
    }

    KalamaHelperHelperL M;

    @Override
    public void ag() {}
}
