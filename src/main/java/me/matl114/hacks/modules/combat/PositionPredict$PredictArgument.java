package me.matl114.hacks.modules.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.matl114.accessors.hacks.EntityInternalAccess;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DisplayWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTType;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.CodecUtils;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.EnumAttrKeyValue;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public record PositionPredict$PredictArgument(double ticksLater, int ticksHistory, PositionPredict$Mode mode)
   implements NBTParsable<PositionPredict$PredictArgument> {
   public static NBTType<PositionPredict$PredictArgument> TYPE = new NBTType<>(
      "predictargument",
      RecordCodecBuilder.create(
         s -> s.group(
               Codec.withAlternative(Codec.DOUBLE, Codec.INT.xmap(t -> (double)t.intValue(), t -> (int)t.doubleValue()))
                  .fieldOf("ticks")
                  .forGetter(PositionPredict$PredictArgument::ticksLater),
               Codec.INT.fieldOf("history").forGetter(PositionPredict$PredictArgument::ticksHistory),
               CodecUtils.enumCodec(PositionPredict$Mode.class).fieldOf("mode").forGetter(PositionPredict$PredictArgument::mode)
            )
            .apply(s, PositionPredict$PredictArgument::new)
      ),
      (s, x, y, dx, dy) -> {
         KalamaHelperHelperCX subScreenWidget = KalamaHelperHelperCX.H(x, y, dx, dy);
         int half = dx / 4;
         WrapperFactory<Double, PositionPredict$PredictArgument> firstWrapper = WrapperFactory.of(
            d -> ((PositionPredict$PredictArgument)s.getOriginValue()).withTicksLater(d), PositionPredict$PredictArgument::ticksLater
         );
         WrapperFactory<Integer, PositionPredict$PredictArgument> secondWrapper = WrapperFactory.of(
            d -> ((PositionPredict$PredictArgument)s.getOriginValue()).withTicksHistory(d), PositionPredict$PredictArgument::ticksHistory
         );
         WrapperFactory<PositionPredict$Mode, PositionPredict$PredictArgument> thirdWrapper = WrapperFactory.of(
            d -> ((PositionPredict$PredictArgument)s.getOriginValue()).withMode(d), PositionPredict$PredictArgument::mode
         );
         return subScreenWidget.Q(
               DisplayWidget.instance(0, 0, dy, dy)
                  .setRenderHandler(
                     new ButtonElement(TextProvider.c(Text.translatableWithFallback("widget.nbt-parsable.predict-argument.ticks", "F:")), ButtonAction.c())
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.nbt-parsable.predict-argument.ticks.tooltips", "")))
                  )
            )
            .Q(new TypeConvertAttrKeyValue<>(s, firstWrapper, NBTTypes.e).generateValueWidget(dy, 0, half - dy, dy))
            .Q(
               DisplayWidget.instance(half, 0, dy, dy)
                  .setRenderHandler(
                     new ButtonElement(TextProvider.c(Text.translatableWithFallback("widget.nbt-parsable.predict-argument.history", "H:")), ButtonAction.c())
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.nbt-parsable.predict-argument.history.tooltips", "")))
                  )
            )
            .Q(new TypeConvertAttrKeyValue<>(s, secondWrapper, NBTTypes.c).generateValueWidget(half + dy, 0, half - dy, dy))
            .Q(
               DisplayWidget.instance(2 * half, 0, dy, dy)
                  .setRenderHandler(
                     new ButtonElement(TextProvider.c(Text.translatableWithFallback("widget.nbt-parsable.predict-argument.mode", "M:")), ButtonAction.c())
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.nbt-parsable.predict-argument.mode.tooltips", "")))
                  )
            )
            .Q(
               new TypeConvertAttrKeyValue<>(
                     s,
                     thirdWrapper,
                     EnumAttrKeyValue.createEnumWidgetFactory(PositionPredict$Mode.class),
                     WrapperFactory.of(PositionPredict$Mode::valueOf, Enum::name)
                  )
                  .generateValueWidget(2 * half + dy, 0, 2 * half - dy, dy)
            );
      },
      new PositionPredict$PredictArgument(2.0, 5, PositionPredict$Mode.NO_PREDICT)
   );

   @Override
   public NBTType<PositionPredict$PredictArgument> type() {
      return TYPE;
   }

   public Vec3d predict(Entity entity) {
      return this.predict0(entity, this.ticksLater);
   }

   public Vec3d predict0(Entity entity, double ticksLater) {
      int floor = (int)Math.floor(ticksLater);
      Vec3d floorPos = EntityInternalAccess.of(entity).getPositionPredictor().predict(floor, this.mode.ordinal(), this.ticksHistory);
      if (Math.abs(floor - ticksLater) < 0.01) {
         return floorPos;
      } else {
         Vec3d roofPos = EntityInternalAccess.of(entity).getPositionPredictor().predict(floor + 1, this.mode.ordinal(), this.ticksHistory);
         return floorPos.multiply(floor + 1 - ticksLater).add(roofPos.multiply(ticksLater - floor));
      }
   }

   public Vec3d predictWithExtraTicks(Entity entity, int ticks) {
      return this.predict0(entity, this.ticksLater + ticks);
   }

   public PositionPredict$PredictArgument withTicksLater(double ticksLater) {
      return this.ticksLater == ticksLater ? this : new PositionPredict$PredictArgument(ticksLater, this.ticksHistory, this.mode);
   }

   public PositionPredict$PredictArgument withTicksHistory(int ticksHistory) {
      return this.ticksHistory == ticksHistory ? this : new PositionPredict$PredictArgument(this.ticksLater, ticksHistory, this.mode);
   }

   public PositionPredict$PredictArgument withMode(PositionPredict$Mode mode) {
      return this.mode == mode ? this : new PositionPredict$PredictArgument(this.ticksLater, this.ticksHistory, mode);
   }
}
