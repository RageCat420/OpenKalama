package me.matl114.hacks;

import java.util.Map;
import me.matl114.hacks.utils.recipes.IRecipeEntry;
import me.matl114.utils.ClientUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.commands.commandGroup.AbstractMainCommand;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperH;
import me.matl114.utils.commands.commandGroup.SubCommand;
import me.matl114.utils.commands.commandGroup.TreeSubCommand;
import me.matl114.utils.commands.params.ArgumentInputStream;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class KalamaHelperHelperY extends AbstractMainCommand {
   public TreeSubCommand main = this.bC().a("sf").k();

   public KalamaHelperHelperY() {
      this.bI("sf");
      this.main
         .subBuilder(SubCommand.bo())
         .u("give")
         .x("message.command.sf.give.help")
         .A(me.matl114.utils.commands.params.KalamaHelperHelperA.a().B("id").d(() -> SlimefunTasks.n().keySet().stream()).v())
         .A(me.matl114.utils.commands.params.KalamaHelperHelperA.a().B("amount").g(1).v())
         .z(e -> e.executor(KalamaHelperHelperH.i(this::onGive)))
         .r();
      this.main
         .subBuilder(SubCommand.bo())
         .u("view")
         .x("message.command.sf.view.help")
         .A(me.matl114.utils.commands.params.KalamaHelperHelperA.a().B("id").d(() -> SlimefunTasks.n().keySet().stream()).v())
         .z(e -> e.executor(KalamaHelperHelperH.i(this::cl)))
         .r();
      this.main.subBuilder(SubCommand.bo()).u("banlist").x("message.command.sf.banlist.help").z(e -> e.executor(KalamaHelperHelperH.i(this::cm))).r();
   }

   public void cm(ArgumentInputStream re) {
      String var2 = "/sf unbanitem ";
      ClientUtils.getServerCommandTabResult(var2).thenAccept(s -> {
         Debug.b("禁用粘液物品列表");
         Map var1 = SlimefunTasks.n();

         for (String var3 : s) {
            IRecipeEntry var4 = (IRecipeEntry)var1.get(var3);
            Text var5 = null;
            if (var4 != null) {
               var5 = var4.Ct().getName();
            }

            if (var5 != null) {
               Debug.chat(var3, " (", var5, ")");
            } else {
               Debug.b(var3);
            }
         }

         Debug.b("注:当前列表可能不全,如果服务器禁用物品过多");
      });
   }

   public void onGive(ArgumentInputStream re) {
      String var2 = re.n();
      if (SlimefunTasks.n().containsKey(var2)) {
         IRecipeEntry var3 = SlimefunTasks.n().get(var2);
         ItemStack var4 = var3.Ct().copyWithCount(re.nextInt());
         String var5 = InvTasks.createGiveCommand(var4);
         ChatTasks.sayMessage(var5, true);
      } else {
         Debug.chat("不存在的id: ", var2);
      }
   }

   public void cl(ArgumentInputStream re) {
      String var2 = re.n();
      if (SlimefunTasks.n().containsKey(var2)) {
         IRecipeEntry var3 = SlimefunTasks.n().get(var2);
         if (var3 != null) {
            SlimefunTasks.w().adX(var3);
         } else {
            Debug.b("未知错误!");
         }
      } else {
         Debug.chat("不存在的id: ", var2);
      }
   }
}
