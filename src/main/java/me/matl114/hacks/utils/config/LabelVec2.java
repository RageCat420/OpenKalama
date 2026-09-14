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
import me.matl114.utils.config.PairLikeFactory;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;
import net.minecraft.text.Text;

public record LabelVec2(String xLabel, String yLabel, Vec2 data) implements NBTParsable<LabelVec2> {
   public static final NBTType<LabelVec2> TYPE = new NBTType<>(
      "labelvec2",
      RecordCodecBuilder.create(
         s -> s.group(
               Codec.STRING.fieldOf("x_label").forGetter(LabelVec2::xLabel),
               Codec.STRING.fieldOf("y_label").forGetter(LabelVec2::yLabel),
               Vec2.TYPE.typeCodec().fieldOf("data").forGetter(LabelVec2::data)
            )
            .apply(s, LabelVec2::new)
      ),
      (s, x, y, dx, dy) -> {
         LabelVec2 originalLabel = (LabelVec2)s.getOriginValue();
         KalamaHelperHelperCX subScreenWidget = KalamaHelperHelperCX.H(x, y, dx, dy);
         int half = dx / 2;
         WrapperFactory<Vec2, LabelVec2> wrapper = WrapperFactory.of(originalLabel::withData, LabelVec2::data);
         PairLikeFactory<Double, Double, LabelVec2> pairFactory = Vec2.PAIR_FACTORY.concat(wrapper);
         return subScreenWidget.Q(
               DisplayWidget.instance(0, 0, 2 * dy, dy)
                  .setRenderHandler(
                     new ButtonElement(TextProvider.c(Text.translatableWithFallback(originalLabel.xLabel(), originalLabel.xLabel())), ButtonAction.c())
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(originalLabel.xLabel() + ".tooltips", "")))
                  )
            )
            .Q(new TypeConvertAttrKeyValue<>(s, pairFactory.asFirstWrapper(s::getOriginValue), NBTTypes.e).generateValueWidget(2 * dy, 0, half - 2 * dy, dy))
            .Q(
               DisplayWidget.instance(half, 0, 2 * dy, dy)
                  .setRenderHandler(
                     new ButtonElement(TextProvider.c(Text.translatableWithFallback(originalLabel.yLabel(), originalLabel.yLabel())), ButtonAction.c())
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation(originalLabel.yLabel() + ".tooltips", "")))
                  )
            )
            .Q(
               new TypeConvertAttrKeyValue<>(s, pairFactory.asSecondWrapper(s::getOriginValue), NBTTypes.e)
                  .generateValueWidget(half + 2 * dy, 0, half - 2 * dy, dy)
            );
      },
      new LabelVec2("", "", new Vec2(0.0, 0.0))
   );

   @Override
   public NBTType<LabelVec2> type() {
      return TYPE;
   }

   @Override
   public boolean isSameType(NBTParsable<?> type) {
      return type instanceof LabelVec2 label2 && Objects.equals(this.xLabel, label2.xLabel()) && Objects.equals(this.yLabel, label2.yLabel());
   }

   @Override
   public <W> Optional<LabelVec2> tryTypeConvert(Ref<W> ref) {
      if (ref instanceof NBTRef nby) {
         Object bb = nby.get();
         if (bb instanceof LabelVec2 bbb) {
            return Optional.of(this.withData(bbb.data()));
         }

         if (bb instanceof Vec2 vvv) {
            return Optional.of(this.withData(vvv));
         }
      }

      return Optional.empty();
   }

   public LabelVec2 withXLabel(String xLabel) {
      return this.xLabel == xLabel ? this : new LabelVec2(xLabel, this.yLabel, this.data);
   }

   public LabelVec2 withYLabel(String yLabel) {
      return this.yLabel == yLabel ? this : new LabelVec2(this.xLabel, yLabel, this.data);
   }

   public LabelVec2 withData(Vec2 data) {
      return this.data == data ? this : new LabelVec2(this.xLabel, this.yLabel, data);
   }
}
