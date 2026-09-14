package me.matl114.hacks;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.api.InputArgument;
import me.matl114.utils.commands.params.impl.PosArgumentResult;
import me.matl114.utils.commands.params.types.ExecutePos;
import org.apache.commons.lang3.function.Consumers;
import org.jetbrains.annotations.Nullable;

public class KalamaHelperHelperUX extends me.matl114.utils.commands.params.impl.KalamaHelperHelperA {
   public KalamaHelperHelperUX(String argsName) {
      super(argsName);
   }

   @Nullable
   @Override
   public InputArgument<ExecutePos> consume(CommandExecution execution, List<InputArgument<?>> args, ArgumentReader reader) {
      if (reader.hasNext()) {
         int var4 = reader.b();
         Optional<net.minecraft.util.math.Vec3d> var5 = MovTasks.ab(execution, args, reader, Consumers.nop());
         if (var5 != null) {
            return new PosArgumentResult(var5.map(ExecutePos::of), this, reader, var4);
         } else {
            reader.c(var4);
            return super.consume(execution, args, reader);
         }
      } else {
         return new PosArgumentResult(null, this, reader, reader.b());
      }
   }

   @Override
   public Stream<String> getTab(CommandExecution sender, List<InputArgument<?>> args) {
      return Stream.concat(super.getTab(sender, args), this.j(sender, args));
   }

   public Stream<String> j(CommandExecution sender, List<InputArgument<?>> args) {
      return args.isEmpty() ? Stream.empty() : this.aC(MovTasks.ac(sender, args).stream(), args);
   }
}
