package me.matl114.hacks.modules.render;

import java.util.Objects;
import me.matl114.events.Event;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.CommonUtils;
import me.matl114.utils.EntityUtils;
import me.matl114.utils.MathUtils;
import me.matl114.versioned.SupportVersion;
import me.matl114.versioned.api.VDrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class Hud extends IRender2DColoredModule {
   public NBTRef<Hud$HudElementSelectSet> elements;
   public final ModulePath lj = makePath(Configs.i, "in-game-hud");
   public final FlagRef useKmPH;
   public final ModulePath he = this.lj.add("hud");

   @Override
   public void hK(Event<Void> event) {
   }

   public void handleSpeedVertical(VDrawContext vdraw) {
      PlayerStateManager var2 = PlayerStateManager.INSTANCE;
      String var4;
      if (this.useKmPH.get()) {
         String var3 = "V: Avg:%.2fKm/h, Kwn:%.2fKm/h";
         var4 = var3.formatted(Math.abs(var2.jt.y) * 72.0, Math.abs(var2.jq.y) * 72.0);
      } else {
         String var5 = "V: Avg:%.2fm/s, Kwn:%.2fm/s";
         var4 = var5.formatted(Math.abs(var2.jt.y) * 20.0, Math.abs(var2.jq.y) * 20.0);
      }

      this.gj(vdraw, var4);
   }

   public void handleIcon(VDrawContext vdraw) {
      if (this.right2.get()) {
         vdraw.w(Identifier.tryParse("kalama:textures/custom/genshin_impact.png"), -80, 0, 0, 27, 0, 0.1F, 0.9F, 0.25F, 0.65F);
      } else {
         vdraw.w(Identifier.tryParse("kalama:textures/custom/genshin_impact.png"), 0, 80, 0, 27, 0, 0.1F, 0.9F, 0.25F, 0.65F);
      }

      vdraw.f().translate(0.0F, 27.0F);
   }

   @Override
   protected ModulePath createRoot() {
      return makePath(Configs.i, "in-game-hud").add("hud");
   }

   public void handleDirection(VDrawContext vdraw) {
      float var2 = mc.player.getYaw();
      int var3 = EntityUtils.x(var2);
      int var4 = EntityUtils.y(var2);
      String var5 = MathUtils.getDirectionName(var3, var4);
      String var6 = "X" + (var3 >= 0 ? "+" : "-") + "Z" + (var4 >= 0 ? "+" : "-");
      this.gj(vdraw, "%s, %s".formatted(var5, var6));
   }

   public void Zi(VDrawContext vdraw) {
      String var2 = "Fall dist: %.2f";
      PlayerStateManager var3 = PlayerStateManager.INSTANCE;
      this.gj(vdraw, var2.formatted(var3.jh));
   }

   public void handleSpeed(VDrawContext vdraw) {
      PlayerStateManager var2 = PlayerStateManager.INSTANCE;
      String var4;
      if (this.useKmPH.get()) {
         String var3 = "Avg:%.2fKm/h, Kwn:%.2fKm/h";
         var4 = var3.formatted(var2.jt.length() * 72.0, var2.jq.length() * 72.0);
      } else {
         String var5 = "Avg:%.2fm/s, Kwn:%.2fm/s";
         var4 = var5.formatted(var2.jt.length() * 20.0, var2.jq.length() * 20.0);
      }

      this.gj(vdraw, var4);
   }

   public void handleSpeedHorizontal(VDrawContext vdraw) {
      PlayerStateManager var2 = PlayerStateManager.INSTANCE;
      String var4;
      if (this.useKmPH.get()) {
         String var3 = "H: Avg:%.2fKm/h, Kwn:%.2fKm/h";
         var4 = var3.formatted(var2.jt.horizontalLength() * 72.0, var2.jq.horizontalLength() * 72.0);
      } else {
         String var5 = "H: Avg:%.2fm/s, Kwn:%.2fm/s";
         var4 = var5.formatted(var2.jt.horizontalLength() * 20.0, var2.jq.horizontalLength() * 20.0);
      }

      this.gj(vdraw, var4);
   }

   public Hud() {
      super("Hud");
      this.elements = this.builder(this.he.add("elements"), Hud$HudElementSelectSet.class).defaultValue(new Hud$HudElementSelectSet()).build();
      this.useKmPH = this.flagBuilder(this.he.add("use-kmPH")).build();
   }

   @Override
   public void render2D(VDrawContext vdraw, float partialTicks) {
      Hud$HudElementSelectSet var3 = this.elements.get();
      if (var3.getState(Hud$HudElement.UP)) {
         this.handleIcon(vdraw);
      }

      if (var3.getState(Hud$HudElement.UQ)) {
         this.handleCommonInfo(vdraw);
      }

      if (var3.getState(Hud$HudElement.UR)) {
         this.handleConnectionInfo(vdraw);
      }

      if (var3.getState(Hud$HudElement.US)) {
         this.Zf(vdraw);
      }

      if (var3.getState(Hud$HudElement.UT)) {
         this.handleDirection(vdraw);
      }

      if (var3.getState(Hud$HudElement.UU)) {
         this.handleRotation(vdraw);
      }

      if (var3.getState(Hud$HudElement.UV)) {
         this.Zi(vdraw);
      }

      if (var3.getState(Hud$HudElement.UW)) {
         this.handleSpeed(vdraw);
      }

      if (var3.getState(Hud$HudElement.UX)) {
         this.handleSpeedHorizontal(vdraw);
      }

      if (var3.getState(Hud$HudElement.UY)) {
         this.handleSpeedVertical(vdraw);
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
   }

   public void handleRotation(VDrawContext vdraw) {
      String var2 = "P:%.2f, Y: %.2f";
      PlayerStateManager var3 = PlayerStateManager.INSTANCE;
      this.gj(vdraw, var2.formatted(var3.jl, MathHelper.wrapDegrees(var3.jm)));
   }

   public void Zf(VDrawContext vdraw) {
      String var2 = "%.2f, %.2f, %.2f";
      String var3 = "Chunk: [%d %d]";
      PlayerStateManager var4 = PlayerStateManager.INSTANCE;
      int var5 = (int)var4.ji >> 4;
      int var6 = (int)var4.jj >> 4;
      this.gj(vdraw, var3.formatted(var5, var6));
      this.gj(vdraw, var2.formatted(var4.ji, var4.jk, var4.jj));
   }

   public void handleConnectionInfo(VDrawContext vdraw) {
      String var2 = "ip:%s, %s";
      this.gj(vdraw, var2.formatted(CommonUtils.getServerName(), mc.player.getNameForScoreboard()));
   }

   public void handleCommonInfo(VDrawContext vdraw) {
      SupportVersion var2 = ViaFabricPlusHooks.getInstance().getCurrentVersion();
      int var3 = 0;
      PlayerListEntry var4;
      if ((var4 = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid())) != null) {
         var3 = var4.getLatency();
      }

      MutableText var5 = ChatUtils.textFromLegacyString(
         "&aMCv" + var2 + (Objects.equals(var2, SupportVersion.CURRENT) ? "" : "(Via)") + " Fps:" + mc.getCurrentFps() + " " + var3 + "ms"
      );
      this.drawText(vdraw, var5);
   }
}
