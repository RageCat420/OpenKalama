package me.matl114.hacks.utils.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ExecutableWidget;
import me.matl114.gui.basic.KalamaHelperHelperCX;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.basic.TooltipHandler;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTType;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.config.WrapperFactory;
import me.matl114.utils.config.kv.TypeConvertAttrKeyValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public record Pos3(int x, int y, int z) implements NBTParsable<Pos3> {
   public static final NBTType<Pos3> TYPE = new NBTType<>(
      "pos3",
      RecordCodecBuilder.create(
         s -> s.group(Codec.INT.fieldOf("x").forGetter(Pos3::x), Codec.INT.fieldOf("y").forGetter(Pos3::y), Codec.INT.fieldOf("z").forGetter(Pos3::z))
            .apply(s, Pos3::new)
      ),
      (s, x, y, dx, dy) -> {
         KalamaHelperHelperCX subScreenWidget = KalamaHelperHelperCX.H(x, y, dx, dy);
         int half = dx / 4;
         WrapperFactory<Integer, Pos3> firstWrapper = WrapperFactory.of(d -> ((Pos3)s.getOriginValue()).withX(d), Pos3::x);
         WrapperFactory<Integer, Pos3> secondWrapper = WrapperFactory.of(d -> ((Pos3)s.getOriginValue()).withY(d), Pos3::y);
         WrapperFactory<Integer, Pos3> thirdWrapper = WrapperFactory.of(d -> ((Pos3)s.getOriginValue()).withZ(d), Pos3::z);
         return subScreenWidget.Q(new TypeConvertAttrKeyValue<>(s, firstWrapper, NBTTypes.c).generateValueWidget(0, 0, half, dy))
            .Q(new TypeConvertAttrKeyValue<>(s, secondWrapper, NBTTypes.c).generateValueWidget(half, 0, half, dy))
            .Q(new TypeConvertAttrKeyValue<>(s, thirdWrapper, NBTTypes.c).generateValueWidget(2 * half, 0, half, dy))
            .Q(
               ExecutableWidget.instance(3 * half, 0, half / 2, dy)
                  .eV(new ButtonElement(TextProvider.c(Text.translatableWithFallback("widget.nbt-parsable.pos3.here", "Here")), ButtonAction.a(() -> {
                     ClientPlayerEntity pl = MinecraftClient.getInstance().player;
                     if (pl != null) {
                        s.valueChangeInternal(null, from(pl.getBlockPos()));
                     }
                  })).aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.nbt-parsable.pos3.here.tooltips", ""))))
            )
            .Q(
               ExecutableWidget.instance(4 * half - half / 2, 0, half / 2, dy)
                  .eV(
                     new ButtonElement(
                           TextProvider.c(Text.translatableWithFallback("widget.nbt-parsable.pos3.zero", "Zero")),
                           ButtonAction.a(() -> s.valueChangeInternal(null, new Pos3(0, 0, 0)))
                        )
                        .aO(TooltipHandler.ap(ChatUtils.parseTranslation("widget.nbt-parsable.pos3.zero.tooltips", "")))
                  )
            );
      },
      new Pos3(0, 0, 0)
   );

   public static Pos3 from(BlockPos pos) {
      return new Pos3(pos.getX(), pos.getY(), pos.getZ());
   }

   public BlockPos to() {
      return new BlockPos(this.x, this.y, this.z);
   }

   @Override
   public NBTType<Pos3> type() {
      return TYPE;
   }

   public Pos3 withX(int x) {
      return this.x == x ? this : new Pos3(x, this.y, this.z);
   }

   public Pos3 withY(int y) {
      return this.y == y ? this : new Pos3(this.x, y, this.z);
   }

   public Pos3 withZ(int z) {
      return this.z == z ? this : new Pos3(this.x, this.y, z);
   }
}
