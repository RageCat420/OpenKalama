package me.matl114.versioned.impl;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.matl114.versioned.api.VNbt;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.visitor.StringNbtWriter;

public class Nbt_v1_21_1 implements VNbt {
    @Override
    public NbtElement d(String element) {
        return this.readNbt(element);
    }

    @Override
    public String b(NbtElement element) {
        StringNbtWriter var2 = new StringNbtWriter();
        return var2.apply(element);
    }

    private NbtElement readNbt(String element) {
        try {
            return new StringNbtReader(new StringReader(element.replace("\\n", "\n"))).parseElement();
        } catch (CommandSyntaxException var3) {
            throw new RuntimeException("Could not deserialize nbt element ", var3);
        }
    }

    @Override
    public NbtElement c(String element) {
        return this.d(element);
    }
}
