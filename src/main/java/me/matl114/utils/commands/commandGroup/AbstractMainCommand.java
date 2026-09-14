package me.matl114.utils.commands.commandGroup;

import com.google.common.base.Supplier;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.commands.interruption.ArgumentException;
import me.matl114.utils.commands.interruption.InterruptionHandler;
import me.matl114.utils.commands.interruption.InvalidExecutorError;
import me.matl114.utils.commands.interruption.LogicalError;
import me.matl114.utils.commands.interruption.PermissionDenyError;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.KalamaHelperHelperA;
import me.matl114.utils.commands.params.api.CommandExecution;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class AbstractMainCommand implements SubCommand, InterruptionHandler {
   private final KalamaHelperHelperF p = new KalamaHelperHelperF("");

   public KalamaHelperHelperE<TreeSubCommand> bC() {
      return SubCommand.bq((a, b, c) -> {
         TreeSubCommand var4 = new TreeSubCommand(a, c);
         this.p.registerSub(var4);
         var4.conditional(true);
         return var4;
      });
   }

   @Override
   public void handleTypeError(
      CommandExecution sender,
      @Nullable ArgumentReader reader,
      @Nullable String argument,
      me.matl114.utils.commands.interruption.KalamaHelperHelperH type,
      String input
   ) {
      StringBuilder var6 = this.bK(reader);
      if (argument != null) {
         var6.append("&c类型错误:参数\"").append(argument).append("\"需要输入一个").append(type.ahE()).append(",但是输入了:").append(input);
      } else {
         var6.append("&c类型错误: 需要输入一个").append(type.ahE()).append(",但是输入了:").append(input);
      }

      this.bE(sender, var6.toString());
   }

   @Override
   public ArgumentInputStream b(CommandExecution execution, ArgumentReader reader) {
      return new ArgumentInputStream(execution, reader, List.of(), List.of());
   }

   public static KalamaHelperHelperA bQ(String... args) {
      return new KalamaHelperHelperA(args);
   }

   @Override
   public boolean onCustomCommand(@NotNull CommandExecution var1, ArgumentReader reader) throws ArgumentException {
      return this.p.onCustomCommand(var1, reader);
   }

   public String bH() {
      return this.p.c();
   }

   public static Supplier<Stream<String>> bS() {
      return () -> Stream.of("0.0", "1.0", "2.0", "3.0", "3.14159", "1.57079", "6.283185");
   }

   protected void bE(CommandExecution sender, String message) {
      sender.sm(message);
   }

   @Override
   public void setPermission(String permission) {
      this.p.setPermission(permission);
   }

   public void bP(CommandExecution sender, String permission, ArgumentReader argument) {
      if (!sender.hasPermission(permission)) {
         throw new PermissionDenyError(permission, argument);
      }
   }

   @Override
   public Stream<String> getHelp(String prefix) {
      return this.p.getHelp(prefix);
   }

   @Override
   public Stream<String> f(CommandExecution sender, ArgumentReader reader) {
      return this.p.f(sender, reader);
   }

   @Deprecated
   protected SubCommand bG(String name) {
      return new TreeSubCommand(name);
   }

   @Override
   public void handleValueOutOfRange(
      CommandExecution sender,
      @Nullable ArgumentReader reader,
      @Nullable String argument,
      me.matl114.utils.commands.interruption.KalamaHelperHelperH type,
      String range,
      @Nonnull String input
   ) {
      StringBuilder var7 = this.bK(reader);
      if (argument != null) {
         var7.append("&c值不在范围内: 参数 %s 输入了类型: %s, 需要在范围 %s 之间, 但是输入了%s".formatted(argument, type.ahE(), range, input));
      } else {
         var7.append("&c值不在范围内: 输入了类型: %s, 需要在范围 %s 之间, 但是输入了 %s".formatted(type.ahE(), range, input));
      }

      this.bE(sender, var7.toString());
   }

   public KalamaHelperHelperF bW() {
      return this.p;
   }

   @Override
   public void bA(CommandExecution sender, String fullMessage) {
      this.bE(sender, "&c执行该指令时出现逻辑错误: " + fullMessage);
   }

   @Override
   public void handleExecutorInvalid(CommandExecution sender, boolean shouldConsole) {
      if (shouldConsole) {
         this.bE(sender, "&c错误! 该指令只能在控制台执行");
      } else {
         this.bE(sender, "&c该指令只能在游戏内执行!");
      }
   }

   protected void bL(CommandExecution var1) {
      this.bE(var1, "&c你没有权限使用该指令!");
   }

   public static void bU(boolean argument, String... msg) {
      if (!argument) {
         throw new LogicalError(String.join(" ", msg));
      }
   }

   @Override
   public void bv(CommandExecution sender, @Nullable ArgumentReader reader, @Nonnull String argument) {
      StringBuilder var4 = this.bK(reader);
      if (reader != null) {
         var4.append("&c值缺失: 并未输入参数\"").append(argument).append("\"的值");
      } else {
         var4.append("&c值缺失: 并未输入参数\"").append(argument).append("\"的值");
      }

      this.bE(sender, var4.toString());
   }

   public boolean bX(PlayerEntity var1, String var3, String[] var4) {
      CommandExecution var4x = CommandExecution.sender(var1);

      try {
         return this.onCustomCommand(var4x, new ArgumentReader(var4));
      } catch (ArgumentException var6) {
         var6.handleAbort(var4x, this);
         return true;
      }
   }

   private StringBuilder bK(ArgumentReader reader) {
      return reader == null ? new StringBuilder() : new StringBuilder("&f" + reader.m() + "&c<--");
   }

   @Override
   public void bz(CommandExecution sender, String permission, @Nullable ArgumentReader commandNodeName) {
      if (commandNodeName == null) {
         this.bL(sender);
      } else {
         this.bE(sender, "&c你没有权限使用: " + commandNodeName.l());
      }
   }

   @Nonnull
   public PlayerEntity bN(CommandExecution sender) {
      PlayerEntity var2 = sender.si();
      if (var2 instanceof PlayerEntity) {
         return var2;
      } else {
         throw new InvalidExecutorError(false);
      }
   }

   public static Supplier<Stream<String>> bR() {
      return () -> Stream.of("0", "1", "16", "64", "114514", "2147483647");
   }

   public void ay(SubCommand command) {
      KalamaHelperHelperF var2 = this.p;
      if (var2 instanceof KalamaHelperHelperD) {
         var2.registerSub(command);
      } else {
         throw new UnsupportedOperationException("Can not register");
      }
   }

   public void bM(CommandExecution sender, ArgumentReader command) {
      command.d();
      String var3 = command.l();
      sender.sm("/%s 全部指令".formatted(var3));
      this.f(sender, new ArgumentReader(command.n())).forEach(s -> this.bE(sender, "&a" + s));
   }

   public static void bV(Object object, String... msg) {
      if (object == null) {
         throw new LogicalError(String.join(" ", msg));
      }
   }

   protected void bF(CommandExecution sender, Text message) {
      sender.sn(message);
   }

   public void bJ(String required) {
      this.p.setPermission(required);
   }

   @Override
   public String c() {
      return this.p.c();
   }

   public static Supplier<Stream<String>> bT() {
      return () -> EntityUtils.getWorldPlayerNames(true);
   }

   @Override
   public List<String> e(CommandExecution sender, ArgumentReader arguments) {
      return this.p.e(sender, arguments);
   }

   @Override
   public void bB(CommandExecution sender, ArgumentReader reader) {
      this.bM(sender, reader);
   }

   public void bI(String name) {
      this.p.b = name;
   }

   public KalamaHelperHelperE<TreeSubCommand> bD() {
      return SubCommand.bq((a, b, c) -> {
         TreeSubCommand var4 = new TreeSubCommand(a, c);
         this.p.registerSub(new KalamaHelperHelperK(var4.c(), var4));
         return var4;
      });
   }

   public List<String> bY(PlayerEntity var1, String var3, String[] var4) {
      CommandExecution var4x = CommandExecution.sender(var1);

      try {
         return this.e(var4x, new ArgumentReader(this.c(), var4));
      } catch (Throwable var6) {
         return List.of();
      }
   }

   @org.jetbrains.annotations.Nullable
   @Override
   public String a() {
      return this.p.a();
   }

   @Override
   public void bw(CommandExecution sender, @Nullable ArgumentReader reader, @Nonnull String argument) {
      StringBuilder var4 = this.bK(reader);
      if (reader != null) {
         var4.append("&c值缺失: 参数\"").append(argument).append("\"解析失败");
      } else {
         var4.append("&c值缺失: 参数\"").append(argument).append("\"解析失败");
      }

      this.bE(sender, var4.toString());
   }

   public void bO(String permission, @Nullable ArgumentReader argument) {
      throw new PermissionDenyError(permission, argument);
   }
}
