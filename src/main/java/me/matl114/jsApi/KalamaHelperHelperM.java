package me.matl114.jsApi;

import com.jsmacrosce.jsmacros.api.library.FJavaUtils;
import com.jsmacrosce.jsmacros.api.math.Pos2D;
import com.jsmacrosce.jsmacros.api.math.Pos3D;
import com.jsmacrosce.jsmacros.client.api.classes.inventory.Inventory;
import com.jsmacrosce.jsmacros.client.api.helper.world.BlockDataHelper;
import com.jsmacrosce.jsmacros.core.Core;
import com.jsmacrosce.jsmacros.core.helpers.BaseHelper;
import com.jsmacrosce.jsmacros.core.language.EventContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class KalamaHelperHelperM implements JsMacrosBridge {
    FJavaUtils d;
    Core c;

    @Override
    public ItemStack e(Object what) {
        return (ItemStack) ((com.jsmacrosce.jsmacros.client.api.helper.inventory.ItemStackHelper) what).getRaw();
    }

    @Override
    public Object h(Object what) throws Throwable {
        EventContainer var2 = (EventContainer) what;
        Object var3 = var2.getCtx().getContext();
        return ReflectHelper.A(var3, "getBindings", "js");
    }

    @Override
    public Object newBlockData(BlockState b, BlockEntity e, BlockPos bp) {
        return new BlockDataHelper(b, e, bp);
    }

    public KalamaHelperHelperM(Core core) {
        this.c = core;

        try {
            this.d = new FJavaUtils(core);
        } catch (Throwable var3) {
        }
    }

    @Override
    public Object d() {
        return Inventory.create();
    }

    @Override
    public Object f(ItemStack what) {
        return new com.jsmacrosce.jsmacros.client.api.helper.inventory.ItemStackHelper(what);
    }

    @Override
    public Object b(Object object) {
        FJavaUtils var2 = this.d;
        if (object instanceof Vec3d var6) {
            return new Pos3D(var6);
        } else if (object instanceof Vec2f var5) {
            return new Pos2D(var5.x, var5.y);
        } else if (object instanceof HandledScreen var4) {
            return Inventory.create(var4);
        } else {
            Object var3 = var2.getHelperFromRaw(object);
            if (var3 != null) {
                return var3;
            } else {
                throw new UnsupportedOperationException(
                        "This type of instance is not supported to wrap, use JavaUtils.getHelperFromRaw instead");
            }
        }
    }

    @Override
    public <T> T a(Object what, Class<T> type) {
        if (what instanceof BaseHelper var8) {
            Object var4 = var8.getRaw();
            return (T) type.cast(var4);
        } else if (what instanceof Pos3D var7) {
            return (T) type.cast(new Vec3d(var7.x, var7.y, var7.z));
        } else if (what instanceof Pos2D var6) {
            return (T) type.cast(new Vec2f((float) var6.x, (float) var6.y));
        } else if (what instanceof Inventory var3) {
            return (T) type.cast(var3.getRawContainer());
        } else {
            try {
                return (T) type.cast(what);
            } catch (Throwable var5) {
                throw new UnsupportedOperationException("This type is not supported to unwrap: "
                        + what.getClass().getName());
            }
        }
    }

    @Override
    public boolean isItemEmpty(Object what) {
        return ((com.jsmacrosce.jsmacros.client.api.helper.inventory.ItemStackHelper) what).isEmpty();
    }
}
