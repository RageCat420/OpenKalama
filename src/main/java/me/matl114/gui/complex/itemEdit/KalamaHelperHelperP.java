package me.matl114.gui.complex.itemEdit;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.utils.Debug;
import me.matl114.utils.config.AttrKeyValue;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class KalamaHelperHelperP {
    AttrKeyValue<Operation> d;
    AttrKeyValue<AttributeModifierSlot> e;
    AttrKeyValue<Double> c;
    protected static final Map<String, AttributeModifierSlot> f = new LinkedHashMap<>();
    AttrKeyValue<Identifier> a;
    protected static final Map<String, Operation> g = new LinkedHashMap<>();
    AttrKeyValue<EntityAttribute> b;

    static {
        for (AttributeModifierSlot var3 : AttributeModifierSlot.values()) {
            f.put(var3.asString(), var3);
        }

        g.put("widget.gui.item-edit-screen.nbt-editor.attribute.op.add", Operation.ADD_VALUE);
        g.put("widget.gui.item-edit-screen.nbt-editor.attribute.op.multiply-base", Operation.ADD_MULTIPLIED_BASE);
        g.put("widget.gui.item-edit-screen.nbt-editor.attribute.op.multiply-total", Operation.ADD_MULTIPLIED_TOTAL);
    }

    public KalamaHelperHelperP(
            final KalamaHelperHelperC this$2,
            String attribute,
            EntityAttributeModifier modifier,
            AttributeModifierSlot slot) {
        this.b = AttrKeyValue.openRegistry(
                "widget.gui.item-edit-screen.nbt-editor.attribute.name", Registries.ATTRIBUTE, attribute);
        this.a = AttrKeyValue.identifier("widget.gui.item-edit-screen.nbt-editor.attribute.uid", modifier.id());
        this.c = AttrKeyValue.doubleVal("widget.gui.item-edit-screen.nbt-editor.attribute.value", modifier.value());
        this.d = AttrKeyValue.enumMap("widget.gui.item-edit-screen.nbt-editor.attribute.op", modifier.operation(), g);
        this.e = AttrKeyValue.enumMap("widget.gui.item-edit-screen.nbt-editor.attribute.slot", slot, f);
    }

    public DrawableWidget b() {
        return new KalamaHelperHelperCX(0, 0, 0, 0)
                .Q(new me.matl114.gui.complex.config.KalamaHelperHelperE<>(0, 0, 180, 20, 50, this.b))
                .Q(new me.matl114.gui.complex.config.KalamaHelperHelperE<>(0, 20, 80, 20, 20, this.e))
                .Q(new me.matl114.gui.complex.config.KalamaHelperHelperE<>(80, 20, 80, 20, 20, this.d))
                .Q(new me.matl114.gui.complex.config.KalamaHelperHelperE<>(160, 20, 100, 20, 20, this.c))
                .Q(new me.matl114.gui.complex.config.KalamaHelperHelperE<>(0, 40, 260, 20, 80, this.a));
    }

    public KalamaHelperHelperP(final KalamaHelperHelperC this$2) {
        this(
                this$2,
                "minecraft:",
                new EntityAttributeModifier(
                        Identifier.tryParse("minecraft:" + UUID.randomUUID().toString()), 0.0, Operation.ADD_VALUE),
                AttributeModifierSlot.ANY);
    }

    public Entry value() {
        try {
            EntityAttribute var1 = this.b.getOriginValue();
            if (var1 != null) {
                RegistryEntry var2 = Registries.ATTRIBUTE.getEntry(this.b.getOriginValue());
                if (var2 != null && var2.value() != null) {
                    EntityAttributeModifier var3 = new EntityAttributeModifier(
                            this.a.getOriginValue(), this.c.getOriginValue(), this.d.getOriginValue());
                    AttributeModifierSlot var4 = this.e.getOriginValue();
                    return new Entry(var2, var3, var4);
                }

                Debug.e("Attribute null? ", this.b.getOriginValue(), this.b.getValue());
            }
        } catch (Throwable var5) {
        }

        return null;
    }
}
