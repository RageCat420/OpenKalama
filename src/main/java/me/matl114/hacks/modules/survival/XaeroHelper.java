package me.matl114.hacks.modules.survival;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.ChatTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.move.Travel;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.PrimitiveList;
import me.matl114.hacks.utils.config.StringFormat;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.hooks.XaeroHooks;
import me.matl114.hooks.impl.xaeroplus.IMapDrawFeature;
import me.matl114.hooks.impl.xaeroplus.wrapper.LineWrapper;
import me.matl114.hooks.impl.xaerowaypoints.IXWaypoint;
import me.matl114.hooks.impl.xaerowaypoints.IXWaypointAccess;
import me.matl114.hooks.impl.xaerowaypoints.IXWaypointFactory;
import me.matl114.hooks.impl.xaeroworldmap.MapClickContext;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.CommonUtils;
import me.matl114.utils.MathUtils;
import me.matl114.utils.ScreenUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class XaeroHelper extends BaseModule {
   Set<ChunkPos> Ar;
   public final ModulePath aD = makePath(Configs.o, "xaero-map-extra.xaero-helper");
   public IMapDrawFeature Ap;
   public final NBTRef<WrapColor> loadedChunkRenderColor;
   public static XaeroHelper INSTANCE;
   final List<LineWrapper<?>> Aq;
   public final FlagRef Ag = this.flagBuilder(this.aD.add("loaded-chunk-render")).updateListener(this::Ov).build();
   public static final int[] cS = new int[]{1, 0, -1, 0};
   public static final String Ao = "slimefun_xaerohelper_loaded_chunk_render";
   private static final List<String> Ak = List.of("world", "world_path", "pos", "pos_str", "pos_x", "pos_y", "pos_z");
   public final NBTRef<PrimitiveList<StringFormat>> xaeroRightClickSuggestList;
   IXWaypoint Au;
   private static final Map<String, Object> As = ImmutableMap.builder()
      .put("pos", Text.translatable("message.module.xaero-helper.right-click-command.pos"))
      .put("pos_str", Text.translatable("message.module.xaero-helper.right-click-command.pos_str"))
      .put("pos_x", Text.translatable("message.module.xaero-helper.right-click-command.pos_x"))
      .put("pos_y", Text.translatable("message.module.xaero-helper.right-click-command.pos_y"))
      .put("pos_z", Text.translatable("message.module.xaero-helper.right-click-command.pos_z"))
      .build();
   public final FlagRef xplusBaritoneElytraPathFix;
   public static final int[] cR = new int[]{0, -1, 0, 1};
   IXWaypointAccess At;
   public final FlagRef enableXaeroRightClickCommand;
   public final NBTRef<PrimitiveList<StringFormat>> xaeroRightClickCommandList;
   public final FlagRef travelGoalSync;

   @Override
   public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
      super.addCustomWidgets(acceptor, dx, dy, dblank);
      acceptor.accept(
         this.createTitleLabel(
            XaeroHooks.getInstance().isXaeroWorldMapEnable() ? "widget.xaero-helper.xaero-worldmap-enable" : "widget.xaero-helper.xaero-worldmap-not-support",
            0,
            dblank,
            dx,
            dy
         )
      );
      acceptor.accept(
         this.createTitleLabel(
            XaeroHooks.getInstance().isXaeroMiniMapEnable() ? "widget.xaero-helper.xaero-minimap-enable" : "widget.xaero-helper.xaero-minimap-not-support",
            0,
            dblank,
            dx,
            dy
         )
      );
      acceptor.accept(
         this.createTitleLabel(
            XaeroHooks.getInstance().isXaeroPlusEnable() ? "widget.xaero-helper.xaero-plus-enable" : "widget.xaero-helper.xaero-plus-not-support",
            0,
            dblank,
            dx,
            dy
         )
      );
   }

   public void Ox(Set<ChunkPos> chunkPos) {
      if (!Objects.equals(chunkPos, this.Ar)) {
         this.Ar = chunkPos;
         LongOpenHashSet var2 = new LongOpenHashSet(chunkPos.size());

         for (ChunkPos var4 : chunkPos) {
            for (int var5 = 0; var5 < 4; var5++) {
               long var6 = this.Oy(var4.x, var4.z, cR[var5], cS[var5]);
               if (var2.contains(var6)) {
                  var2.remove(var6);
               } else {
                  var2.add(var6);
               }
            }
         }

         this.Aq.clear();
         var2.longStream().mapToObj(this::Oz).forEach(this.Aq::add);
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.V(), this::OA);
      this.registerListener(XaeroHooks.getWorldMapRightClickOption(), this::OB);
      this.registerListener(Listener.V(), this::OE);
   }

   public void OA(Event<ClientPlayerEntity> event) {
      if (this.Ag.get() && this.Ap != null) {
         HashSet var2 = new HashSet(100);

         for (Chunk var4 : CommonUtils.chunks(false)) {
            var2.add(var4.getPos());
         }

         this.Ox(var2);
      }
   }

   public XaeroHelper() {
      super("XaeroHelper");
      this.loadedChunkRenderColor = this.builder(this.aD.add("loaded-chunk-render-color"), WrapColor.class).defaultValue(new WrapColor(Formatting.RED)).build();
      this.xplusBaritoneElytraPathFix = this.flagBuilder(this.aD.add("xplus-baritone-elytra-path-fix")).build();
      this.enableXaeroRightClickCommand = this.flagBuilder(this.aD.add("enable-xaero-right-click-command")).build();
      this.xaeroRightClickCommandList = this.builder(this.aD.add("xaero-right-click-command-list"), PrimitiveList.type(StringFormat.class))
         .defaultValue(
            new PrimitiveList<>(NBTTypes.x, List.of(new StringFormat(Ak, "/tp {pos}"), new StringFormat(Ak, "/!!travel to {pos}")), new StringFormat(Ak, ""))
         )
         .build();
      this.xaeroRightClickSuggestList = this.builder(this.aD.add("xaero-right-click-suggest-list"), PrimitiveList.type(StringFormat.class))
         .defaultValue(new PrimitiveList<>(NBTTypes.x, List.of(), new StringFormat(Ak, "")))
         .build();
      this.travelGoalSync = this.flagBuilder(this.aD.add("travel-goal-sync")).build();
      this.Aq = new ArrayList<>();
      this.Ar = new HashSet<>();
      INSTANCE = this;
   }

   public void OB(Event<ArrayList<MapClickContext>> event) {
      if (this.enableXaeroRightClickCommand.get()) {
         RegistryKey var2 = event.getArgs(0);
         BlockPos var3 = event.getArgs(1);
         ImmutableMap var4 = ImmutableMap.builder()
            .put("world", var2.getValue().toString())
            .put("world_path", var2.getValue().getPath())
            .put("pos", "%d %d %d".formatted(var3.getX(), var3.getY(), var3.getZ()))
            .put("pos_str", "%d,%d,%d".formatted(var3.getX(), var3.getY(), var3.getZ()))
            .put("pos_x", String.valueOf(var3.getX()))
            .put("pos_y", String.valueOf(var3.getY()))
            .put("pos_z", String.valueOf(var3.getZ()))
            .build();

         for (StringFormat var6 : this.xaeroRightClickCommandList.get().list()) {
            String var7 = ChatUtils.l(Text.translatable("message.module.xaero-helper.right-click-command.command", new Object[]{var6.formatText(As)}));
            ((ArrayList)event.b).add(new MapClickContext(var7, (world, position) -> {
               String var4x = var6.format(var4);
               ChatTasks.sayMessage(var4x, false);
            }));
         }

         for (StringFormat var9 : this.xaeroRightClickSuggestList.get().list()) {
            String var10 = ChatUtils.l(Text.translatable("message.module.xaero-helper.right-click-command.suggest", new Object[]{var9.formatText(As)}));
            ((ArrayList)event.b).add(new MapClickContext(var10, (world, position) -> {
               String var4x = var9.format(var4);
               ScreenUtils.openChatScreen(var4x);
            }));
         }
      }
   }

   public void OE(Event<ClientPlayerEntity> eventVoid) {
      if (!checkNull()) {
         if (XaeroHooks.getInstance().isXaeroMiniMapEnable()) {
            IXWaypointFactory var2 = XaeroHooks.getInstance().getWaypointFactory();
            if (!Objects.equals(var2.getCurrentWorld(), mc.world.getRegistryKey())) {
               this.OC();
            } else {
               IXWaypointAccess var3 = var2.getCurrentWaypointSet();
               if (!Objects.equals(var3, this.At)) {
                  this.OC();
               }

               this.At = var3;
               if (this.travelGoalSync.get()) {
                  if (Travel.CW == null) {
                     this.OD();
                  } else {
                     Vec3d var4 = Travel.CW.getCurrentFlyingTarget();
                     BlockPos var5 = new BlockPos((int)var4.x, (int)Math.clamp(var4.y, -512.0, 512.0), (int)var4.z);
                     if (this.Au == null) {
                        this.Au = var2.createWaypoint(var5.getX(), var5.getY(), var5.getZ(), "[SFH] Travel", "T", Formatting.GREEN.ordinal(), 0, true, true);
                        this.At.add(this.Au);
                        this.At.requestRefresh();
                     }

                     this.At.update(this.Au, acc -> {
                        acc.setX(var5.getX());
                        acc.setY(var5.getY());
                        acc.setZ(var5.getZ());
                     });
                  }
               }
            }
         }
      }
   }

   public void Ov(boolean bl) {
      if (XaeroHooks.getInstance().isXaeroPlusEnable()) {
         if (bl) {
            this.Ap = XaeroHooks.getInstance()
               .getMapDrawFactory()
               .lines("slimefun_xaerohelper_loaded_chunk_render", this::Ow, () -> this.loadedChunkRenderColor.get().withAlpha(255), () -> 0.1F, 100);
            this.Ap.register();
         } else if (this.Ap != null) {
            this.Ap.unregister();
            this.Ap = null;
         } else {
            XaeroHooks.getInstance().getMapDrawFactory().unregisterId("slimefun_xaerohelper_loaded_chunk_render");
         }
      }
   }

   @Override
   public <W> void unregisterAll() {
      super.unregisterAll();
      this.Ov(false);
      this.OC();
   }

   public LineWrapper<?> Oz(long offset) {
      int var3 = MathUtils.e(offset);
      int var4 = MathUtils.f(offset);
      int var5 = var3 >> 1;
      int var6 = var4 >> 1;
      int var7 = var3 - var5;
      int var8 = var4 - var6;
      if (var5 == var7) {
         int var10 = Math.min(var8, var6);
         return new LineWrapper(var5 << 4, var10 + 1 << 4, var5 + 1 << 4, var10 + 1 << 4);
      } else {
         int var9 = Math.min(var7, var5);
         return new LineWrapper(var9 + 1 << 4, var6 << 4, var9 + 1 << 4, var6 + 1 << 4);
      }
   }

   private long Oy(int chunkX, int chunkZ, int dx, int dz) {
      int var5 = 2 * chunkX + dx;
      int var6 = 2 * chunkZ + dz;
      return MathUtils.packInt(var5, var6);
   }

   public List<LineWrapper<?>> Ow(int x, int y, int w, RegistryKey<World> dimension) {
      return mc.world != null && Objects.equals(mc.world.getRegistryKey(), dimension) ? this.Aq : List.of();
   }

   private void OD() {
      if (this.Au != null) {
         this.At.remove(this.Au);
         this.Au = null;
         this.At.requestRefresh();
      }
   }

   private void OC() {
      if (this.At != null && this.Au != null) {
         this.At.remove(this.Au);
         this.Au = null;
      }

      this.At = null;
   }
}
