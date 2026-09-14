package me.matl114.hacks.utils.config;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import me.matl114.accessors.gui.ScreenAccess;
import me.matl114.gui.KalamaHelperHelperB;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.IconElement;
import me.matl114.gui.presets.choices.ColorSelectIcon;
import me.matl114.gui.presets.lists.NBTBoundedListScreen;
import me.matl114.gui.presets.lists.NBTListModifyScreen;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTType;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.CodecUtils;
import me.matl114.utils.collections.InitializationTask;
import me.matl114.utils.config.AttrKeyValue;
import me.matl114.utils.config.BaseAttrKeyValue;
import me.matl114.utils.config.PairLikeFactory;
import me.matl114.utils.config.ValueAccessor;
import me.matl114.utils.config.WidgetFactory;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.AttrKeyValues;
import me.matl114.utils.config.kv.EnumAttrKeyValue;
import me.matl114.utils.config.kv.RegistryAttrKeyValue;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;
import me.matl114.utils.config.kv.WrapperAttrKeyValue;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public interface NBTTypes {
    Map<String, NBTType<?>> a = new LinkedHashMap<>();
    Codec<NBTType<?>> b = Codec.STRING.flatXmap(
            s -> {
                NBTType<?> var1 = primitiveTypes(s);
                return var1 == null ? DataResult.<NBTType<?>>error(() -> "Not found") : DataResult.success(var1);
            },
            t -> a.containsKey(t.typeName())
                    ? DataResult.success(t.typeName())
                    : DataResult.error(() -> "Not a primitive type: " + t.typeName()));
    NBTType<String> g = new NBTType<>(
            "string", Codec.STRING, BaseAttrKeyValue.getWidgetFactory(), AttrKeyValues.STRING_FACTORY, "");
    NBTType<Integer> c =
            new NBTType<>("int", Codec.INT, BaseAttrKeyValue.getWidgetFactory(), AttrKeyValues.INT_FACTORY, 0);
    NBTType<Long> d =
            new NBTType<>("long", Codec.LONG, BaseAttrKeyValue.getWidgetFactory(), AttrKeyValues.LONG_FACTORY, 0L);
    NBTType<Double> e = new NBTType<>(
            "double", Codec.DOUBLE, BaseAttrKeyValue.getWidgetFactory(), AttrKeyValues.DOUBLE_FACTORY, 0.0);
    NBTType<Boolean> f = new NBTType<>(
            "boolean", Codec.BOOL, AttrKeyValues.BOOLEAN_WIDGET_FACTORY, AttrKeyValues.BOOL_FACTORY, false);
    NBTType<TextColor> h = new NBTType<>(
            "color",
            Codec.withAlternative(
                    TextColor.CODEC,
                    Codec.INT.comapFlatMap(
                            s -> s >= 0 && s <= 16777215
                                    ? DataResult.success(TextColor.fromRgb(s))
                                    : DataResult.error(() -> "Color value out of range: " + s),
                            TextColor::getRgb)),
            NBTTypes::generateColorInputWidget,
            AttrKeyValues.COLOR_FACTORY,
            TextColor.fromFormatting(Formatting.BLACK));
    NBTType<MultiKeyBind> i = new NBTType<>(
            "keybind",
            Codec.STRING.comapFlatMap(
                    str -> {
                        try {
                            return DataResult.success(new MultiKeyBind(str));
                        } catch (Throwable var2) {
                            return DataResult.error(() -> "Invalid keybind: " + str);
                        }
                    },
                    MultiKeyBind::b),
            KeyBindRef.WIDGET_FACTORY,
            KeyBindRef.FACTORY,
            new MultiKeyBind());
    NBTType<Registry<?>> j = (NBTType<Registry<?>>) new NBTType(
            "registry",
            Registries.REGISTRIES.getCodec(),
            (AttrKeyValue.CustomWidgetFactory)
                    (s, x, y, dx, dy) -> (DrawableWidget) RegistryAttrKeyValue.generateTextInputWithRegistrySearch(
                            (Registry) Registries.REGISTRIES, (AttrKeyValue) s, x, y, dx, dy),
            WrapperFactory.of(
                    (java.util.function.Function<String, Registry<?>>)
                            s -> (Registry<?>) Registries.REGISTRIES.get(Identifier.tryParse(s)),
                    (java.util.function.Function<Registry<?>, String>)
                            v -> ((Registry) Registries.REGISTRIES).getId(v).toString()),
            Registries.BLOCK);
    NBTType<Pattern> k = e("pattern", g, WrapperFactory.of(Pattern::compile, Pattern::pattern));
    NBTType<Identifier> l = createComapFlatMap(
            "identifier", g, WrapperFactory.of(Identifier::of, Identifier::toString), Identifier.ofVanilla(""));
    NBTType<NbtElement> m = e("nbtelement", g, AttrKeyValues.NBT_FACTORY);
    NBTType<NbtCompound> n = new NBTType<>(
            "nbtcompound",
            NbtCompound.CODEC,
            BaseAttrKeyValue.getWidgetFactory(),
            AttrKeyValues.NBT_COMPOUND_FACTORY,
            new NbtCompound());
    NBTType<Vec2> p = Vec2.TYPE;
    NBTType<Vec3> q = Vec3.TYPE;
    NBTType<Pos3> r = Pos3.TYPE;
    NBTType<StringFormat> x = StringFormat.TYPE;
    NBTType<Label> v = Label.TYPE;
    NBTType<WrapEnum<?>> o = WrapEnum.TYPE.cast();
    NBTType<Primitive<?>> s = Primitive.TYPE.cast();
    NBTType<LabelPrimitive<?>> w = LabelPrimitive.TYPE.cast();
    NBTType<Holder<?>> t = Holder.TYPE.cast();
    NBTType<WeakHolder<?>> u = WeakHolder.TYPE.cast();
    NBTType<PrimitiveList<?>> z = PrimitiveList.TYPE.cast();
    NBTType<PrimitiveMap<?, ?>> y = PrimitiveMap.TYPE.cast();
    InitializationTask A = InitializationTask.of(NBTTypes::init);

    static <T, K1, K2> NBTType<T> createPairWithKey(
            String targetClass,
            Codec<K1> k1Codec,
            K1 k1Default,
            String name1,
            NBTType<K2> k2Type,
            String name2,
            PairLikeFactory<K1, K2, T> pairFactory,
            WidgetFactory<K1> k1Factory,
            UnaryOperator<AttrKeyValue.CustomWidgetFactory<K2>> k2Resize) {
        return new NBTType<>(
                targetClass,
                RecordCodecBuilder.create(instance -> instance.group(
                                k1Codec.fieldOf(name1).forGetter(pairFactory::getFirst),
                                k2Type.typeCodec().fieldOf(name2).forGetter(pairFactory::getSecond))
                        .apply(instance, pairFactory::create)),
                (s, x, y, dx, dy) -> {
                    K1 var10 = pairFactory.getFirst(s.getOriginValue());
                    @SuppressWarnings({"unchecked", "rawtypes"})
                    TypeConvertAttrKeyValue var11 =
                            new TypeConvertAttrKeyValue(s, pairFactory.asSecondWrapper(s::getOriginValue), k2Type);
                    KalamaHelperHelperCX var12 = new KalamaHelperHelperCX(x, y, dx, dy);
                    var12.Q(k1Factory.generateWidget(var10, 0, 0, dx, dy))
                            .Q(k2Resize.apply(k2Type.customWidgetFactory()).generateWidget(var11, 0, 0, dx, dy));
                    return var12;
                },
                (T) pairFactory.create(k1Default, k2Type.empty()));
    }

    static <W> void openListModifyScreen(
            AttrKeyValue<List<W>> keyValue, NBTType<W> type, Supplier<W> supplier, int listWidth, int listHeight) {
        ScreenAccess.of(new NBTListModifyScreen(
                        keyValue,
                        type,
                        supplier,
                        lst -> keyValue.valueChangeInternal(null, (List<W>) lst),
                        listWidth,
                        listHeight))
                .openFromCurrent();
    }

    static <T, W> void openBoundedListModifyScreen(
            AttrKeyValue<Map<T, W>> keyValue,
            List<T> bound,
            NBTType<W> type,
            WidgetFactory<T> keyWidget,
            int keyLabelWidth,
            int listWidth,
            int listHeight) {
        Map<T, W> var7 = (Map<T, W>) keyValue.getOriginValue();
        boolean var8 = false;

        for (T var10 : bound) {
            if (!var7.containsKey(var10)) {
                var8 = true;
                var7 = new LinkedHashMap<>((Map<T, W>) var7);
                var7.put(var10, type.createEmpty());
            }
        }

        if (var8) {
            keyValue.valueChangeInternal(null, var7);
        }

        NBTBoundedListScreen var11 = new NBTBoundedListScreen(
                keyValue,
                type,
                keyWidget,
                map -> keyValue.valueChangeInternal(null, (Map<T, W>) map),
                keyLabelWidth,
                listWidth,
                listHeight);
        ScreenAccess.of(var11).openFromCurrent();
    }

    static <T, K1, K2> NBTType<T> createPairLike(
            String targetClass,
            NBTType<K1> k1Type,
            String name1,
            NBTType<K2> k2Type,
            String name2,
            PairLikeFactory<K1, K2, T> pairFactory,
            UnaryOperator<AttrKeyValue.CustomWidgetFactory<K1>> k1Resize,
            UnaryOperator<AttrKeyValue.CustomWidgetFactory<K2>> k2Resize) {
        return new NBTType<>(
                targetClass,
                RecordCodecBuilder.create(instance -> instance.group(
                                k1Type.typeCodec().fieldOf(name1).forGetter(pairFactory::getFirst),
                                k2Type.typeCodec().fieldOf(name2).forGetter(pairFactory::getSecond))
                        .apply(instance, pairFactory::create)),
                (s, x, y, dx, dy) -> {
                    TypeConvertAttrKeyValue var11 =
                            new TypeConvertAttrKeyValue(s, pairFactory.asFirstWrapper(s::getOriginValue), k1Type);
                    TypeConvertAttrKeyValue var12 =
                            new TypeConvertAttrKeyValue(s, pairFactory.asSecondWrapper(s::getOriginValue), k2Type);
                    KalamaHelperHelperCX var13 = new KalamaHelperHelperCX(x, y, dx, dy);
                    var13.Q(k1Resize.apply(k1Type.customWidgetFactory()).generateWidget(var11, 0, 0, dx, dy))
                            .Q(k2Resize.apply(k2Type.customWidgetFactory()).generateWidget(var12, 0, 0, dx, dy));
                    return var13;
                },
                WrapperFactory.of(
                        s -> {
                            List var4 = AttrKeyValues.STR_LIST_FACTORY.create(s);
                            return (T) pairFactory.create(
                                    k1Type.stringifyFactory().create((String) var4.get(0)),
                                    k2Type.stringifyFactory().create((String) var4.get(1)));
                        },
                        v -> AttrKeyValues.STR_LIST_FACTORY.get(
                                List.of((String) k1Type.stringifyFactory().get(pairFactory.getFirst(v)), (String)
                                        k2Type.stringifyFactory().get(pairFactory.getSecond(v))))),
                (T) pairFactory.create(k1Type.empty(), k2Type.empty()));
    }

    static <W> DrawableWidget generateListModifyButton(
            AttrKeyValue<List<W>> keyValue,
            NBTType<W> typeW,
            Supplier<W> supplier,
            int x,
            int y,
            int dx,
            int dy,
            int listWidth,
            int listHeight) {
        return new KalamaHelperHelperCX(x, y, dx, dy)
                .Q(new ExecutableWidget(dy, 0, dx - dy, dy)
                        .eV(new ButtonElement(
                                        TextProvider.c(KalamaHelperHelperB.i),
                                        ButtonAction.a(() ->
                                                openListModifyScreen(keyValue, typeW, supplier, listWidth, listHeight)))
                                .aO(TooltipHandler.ap(KalamaHelperHelperB.b()))))
                .Q(DisplayWidget.instance(0, 0, dy - 1, dy)
                        .setRenderHandler(IconElement.cm(KalamaHelperHelperB.f, ButtonAction.c())));
    }

    static <T, W> NBTType<T> createListLke(
            String targetClass, NBTType<W> type, WrapperFactory<List<W>, T> wrapper, int listWidth, int listHeight) {
        return new NBTType<>(
                targetClass,
                Codec.list(type.typeCodec()).xmap(wrapper::create, wrapper::get),
                (attr, x, y, dx, dy) -> generateListModifyButton(
                        new WrapperAttrKeyValue<>(attr, wrapper),
                        type,
                        type::empty,
                        x,
                        y,
                        dx,
                        dy,
                        listWidth,
                        listHeight),
                AttrKeyValues.STR_LIST_FACTORY
                        .concat(WrapperFactory.list(type.stringifyFactory()))
                        .concat(wrapper),
                (T) wrapper.create(List.of()));
    }

    static <T> NBTType<T> n(String targetClass, Map<String, T> finiteLookup, Function<T, String> string) {
        return new NBTType<>(
                targetClass,
                CodecUtils.finiteMapCodec(finiteLookup, string),
                EnumAttrKeyValue.createFiniteLookupWidgetFactory(finiteLookup),
                EnumAttrKeyValue.createFiniteMapLookup(finiteLookup),
                (T) finiteLookup.values().iterator().next());
    }

    static void init() {
        Field[] var0 = NBTTypes.class.getDeclaredFields();

        for (Field var4 : var0) {
            try {
                if (Modifier.isStatic(var4.getModifiers()) && NBTType.class.isAssignableFrom(var4.getType())) {
                    var4.setAccessible(true);
                    NBTType var5 = (NBTType) var4.get(null);
                    a.put(var5.typeName(), var5);
                }
            } catch (Throwable var6) {
            }
        }
    }

    static <W> Codec<NBTType<W>> codec() {
        return (Codec<NBTType<W>>) (Codec<?>) b;
    }

    static <T, W> NBTType<T> e(String value, NBTType<W> type, WrapperFactory<W, T> wrapper) {
        return createComapFlatMap(value, type, wrapper, (T) wrapper.create(type.empty()));
    }

    static <T, W> NBTType<T> g(
            String targetClass,
            NBTType<W> type,
            WrapperFactory<List<W>, T> wrapper,
            Supplier<W> customNewElementSupplier,
            int listWidth,
            int listHeight) {
        return new NBTType<>(
                targetClass,
                Codec.list(type.typeCodec()).xmap(wrapper::create, wrapper::get),
                (attr, x, y, dx, dy) -> generateListModifyButton(
                        new WrapperAttrKeyValue<>(attr, wrapper),
                        type,
                        customNewElementSupplier,
                        x,
                        y,
                        dx,
                        dy,
                        listWidth,
                        listHeight),
                AttrKeyValues.STR_LIST_FACTORY
                        .concat(WrapperFactory.list(type.stringifyFactory()))
                        .concat(wrapper),
                (T) wrapper.create(List.of()));
    }

    static <T, K1, K2> NBTType<T> createArrayMapLike(
            String targetClass,
            NBTType<K1> k1Type,
            Supplier<K1> k1Supplier,
            String name1,
            NBTType<K2> k2Type,
            Supplier<K2> k2Supplier,
            String name2,
            WrapperFactory<Map<K1, K2>, T> mapLike,
            UnaryOperator<AttrKeyValue.CustomWidgetFactory<K1>> k1Resize,
            UnaryOperator<AttrKeyValue.CustomWidgetFactory<K2>> k2Resize,
            int width,
            int height) {
        PairLikeFactory var12 = PairLikeFactory.of(Pair::of, Pair::getFirst, Pair::getSecond);
        NBTType var13 = createPairLike("pair", k1Type, name1, k2Type, name2, var12, k1Resize, k2Resize);
        WrapperFactory var14 = WrapperFactory.getListMapWrapper().concat((WrapperFactory) mapLike);
        return g(
                targetClass,
                var13,
                var14,
                () -> (Pair) var12.create(k1Supplier.get(), k2Supplier.get()),
                width,
                height);
    }

    static <T, W> NBTType<T> createXMap(String name, NBTType<W> type, WrapperFactory<W, T> wrapper) {
        return new NBTType<>(
                name,
                type.typeCodec().xmap(wrapper::create, wrapper::get),
                (attr, x, y, dx, dy) -> new TypeConvertAttrKeyValue(
                                attr, wrapper, type.customWidgetFactory(), type.stringifyFactory())
                        .generateValueWidget(x, y, dx, dy),
                type.stringifyFactory().concat(wrapper),
                (T) wrapper.create(type.empty()));
    }

    static <T> NBTType<Optional<T>> m(String targetClass, NBTType<T> type, String defaultValue) {
        WrapperFactory var3 = type.stringifyFactory();
        WrapperFactory var4 = WrapperFactory.of(Optional::of, s -> s.orElse(null));
        WrapperFactory var5 = WrapperFactory.of(
                s -> Objects.equals(s, defaultValue) ? Optional.empty() : Optional.of(var3.create(s)),
                t -> (String) t.map(var3::get).orElse((T) defaultValue));
        WrapperFactory var6 = var5.concat(var4.inverse());
        return new NBTType<>(
                targetClass,
                var4.wrapCodecComapFlatMap(type.typeCodec()),
                (attr, x, y, dx, dy) -> new TypeConvertAttrKeyValue(attr, var4, type.customWidgetFactory(), var6)
                        .generateValueWidget(x, y, dx, dy),
                var5,
                Optional.empty());
    }

    static <T, K1, K2> NBTType<T> j(
            String targetClass,
            NBTType<K1> k1Type,
            String name1,
            NBTType<K2> k2Type,
            String name2,
            WrapperFactory<Map<K1, K2>, T> mapLike,
            UnaryOperator<AttrKeyValue.CustomWidgetFactory<K1>> k1Resize,
            UnaryOperator<AttrKeyValue.CustomWidgetFactory<K2>> k2Resize,
            int width,
            int height) {
        return createArrayMapLike(
                targetClass,
                k1Type,
                k1Type::empty,
                name1,
                k2Type,
                k2Type::empty,
                name2,
                mapLike,
                k1Resize,
                k2Resize,
                width,
                height);
    }

    static <T> NBTType<Optional<T>> l(String targetClass, NBTType<T> type, String defaultValue) {
        WrapperFactory var3 = type.stringifyFactory();
        WrapperFactory var4 = WrapperFactory.of(Optional::ofNullable, s -> s.orElse(null));
        WrapperFactory var5 = WrapperFactory.of(
                s -> Objects.equals(s, defaultValue) ? Optional.empty() : Optional.ofNullable(var3.create(s)),
                t -> (String) t.map(var3::get).orElse((T) defaultValue));
        WrapperFactory var6 = var5.concat(var4.inverse());
        return new NBTType<>(
                targetClass,
                var4.wrapCodecXmap(type.typeCodec()),
                (attr, x, y, dx, dy) -> new TypeConvertAttrKeyValue(attr, var4, type.customWidgetFactory(), var6)
                        .generateValueWidget(x, y, dx, dy),
                var5,
                Optional.empty());
    }

    static <T> NBTType<T> primitiveTypes(String string) {
        return (NBTType<T>) a.get(string);
    }

    static <T, W> DrawableWidget generateBoundedListModifyButton(
            AttrKeyValue<Map<T, W>> keyValue,
            List<T> keyBound,
            NBTType<W> valueType,
            WidgetFactory<T> keyWidget,
            int x,
            int y,
            int dx,
            int dy,
            int keyLabelWidth,
            int listWidth,
            int listHeight) {
        return new KalamaHelperHelperCX(x, y, dx, dy)
                .Q(new ExecutableWidget(dy, 0, dx - dy, dy)
                        .eV(new ButtonElement(
                                        TextProvider.c(KalamaHelperHelperB.i),
                                        ButtonAction.a(() -> openBoundedListModifyScreen(
                                                keyValue,
                                                keyBound,
                                                valueType,
                                                keyWidget,
                                                keyLabelWidth,
                                                listWidth,
                                                listHeight)))
                                .aO(TooltipHandler.ap(KalamaHelperHelperB.b()))))
                .Q(DisplayWidget.instance(0, 0, dy - 1, dy)
                        .setRenderHandler(IconElement.cm(KalamaHelperHelperB.f, ButtonAction.c())));
    }

    static DrawableWidget generateColorInputWidget(AttrKeyValue<TextColor> keyValue, int x, int y, int dx, int dy) {
        KalamaHelperHelperCX var5 = new KalamaHelperHelperCX(x, y, dx, dy);
        var5.Q(BaseAttrKeyValue.generateTextInputValueWidget(keyValue, 0, 0, dx - dy, dy));
        var5.Q(new ExecutableWidget(dx - dy, 0, dy, dy).eV(new ColorSelectIcon(ValueAccessor.of(keyValue))));
        return var5;
    }

    static <T, W> NBTType<T> createComapFlatMap(String value, NBTType<W> type, WrapperFactory<W, T> wrapper, T empty) {
        return new NBTType<>(
                value,
                type.typeCodec()
                        .comapFlatMap(
                                s -> {
                                    try {
                                        return DataResult.success(wrapper.create(s));
                                    } catch (Throwable var3) {
                                        return DataResult.error(() -> "Error while creating");
                                    }
                                },
                                wrapper::get),
                (attr, x, y, dx, dy) -> new TypeConvertAttrKeyValue(
                                attr, wrapper, type.customWidgetFactory(), type.stringifyFactory())
                        .generateValueWidget(x, y, dx, dy),
                type.stringifyFactory().concat(wrapper),
                (T) empty);
    }
}
