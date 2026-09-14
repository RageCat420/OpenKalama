package me.matl114.hacks.modules.task;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import me.matl114.commands.MainCommand;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.ChatTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.PrimitivePairList;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.IHotKey;
import me.matl114.managers.input.IInputManager;
import me.matl114.managers.input.KeyCode;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.managers.input.SimpleInputManager;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperD;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperH;
import me.matl114.utils.commands.commandGroup.SubCommand;
import me.matl114.utils.commands.commandGroup.TaskSubCommand;
import me.matl114.utils.commands.commandGroup.TreeSubCommand;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.api.CommandExecution;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BindCommand extends BaseModule implements IHotKey {
   public final FlagRef ae;
   public final KeyBindRef J;
   private final ModulePath aD = makePath(Configs.r, "bind-command");
   public final NBTRef<PrimitivePairList<MultiKeyBind, String>> commands;
   private static final String hb = "bind-command";
   private static final IntList hd = new IntArrayList();
   public static BindCommand INSTANCE;

   @Override
   public boolean handleKeyInput(IInputManager manager, int keyCode, boolean isStateChanged, boolean isClicked) {
      boolean var5 = false;
      if (isStateChanged && isClicked) {
         if (checkNull()) {
            return var5;
         }

         for (Pair var7 : this.commands.get().list()) {
            MultiKeyBind var8 = (MultiKeyBind)var7.getFirst();
            if (!var8.g() && keyCode == var8.getLastKey() && var8.d()) {
               Event var9 = new Event<>(this, true, false, manager);
               Listener.bv().catchEvent(var9);
               if (!var9.d()) {
                  if (HotKeyUtils.isValidState()) {
                     this.kF((String)var7.getSecond());
                  }

                  if (var8.j()) {
                     Tasks.q(() -> {
                        if (!var8.d()) {
                           if (HotKeyUtils.isValidState()) {
                              this.kF((String)var7.getSecond());
                           }

                           return true;
                        } else {
                           return false;
                        }
                     }, 1, 1);
                  }

                  var5 |= !var8.k();
               }
            }
         }
      }

      return var5;
   }

   public void kA(MainCommand command) {
      TreeSubCommand var2 = command.bD().a("bindc").k();
      var2.<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
         .u("opengui")
         .x("message.command.bindc.opengui.help")
         .z(e -> e.executor(KalamaHelperHelperH.h(this::kB)))
         .r()
         .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
         .u("list")
         .x("message.command.bindc.list.help")
         .z(e -> e.executor(KalamaHelperHelperH.m(this::kC)))
         .r()
         .subBuilder(SubCommand.bo())
         .u("help")
         .x("message.command.bindc.help.help")
         .z(e -> e.executor(KalamaHelperHelperH.m(this::kD)))
         .r();
   }

   private void kD(CommandExecution execution, ArgumentInputStream args) {
      execution.sn(Text.literal("BindCommand 模块说明").formatted(Formatting.GREEN));
      execution.sn(Text.literal("该模块用于把自定义快捷键绑定到聊天文本、服务端指令或客户端指令。"));
      execution.sn(Text.literal("触发已配置的快捷键时，会自动发送对应内容。"));
   }

   public BindCommand() {
      super("BindCommand");
      this.ae = this.builder(this.aD.addEnable(), Boolean.class).defaultValue(true).build();
      this.J = this.moduleEntry(this.aD.addHotkey(), new MultiKeyBind(), this.aD.addEnable()).build();
      this.commands = this.builder(this.aD.add("commands"), PrimitivePairList.<MultiKeyBind, String>uA())
         .defaultValue(
            new PrimitivePairList<MultiKeyBind, String>(
               "widget.bind-command.hotkey", "widget.bind-command.command", NBTTypes.i, NBTTypes.g, List.of(Pair.of(new MultiKeyBind(), "/!!help"))
            )
         )
         .build();
      hd.addAll(KeyCode.getKeyMap().values());
      INSTANCE = this;
   }

   @Override
   public void registerAll() {
      super.registerAll();
      SimpleInputManager.h().registerHotKeys(this);
      this.registerCommandBootstrap(this::kA);
   }

   private void kF(String string) {
      ChatTasks.sayMessage(string, false);
   }

   @Override
   public void unregisterAll() {
      super.unregisterAll();
      SimpleInputManager.h().unregisterHotKeys(this);
   }

   @Override
   public void addRegisteredManager(IInputManager manager) {
   }

   private boolean kB() {
      return true;
   }

   @Override
   public String kG() {
      return "custom.module.bind-command";
   }

   private void kC(CommandExecution execution, ArgumentInputStream args) {
      List var3 = this.commands.get().list();
      execution.sn(Text.literal("bindc 当前绑定: " + var3.size() + " 条").formatted(Formatting.GREEN));

      for (int var4 = 0; var4 < var3.size(); var4++) {
         Pair var5 = (Pair)var3.get(var4);
         MultiKeyBind var6 = (MultiKeyBind)var5.getFirst();
         String var7 = var6 != null && !var6.g() ? var6.b() : "<empty>";
         execution.sn(Text.literal(var4 + 1 + ". " + var7 + " -> " + (String)var5.getSecond()));
      }
   }

   @Override
   public IntList kH() {
      return hd;
   }
}
