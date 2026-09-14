package me.matl114.hacks;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.api.InputArgument;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class KalamaHelperHelperEX extends KalamaHelperHelperZX {
   public final Consumer<Text> b;
   public Optional<Vec3d> a = null;
   public ArgumentReader c;

   public KalamaHelperHelperEX(ArgumentReader arguments, CommandExecution player, List<InputArgument<?>> inputs, Consumer<Text> errMsg) {
      super(KalamaHelperHelperZ.UJ, player, inputs);
      this.c = arguments;
      this.b = errMsg;
   }

   public boolean hasResolved() {
      return this.a != null;
   }
}
