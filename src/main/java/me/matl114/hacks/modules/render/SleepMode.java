package me.matl114.hacks.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import java.util.function.Consumer;
import java.util.stream.Stream;
import me.matl114.commands.MainCommand;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.CharTypedAction;
import me.matl114.events.impl.KeyboardAction;
import me.matl114.events.impl.MouseClickAction;
import me.matl114.events.impl.MouseDragAction;
import me.matl114.events.impl.MouseMoveAction;
import me.matl114.events.impl.MouseScrollAction;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.RenderTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.IHotKey;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ClientUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.ScreenUtils;
import me.matl114.utils.collections.Point;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperH;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperK;
import me.matl114.utils.commands.commandGroup.SubCommand;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.KalamaHelperHelperA;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public class SleepMode extends BaseModule {
   public final KeyBindRef oq;
   boolean Rh;
   private Screen Rj;
   public static SleepMode INSTANCE;
   public final FlagRef sleepModeRunnerOptimize;
   public Screen Ri;
   private int sleepingLevel = 0;
   public final ModulePath ow = makePath(Configs.i, "render");
   public boolean Rk;
   public void aiA(Event<MouseScrollAction> event) {
      if (this.aim()) {
         event.cancel();
         if (this.Ri != null) {
            ScreenUtils.simulateMouseScroll(this.Ri, ((MouseScrollAction)event.b).horizontal(), ((MouseScrollAction)event.b).horizontal());
         }
      }
   }

   private void setCurrentRenderingSleeping(Screen screen) {
      BufferRenderer.reset();
      if (screen != null) {
         mc.mouse.unlockCursor();
         KeyBinding.unpressAll();
         this.Rj = screen;
         this.Rj.init(mc, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
      } else {
         this.Rj = null;
         if (mc.currentScreen != null) {
            mc.mouse.unlockCursor();
            KeyBinding.unpressAll();
         } else {
            mc.mouse.lockCursor();
            mc.getSoundManager().resumeAll();
         }
      }
   }

   public boolean sleepingRenderTick(GameRenderer gameRenderer, RenderTickCounter tickCounter) {
      if (this.ensureSleepingScreen()) {
         if (this.Rj != null) {
            this.Rk = true;
            if (this.Rk) {
               this.Rk = false;
               RenderSystem.clear(16640, MinecraftClient.IS_SYSTEM_MAC);
               int var3 = (int)(mc.mouse.getX() * mc.getWindow().getScaledWidth() / mc.getWindow().getWidth());
               int var4 = (int)(mc.mouse.getY() * mc.getWindow().getScaledHeight() / mc.getWindow().getHeight());
               Window var5 = mc.getWindow();
               RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
               Matrix4f var6 = new Matrix4f()
                  .setOrtho(
                     0.0F,
                     (float)(var5.getFramebufferWidth() / var5.getScaleFactor()),
                     (float)(var5.getFramebufferHeight() / var5.getScaleFactor()),
                     0.0F,
                     1000.0F,
                     21000.0F
                  );
               RenderSystem.setProjectionMatrix(var6, VertexSorter.BY_Z);
               Matrix4fStack var7 = RenderSystem.getModelViewStack();
               var7.pushMatrix();
               var7.translation(0.0F, 0.0F, -11000.0F);
               RenderSystem.applyModelViewMatrix();
               DiffuseLighting.enableGuiDepthLighting();
               DrawContext var8 = new DrawContext(mc, gameRenderer.buffers.getEntityVertexConsumers());
               this.Rj.renderWithTooltip(var8, var3, var4, tickCounter.getLastDuration());
               var8.draw();
               var7.popMatrix();
               RenderSystem.applyModelViewMatrix();
            }
         } else {
            this.setUpSleepingScreen(this.getDefaultDisplayText());
         }

         return true;
      } else {
         return false;
      }
   }

   public void onChunkData(Event<ChunkDataS2CPacket> dataS2CPacket) {
      if (this.Rh) {
         this.aii();
         if (!checkNull()) {
            if (this.Rh && mc.player.getY() > mc.world.getBottomY() + mc.world.getHeight()) {
               dataS2CPacket.cancel();
            }
         }
      }
   }

   public void setUpSleepingScreen(Text display) {
      if (this.Ri == null) {
         switch (this.sleepingLevel) {
            case 1:
               this.Ri = new RenderSubHelperT(this, "", display);
               break;
            default:
               this.Ri = new RenderSubHelperBX(this, Text.empty(), display);
         }
      }
   }

   public boolean wakeUpScreen() {
      if (this.setScreenSleeping(0)) {
         if (mc.player != null) {
            Debug.b(Text.literal("睡眠状态结束, 欢迎回来!").formatted(Formatting.GREEN));
         }

         return true;
      } else {
         return false;
      }
   }

   public void aii() {
      if (!this.aim()) {
         this.Rh = false;
      }
   }

   public String aiE() {
      return this.oq.get().c();
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.W(), this::ail, Integer.MIN_VALUE);
      this.registerListener(Listener.bo(), this::onSleepingResizeScreen, Integer.MIN_VALUE);
      this.registerListener(Listener.af(), this::aix, Integer.MIN_VALUE);
      this.registerListener(Listener.bp(), this::aiy, Integer.MIN_VALUE);
      this.registerListener(Listener.bq(), this::aiz, Integer.MIN_VALUE);
      this.registerListener(Listener.br(), this::aiA, Integer.MIN_VALUE);
      this.registerListener(Listener.bu(), this::interceptCharType, Integer.MIN_VALUE);
      this.registerListener(Listener.bs(), this::interceptMouseMove, Integer.MIN_VALUE);
      this.registerListener(Listener.bt(), this::interceptMouseDragged, Integer.MIN_VALUE);
      this.registerCommandBootstrap(this::aij);
      this.registerListener(Listener.ap().getChannel(ChunkDataS2CPacket.class), this::onChunkData, Integer.MIN_VALUE);
      this.registerListener(Listener.bv(), this::aiF, Integer.MIN_VALUE);
   }

   public Screen aiq() {
      return this.Rj;
   }

   public boolean setScreenSleeping(int s) {
      return this.setCustomScreenSleeping(s, null);
   }

   public void aiz(Event<MouseClickAction> event) {
      if (this.aim()) {
         event.cancel();
         if (this.Ri != null) {
            ScreenUtils.simulateMouseButton(this.Ri, ((MouseClickAction)event.b).eventButton(), ((MouseClickAction)event.b).action(), ((MouseClickAction)event.b).eventButton());
         }
      }
   }

   public boolean aim() {
      return this.sleepingLevel != 0;
   }

   public void interceptMouseDragged(Event<MouseDragAction> event) {
      if (this.aim()) {
         event.cancel();
         if (this.Ri != null) {
            this.Ri
               .mouseDragged(
                  ((MouseDragAction)event.b).mouseX(),
                  ((MouseDragAction)event.b).mouseY(),
                  ((MouseDragAction)event.b).mouse().activeButton,
                  ((MouseDragAction)event.b).mouseY(),
                  ((MouseDragAction)event.b).mouseX()
               );
         }
      }
   }

   public void aij(MainCommand mainCommand) {
      mainCommand.ay(
         new KalamaHelperHelperK(
            "sleep",
            SubCommand.bo()
               .a("sleep")
               .d("message.command.sleep.help")
               .h(KalamaHelperHelperA.a().B("level").f().v())
               .h(KalamaHelperHelperA.a().B("confirm").t(str -> {
                  int var1 = str.n();
                  return var1 > 0 ? Stream.of("confirm") : Stream.of("第一个参数请输入正整数");
               }).b("").v())
               .h(KalamaHelperHelperA.a().B("display").v())
               .g(e -> e.executor(KalamaHelperHelperH.i(this::aik)))
               .k()
         )
      );
   }

   public void ail(Event<GameRenderer> rendererEvent) {
      if (this.aim() && this.sleepingRenderTick((GameRenderer)rendererEvent.e(), rendererEvent.getArgs(0))) {
         rendererEvent.cancel();
      }
   }

   public void aiy(Event<KeyboardAction> event) {
      if (this.aim()) {
         event.cancel();
         if (((KeyboardAction)event.b).keyCode() == this.oq.get().getLastKey()) {
            this.wakeUpScreen();
            return;
         }

         if (this.Ri != null) {
            ScreenUtils.simulateKeyAction(this.Ri, (Integer)event.c[0], (Integer)event.c[1], (Integer)event.c[2], (Integer)event.c[3]);
         }
      }
   }

   public void aik(ArgumentInputStream re) {
      int var2 = re.nextClampedInt(1, 3);
      if (var2 != 1 && var2 != 2) {
         Debug.b("请输入范围内的数字: 1~2");
      } else {
         String var3 = re.n();
         String var4 = re.f();
         if ("confirm".equals(var3)) {
            if (this.sleepModeRunnerOptimize.get()) {
               this.Rh = true;
            }

            Tasks.l(() -> RenderTasks.E().setCustomScreenSleeping(var2, var4), 1);
         } else {
            Debug.b("使用sleep confirm 确认进入睡眠模式, 进入睡眠模式后可以按 " + RenderTasks.E().aiE() + " 键离开");
         }
      }
   }

   public void interceptCharType(Event<CharTypedAction> event) {
      if (this.aim()) {
         event.cancel();
         if (this.Ri != null) {
            this.Ri.charTyped(((CharTypedAction)event.b).chr(), ((CharTypedAction)event.b).codepoint());
         }
      }
   }

   public void interceptMouseMove(Event<MouseMoveAction> event) {
      if (this.aim()) {
         event.cancel();
         if (this.Ri != null) {
            this.Ri.mouseMoved(((MouseMoveAction)event.b).mouseX(), ((MouseMoveAction)event.b).mouseX());
         }
      }
   }

   @Override
   public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
      super.addCustomWidgets(acceptor, dx, dy, dblank);
      acceptor.accept(this.createTitleLabel("widget.sleep-mode.command", 0, dblank, dx, dy));
   }

   public SleepMode() {
      super("SleepMode");
      this.oq = this.hotkey(Configs.i, this.ow.add("wake-up-screen").toPath()).defaultValue(new MultiKeyBind(300)).build();
      this.sleepModeRunnerOptimize = this.flagBuilder(this.ow.add("sleep-mode-runner-optimize")).build();
      this.Rh = false;
      this.Rk = false;
      INSTANCE = this;
   }

   public void aix(Event<Screen> event) {
      if (this.aim()) {
         event.cancel();
      }
   }

   public void onSleepingResizeScreen(Event<Point> event) {
      if (this.Rj != null) {
         this.Rj.resize(mc, ((Point)event.b).a, ((Point)event.b).b);
      }
   }

   public void aiF(Event<IHotKey> eventHotKey) {
      if (this.aim()) {
         eventHotKey.cancel();
      }
   }

   public boolean ensureSleepingScreen() {
      if (!this.aim()) {
         return false;
      } else {
         boolean var1 = false;
         if (ClientUtils.isPlayerOnline()) {
            if (this.Rj != this.Ri) {
               this.setCurrentRenderingSleeping(this.Ri);
               var1 = true;
            }
         } else if (!(this.Ri instanceof RenderSubHelperC)) {
            this.setCurrentRenderingSleeping(this.Ri = new RenderSubHelperC(this));
            var1 = true;
         }

         if (var1) {
            mc.getFramebuffer().clear(true);
            mc.getFramebuffer().endRead();
            mc.getFramebuffer().beginWrite(true);
            return true;
         } else {
            return true;
         }
      }
   }

   public boolean setCustomScreenSleeping(int s, String sleep) {
      if (this.sleepingLevel != s) {
         if (s != 0) {
            this.sleepingLevel = s;
            this.setUpSleepingScreen((Text)(sleep == null ? this.getDefaultDisplayText() : Text.literal(sleep)));
         } else {
            this.sleepingLevel = s;
            this.Ri = null;
            this.Rj = null;
            if (mc.currentScreen == null) {
               mc.setScreen(null);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public Text getDefaultDisplayText() {
      return Text.literal("按 " + this.aiE() + " 键退出休眠模式");
   }
}
