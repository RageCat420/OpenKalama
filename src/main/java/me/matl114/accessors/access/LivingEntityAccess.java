package me.matl114.accessors.access;

import java.util.Map;
import me.matl114.accessors.events.EntityAccess;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface LivingEntityAccess<T extends LivingEntity> extends EntityAccess<T> {
    void setJumpingCooldown(int var1);

    static <T extends LivingEntity> LivingEntityAccess<T> of(T val) {
        return (LivingEntityAccess<T>) val;
    }

    float getJumpUpwardSpeed(float var1);

    Map<EquipmentSlot, ItemStack> getClientLastEquipmentSnapshot();

    void tickEquipment();

    void updateEquipmentAttributeChange();
}
