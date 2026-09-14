package me.matl114.utils.commands.commandGroup;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.KalamaHelperHelperA;
import me.matl114.utils.commands.params.api.CommandExecution;
import net.minecraft.util.Language;
import org.jetbrains.annotations.NotNull;

public class SubCommandImpl implements SubCommand {
   String b;
   String[] help;
   String f;
   KalamaHelperHelperA e;

   @Override
   public String c() {
      return this.b;
   }

   @NotNull
   public ArgumentInputStream parseInput(CommandExecution execution, ArgumentReader args) {
      return this.e.c(execution, args);
   }

   @Override
   public String a() {
      return this.f;
   }

   @Override
   public Stream<String> getHelp(String prefix) {
      return Arrays.stream(this.help).map(s -> prefix + Language.getInstance().get(s, s));
   }

   @Override
   public void setPermission(String permission) {
      this.f = permission;
   }

   public SubCommandImpl(String name, KalamaHelperHelperA argsTemplate, List<String> help) {
      this(name, argsTemplate, help.toArray(String[]::new));
   }

   public SubCommandImpl(String name, KalamaHelperHelperA argsTemplate, String... help) {
      this.b = Objects.requireNonNull(name);
      this.e = argsTemplate;
      this.help = help;
   }

   public List<String> e(Object arg0, Object arg1) { return null; }

}
