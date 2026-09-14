package me.matl114.utils.config.kv;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.IconElement;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.BaseAttrKeyValue;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.versioned.api.VNbt;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public interface AttrKeyValues {
    AttrKeyValue.CustomWidgetFactory<Boolean> BOOLEAN_WIDGET_FACTORY =
            (s, x, y, inputDx, dy) -> ExecutableWidget.instance(x, y, dy, dy)
                    .eV(IconElement.co(
                            ButtonElement.bH,
                            ButtonElement.bJ,
                            ButtonAction.a(() -> s.valueChange(s, String.valueOf(!(Boolean) s.getOriginValue()))),
                            bl -> (Boolean) s.getOriginValue()));
    WrapperFactory<String, Boolean> BOOL_FACTORY = WrapperFactory.of(
            s -> {
                switch (s) {
                    case "true":
                        return Boolean.TRUE;
                    case "false":
                        return Boolean.FALSE;
                    default:
                        throw WrapperFactory.PARSE_FAILURE;
                }
            },
            v -> v == Boolean.TRUE ? "true" : "false");
    WrapperFactory<String, Integer> INT_FACTORY =
            WrapperFactory.of(Integer::parseInt, s -> s != null ? String.valueOf(s) : "0");
    WrapperFactory<String, Long> LONG_FACTORY =
            WrapperFactory.of(Long::parseLong, s -> s != null ? String.valueOf(s) : "0");
    WrapperFactory<String, Identifier> IDENTIFIER_FACTORY =
            WrapperFactory.of(Identifier::tryParse, Identifier::toString);
    WrapperFactory<String, Float> FLOAT_FACTORY =
            WrapperFactory.of(Float::parseFloat, s -> s != null ? String.valueOf(s) : "0.0");
    WrapperFactory<String, Double> DOUBLE_FACTORY =
            WrapperFactory.of(Double::parseDouble, s -> s != null ? String.valueOf(s) : "0.0");
    WrapperFactory<String, String> STRING_FACTORY = WrapperFactory.of(Function.identity(), Function.identity());
    WrapperFactory<String, TextColor> COLOR_FACTORY =
            WrapperFactory.of(s -> (TextColor) TextColor.parse(s).getOrThrow(), TextColor::getName);
    WrapperFactory<String, NbtElement> NBT_FACTORY = WrapperFactory.of(
            s -> s != null && !s.isEmpty() ? VNbt.getInstance().d(s) : null,
            val -> val == null ? "" : VNbt.getInstance().b(val));
    WrapperFactory<String, NbtCompound> NBT_COMPOUND_FACTORY = WrapperFactory.of(
            s -> {
                if (s != null && !s.isEmpty()) {
                    if (VNbt.getInstance().d(s) instanceof NbtCompound cpd) {
                        return cpd;
                    } else {
                        throw WrapperFactory.PARSE_FAILURE;
                    }
                } else {
                    return null;
                }
            },
            val -> val == null ? "" : VNbt.getInstance().b(val));
    Gson gson = new Gson();
    WrapperFactory<String, JsonElement> JSON_ELEMENT_FACTORY =
            WrapperFactory.of(str -> (JsonElement) gson.fromJson(str, JsonElement.class), gson::toJson);
    Type LIST_TYPE = (new TypeToken<List<String>>() {}).getType();
    WrapperFactory<String, List<String>> STR_LIST_FACTORY =
            WrapperFactory.of(s -> (List<String>) gson.fromJson(s, LIST_TYPE), gson::toJson);
    Type MAP_TYPE = (new TypeToken<Map<String, String>>() {}).getType();
    WrapperFactory<String, Map<String, String>> STR_MAP_FACTORY =
            WrapperFactory.of(s -> (Map<String, String>) gson.fromJson(s, MAP_TYPE), gson::toJson);

    public static class ClampedIntAttrKeyValue extends BaseAttrKeyValue<Integer> {
        final int min;
        final int max;

        public ClampedIntAttrKeyValue(String key, int value, int min, int max) {
            super(key, value, AttrKeyValues.INT_FACTORY);
            this.min = min;
            this.max = max;
            this.getValidators().add(i -> i >= this.min && i <= this.max);
        }

        public int clampInput(int val) {
            return MathHelper.clamp(val, this.min, this.max);
        }

        public int getMin() {
            return this.min;
        }

        public int getMax() {
            return this.max;
        }
    }
}
