package me.matl114.hacks;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.api.InputArgument;
import me.matl114.utils.commands.params.impl.AbstractArgumentType;
import me.matl114.utils.commands.params.impl.PosArgumentResult;
import me.matl114.utils.commands.params.types.ExecutePos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.function.Consumers;
import org.jetbrains.annotations.Nullable;

public class KalamaHelperHelperM extends AbstractArgumentType<ExecutePos> {
    public KalamaHelperHelperM(String argsName) {
        super(argsName);
    }

    public Stream<String> j(CommandExecution sender, List<InputArgument<?>> args) {
        return args.isEmpty() ? Stream.empty() : this.aC(MovTasks.ac(sender, args).stream(), args);
    }

    @Override
    public Stream<String> getTab(CommandExecution sender, List<InputArgument<?>> args) {
        return Stream.concat(super.getTab(sender, args), this.j(sender, args));
    }

    @Nullable
    @Override
    public InputArgument<ExecutePos> consume(
            CommandExecution execution, List<InputArgument<?>> args, ArgumentReader reader) {
        if (reader.hasNext()) {
            int var4 = reader.b();
            Optional<Vec3d> var5 = MovTasks.ab(execution, args, reader, Consumers.nop());
            return var5 != null
                    ? new PosArgumentResult(var5.map(ExecutePos::of), this, reader, var4)
                    : new PosArgumentResult(null, this, reader, var4);
        } else {
            return new PosArgumentResult(null, this, reader, reader.b());
        }
    }
}
