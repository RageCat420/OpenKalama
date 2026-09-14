package me.matl114.hacks.utils.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.config.NBTType;
import me.matl114.managers.config.Ref;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;
import net.minecraft.text.Text;

public record LabelVec3(String xLabel, String yLabel, String zLabel, Vec3 data) implements NBTParsable<LabelVec3> {
   public static final NBTType<LabelVec3> TYPE = new NBTType<>(
      "labelvec3",
      RecordCodecBuilder.create(
         s -> s.group(
               Codec.STRING.fieldOf("x_label").forGetter(LabelVec3::xLabel),
               Codec.STRING.fieldOf("y_label").forGetter(LabelVec3::yLabel),
               Codec.STRING.fieldOf("z_label").forGetter(LabelVec3::zLabel),
               Vec3.TYPE.typeCodec().fieldOf("data").forGetter(LabelVec3::data)
            )
            .apply(s, LabelVec3::new)
      ),
      (s, x, y, dx, dy) -> {
         KalamaHelperHelperCX subScreenWidget = KalamaHelperHelperCX.H(x, y, dx, dy);
         int half = dx / 3;
         int label = Math.min(dy, half / 2);
         LabelVec3 original = (LabelVec3)s.getOriginValue();
         WrapperFactory<Double, LabelVec3> firstWrapper = WrapperFactory.of(d -> ((LabelVec3)s.getOriginValue()).withX(d), LabelVec3::x);
         WrapperFactory<Double, LabelVec3> secondWrapper = WrapperFactory.of(d -> ((LabelVec3)s.getOriginValue()).withY(d), LabelVec3::y);
         WrapperFactory<Double, LabelVec3> thirdWrapper = WrapperFactory.of(d -> ((LabelVec3)s.getOriginValue()).withZ(d), LabelVec3::z);
         return subScreenWidget.Q(
               DisplayWidget.instance(0, 0, label, dy)
                  .setRenderHandler(
                     new ButtonElement(TextProvider.c(Text.translatableWithFallback(original.xLabel(), original.xLabel())), ButtonAction.c())
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(original.xLabel() + ".tooltips", "")))
                  )
            )
            .Q(new TypeConvertAttrKeyValue<>(s, firstWrapper, NBTTypes.e).generateValueWidget(label, 0, half - label, dy))
            .Q(
               DisplayWidget.instance(half, 0, label, dy)
                  .setRenderHandler(
                     new ButtonElement(TextProvider.c(Text.translatableWithFallback(original.yLabel(), original.yLabel())), ButtonAction.c())
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(original.yLabel() + ".tooltips", "")))
                  )
            )
            .Q(new TypeConvertAttrKeyValue<>(s, secondWrapper, NBTTypes.e).generateValueWidget(half + label, 0, half - label, dy))
            .Q(
               DisplayWidget.instance(2 * half, 0, label, dy)
                  .setRenderHandler(
                     new ButtonElement(TextProvider.c(Text.translatableWithFallback(original.zLabel(), original.zLabel())), ButtonAction.c())
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(original.zLabel() + ".tooltips", "")))
                  )
            )
            .Q(new TypeConvertAttrKeyValue<>(s, thirdWrapper, NBTTypes.e).generateValueWidget(2 * half + label, 0, half - label, dy));
      },
      new LabelVec3("", "", "", new Vec3(0.0, 0.0, 0.0))
   );

   @Override
   public NBTType<LabelVec3> type() {
      return TYPE;
   }

   public LabelVec3 withX(double x) {
      return this.withData(this.data.withX(x));
   }

   public LabelVec3 withY(double y) {
      return this.withData(this.data.withY(y));
   }

   public LabelVec3 withZ(double z) {
      return this.withData(this.data.withZ(z));
   }

   public double x() {
      return this.data.x();
   }

   public double y() {
      return this.data.y();
   }

   public double z() {
      return this.data.z();
   }

   @Override
   public boolean isSameType(NBTParsable<?> type) {
      return type instanceof LabelVec3 vec3
         && Objects.equals(vec3.xLabel, this.xLabel)
         && Objects.equals(vec3.yLabel, this.yLabel)
         && Objects.equals(vec3.zLabel, this.zLabel);
   }

   @Override
   public <W> Optional<LabelVec3> tryTypeConvert(Ref<W> ref) {
      if (ref instanceof NBTRef nbt) {
         Object re = nbt.get();
         if (re instanceof LabelVec3 lbb) {
            return Optional.of(this.withData(lbb.data()));
         }

         if (re instanceof Vec3 ddd) {
            return Optional.of(this.withData(ddd));
         }
      }

      return Optional.empty();
   }

   public LabelVec3 withXLabel(String xLabel) {
      return this.xLabel == xLabel ? this : new LabelVec3(xLabel, this.yLabel, this.zLabel, this.data);
   }

   public LabelVec3 withYLabel(String yLabel) {
      return this.yLabel == yLabel ? this : new LabelVec3(this.xLabel, yLabel, this.zLabel, this.data);
   }

   public LabelVec3 withZLabel(String zLabel) {
      return this.zLabel == zLabel ? this : new LabelVec3(this.xLabel, this.yLabel, zLabel, this.data);
   }

   public LabelVec3 withData(Vec3 data) {
      return this.data == data ? this : new LabelVec3(this.xLabel, this.yLabel, this.zLabel, data);
   }
}
