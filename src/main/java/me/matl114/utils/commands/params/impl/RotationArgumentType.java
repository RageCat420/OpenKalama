package me.matl114.utils.commands.params.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.ArgumentType;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.api.InputArgument;
import me.matl114.utils.commands.params.types.ExecuteRotation;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

public class RotationArgumentType extends AbstractArgumentType<ExecuteRotation>
        implements ArgumentType<ExecuteRotation> {
    public Stream<String> tabCompleteArguments(CommandExecution sender, List<InputArgument<?>> args) {
        InputArgument var3 = (InputArgument) args.get(args.size() - 1);
        if (!(var3 instanceof KalamaHelperHelperG var4)) {
            return Stream.empty();
        } else {
            String[] var5 = var4.getParsedArgument();
            int var6 = var5.length;
            if (var6 != 0 && !var5[0].isEmpty()) {
                boolean var13 = true;

                for (int var8 = 0; var8 < var6; var8++) {
                    var13 &= this.isPartOfRotation(var5[var8]);
                }

                if (!var13) {
                    return Stream.empty();
                } else {
                    int var14 = 2 - var6;
                    Vec2f var9 = this.currentRotation(sender);
                    String[] var10 = new String[] {"%.1f".formatted(var9.x), "%.1f".formatted(var9.y)};
                    String[] var11 = new String[] {"~", "~"};
                    if (var5[var6 - 1].isEmpty()) {
                        int var12 = var14 + 1;
                        return Stream.of(var10, var11).map(s -> String.join(" ", Arrays.copyOfRange(s, 2 - var12, 2)));
                    } else {
                        return Stream.of(var10, var11).map(s -> {
                            ArrayList var4x = new ArrayList();
                            var4x.add(var5[var6 - 1]);

                            for (int var5x = 2 - var14; var5x < 2; var5x++) {
                                var4x.add(s[var5x]);
                            }

                            return String.join(" ", var4x);
                        });
                    }
                }
            } else {
                Vec2f var7 = this.currentRotation(sender);
                return Stream.of(ExecuteRotation.fixed(var7.x, var7.y), ExecuteRotation.relative(3, 0.0F, 0.0F))
                        .map(ExecuteRotation::aog);
            }
        }
    }

    public RotationArgumentType(String argsName) {
        super(argsName);
    }

    protected ExecuteRotation l(String pitchStr, String yawStr) {
        byte var3 = 0;
        float var4 = 0.0F;
        float var5 = 0.0F;
        if (pitchStr.startsWith("~")) {
            var3 |= 1;
            var4 = this.parseFloatAfterPrefix(pitchStr, "~");
        } else {
            var4 = Float.parseFloat(pitchStr);
        }

        if (Float.isNaN(var4)) {
            return null;
        } else {
            if (yawStr.startsWith("~")) {
                var3 |= 2;
                var5 = this.parseFloatAfterPrefix(yawStr, "~");
            } else {
                var5 = Float.parseFloat(yawStr);
            }

            if (Float.isNaN(var5)) {
                return null;
            } else {
                return var3 == 0 ? ExecuteRotation.fixed(var4, var5) : ExecuteRotation.relative(var3, var4, var5);
            }
        }
    }

    protected float parseFloatAfterPrefix(String s, String prefix) {
        String var3 = s.substring(prefix.length());
        return var3.isEmpty() ? 0.0F : Float.parseFloat(var3);
    }

    @Override
    public Stream<String> getTab(CommandExecution sender, List<InputArgument<?>> args) {
        return Stream.concat(super.getTab(sender, args), this.tabCompleteArguments(sender, args));
    }

    @Nullable
    @Override
    public InputArgument<ExecuteRotation> consume(
            CommandExecution execution, List<InputArgument<?>> args, ArgumentReader reader) {
        if (!reader.hasNext()) {
            return new KalamaHelperHelperG(Optional.ofNullable(this.m), this, reader, reader.b());
        } else {
            int var4 = reader.b();
            String var5 = reader.f();
            Object var6 = null;
            if (reader.hasNext()) {
                String var7 = reader.f();

                try {
                    var6 = this.l(var5, var7);
                    return new KalamaHelperHelperG(Optional.ofNullable((ExecuteRotation) var6), this, reader, var4);
                } catch (Throwable var9) {
                }
            }

            reader.c(var4);
            return new KalamaHelperHelperG(Optional.ofNullable(this.m), this, reader, var4);
        }
    }

    private Vec2f currentRotation(CommandExecution sender) {
        Vector2f var2 = sender.so();
        return new Vec2f(var2.x, var2.y);
    }

    public boolean isPartOfRotation(String str) {
        if (str.startsWith("~")) {
            str = str.substring(1);
        }

        if (str.startsWith("-")) {
            str = str.substring(1);
        }

        try {
            if (str.isEmpty()) {
                return true;
            } else {
                Float.parseFloat(str);
                return true;
            }
        } catch (NumberFormatException var3) {
            return false;
        }
    }
}
