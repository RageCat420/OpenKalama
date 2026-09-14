package me.matl114.events;

import com.google.common.collect.ImmutableSet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPipeline;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import me.matl114.accessors.events.ClientConnectionAccess;
import me.matl114.events.catchers.AbstractTypedPacketCatcher;
import me.matl114.events.channels.EventChannel;
import me.matl114.events.channels.KalamaHelperHelperC;
import me.matl114.events.channels.ListenerPoint;
import me.matl114.events.channels.PacketEventChannel;
import me.matl114.events.impl.CharTypedAction;
import me.matl114.events.impl.KalamaHelperHelperD;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.events.impl.KeyboardAction;
import me.matl114.events.impl.MouseClickAction;
import me.matl114.events.impl.MouseDragAction;
import me.matl114.events.impl.MouseMoveAction;
import me.matl114.events.impl.MouseScrollAction;
import me.matl114.events.impl.SlotClickAction;
import me.matl114.events.impl.UseItem;
import me.matl114.hacks.MovTasks$MovInfo;
import me.matl114.managers.Tasks;
import me.matl114.managers.input.IHotKey;
import me.matl114.utils.collections.KalamaHelperHelperM;
import me.matl114.utils.collections.Point;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.listener.ClientCookieRequestPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.ConfigPackets;
import net.minecraft.network.packet.CookiePackets;
import net.minecraft.network.packet.HandshakePackets;
import net.minecraft.network.packet.LoginPackets;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PingPackets;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.network.packet.StatusPackets;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.config.FeaturesS2CPacket;
import net.minecraft.network.packet.s2c.config.ResetChatS2CPacket;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkSentS2CPacket;
import net.minecraft.network.packet.s2c.play.StartChunkSendS2CPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.spongepowered.asm.mixin.Unique;

public class Listener {
   private static final EventChannel<KalamaHelperHelperM> ac;
   public static ClientConnection e;
   private static final Map<Class<?>, ArrayDeque<ListenerPoint>> aY;
   private static final EventChannel<ServerAddress> k;
   private static final EventChannel<ClientPlayerEntity> M;
   private static final KalamaHelperHelperC<SoundInstance> aV;
   private static final EventChannel<Integer> V;
   private static final EventChannel<Void> aA;
   private static final KalamaHelperHelperC<KalamaHelperHelperE> aQ;
   private static final EventChannel<CharTypedAction> aO;
   private static final EventChannel<KeyboardAction> aJ;
   private static final EventChannel<Void> i;
   private static final KalamaHelperHelperC<Entity> an;
   private static final EventChannel<HitResult> aF;
   private static final EventChannel<Void> at;
   private static final EventChannel<ClientPlayerEntity> g;
   private static final EventChannel<Vec3d> T;
   private static final EventChannel<Vec3d> W;
   private static final KalamaHelperHelperC<SoundInstance> aW;
   private static final EventChannel<me.matl114.events.impl.KalamaHelperHelperG> D;
   private static final EventChannel<ChannelPipeline> aS;
   private static final EventChannel<String> t;
   private static final KalamaHelperHelperC<Screen> C;
   private static final KalamaHelperHelperC<Vec3d> ah;
   private static final KalamaHelperHelperC<Entity> aj;
   private static final EventChannel<ClientPlayerEntity> N;
   private static final KalamaHelperHelperC<Particle> aU;
   private static final EventChannel<Vec3d> ad;
   private static final EventChannel<ClientPlayerEntity> P;
   private static final EventChannel<PlayerListEntry> af;
   private static final EventChannel<Integer> Q;
   private static final Map<Class<? extends Packet<?>>, Class<? extends Packet<?>>> d;
   @Experimental
   private static final KalamaHelperHelperC<SerializedEntry<?>> O;
   private static final Set<Class<?>> aX;
   private static final KalamaHelperHelperC<Map<TagKey<?>, List<RegistryEntry<?>>>> l;
   private static final KalamaHelperHelperC<Screen> z;
   private static final EventChannel<MouseClickAction> aK;
   private static final EventChannel<Point> aI;
   private static final EventChannel<me.matl114.events.impl.KalamaHelperHelperH> aE;
   private static final EventChannel<UseItem> aC;
   private static final EventChannel<MouseMoveAction> aM;
   private static final KalamaHelperHelperC<KalamaHelperHelperI<?>> aR;
   private static final EventChannel<List<BiPredicate<BlockPos, BlockState>>> au;
   private static final PacketEventChannel I;
   private static final EventChannel<ClientPlayerEntity> Y;
   private static final EventChannel<Void> j;
   private static final KalamaHelperHelperC<Entity> ao;
   private static final KalamaHelperHelperC<Entity> al;
   private static final EventChannel<Vec3d> ab;
   private static final PacketEventChannel L;
   private static final KalamaHelperHelperC<SlotClickAction> F;
   private static final EventChannel<Vec3d> S;
   private static final EventChannel<UseItem> aB;
   private static final Function<Class<? extends Screen>, Class<? extends Screen>> f;
   @Experimental
   private static final EventChannel<Language> r;
   private static final EventChannel<ChunkPos> ar;
   private static final EventChannel<RecipeEntry<?>> G;
   private static final EventChannel<ChatHudLine> v;
   private static final EventChannel<HitResult> aG;
   private static final Map<PacketType<?>, Class<? extends Packet<?>>> a = new LinkedHashMap<>();
   private static final EventChannel<Boolean> aa;
   private static final EventChannel<PlayerListEntry> ag;
   public static final PacketEventChannel H;
   private static final EventChannel<PlayerListEntry> ae;
   private static final EventChannel<Map<BlockPos, BlockState>> aw;
   private static final EventChannel<Input> X;
   private static final EventChannel<Void> n;
   private static final EventChannel<Void> ay;
   private static final EventChannel<ClientPlayerEntity> R;
   private static final KalamaHelperHelperC<SlotClickAction> E;
   private static final EventChannel<Void> ax;
   private static final KalamaHelperHelperC<Screen> x;
   private static final EventChannel<ClientPlayerEntity> p;
   private static final EventChannel<Boolean> az;
   private static final EventChannel<World> h;
   private static final EventChannel<MinecraftClient> s;
   private static final EventChannel<MouseDragAction> aN;
   private static final EventChannel<Integer> Z;
   private static final Map<Class<?>, ArrayDeque<ListenerPoint>> aZ;
   private static final KalamaHelperHelperC<Screen> A;
   private static final EventChannel<KalamaHelperHelperD> aq;
   private static final KalamaHelperHelperC<BlockState> av;
   private static final EventChannel<Void> m;
   private static final EventChannel<HitResult> aH;
   private static final EventChannel<GameRenderer> q;
   private static final EventChannel<me.matl114.events.impl.KalamaHelperHelperH> aD;
   private static final EventChannel<HandledScreen<?>> B;
   private static final KalamaHelperHelperC<Entity> am;
   private static final PacketEventChannel J;
   private static final KalamaHelperHelperC<Entity> ai;
   private static final EventChannel<BlockEntityTickInvoker> ap;
   private static final Map<Identifier, PacketType<?>> b = new HashMap<>();
   private static final EventChannel<MovTasks$MovInfo> U;
   private static final EventChannel<ClientConnection> aT;
   private static final PacketEventChannel K;
   private static final Map<Identifier, PacketType<?>> c = new HashMap<>();
   private static final EventChannel<MouseScrollAction> aL;
   private static final KalamaHelperHelperC<Entity> ak;
   private static final EventChannel<Boolean> as;
   private static final EventChannel<ClientPlayerEntity> o;
   private static final KalamaHelperHelperC<Screen> y;
   private static final EventChannel<String> w;
   private static final EventChannel<Text> u;
   private static final EventChannel<IHotKey> aP;

   public static EventChannel<ClientPlayerEntity> as() {
      return M;
   }

   public static KalamaHelperHelperC<SoundInstance> bC() {
      return aW;
   }

   public static KalamaHelperHelperC<Screen> ad() {
      return x;
   }

   public static EventChannel<UseItem> bi() {
      return aC;
   }

   public static EventChannel<MouseDragAction> bt() {
      return aN;
   }

   public static EventChannel<ServerAddress> Q() {
      return k;
   }

   public static EventChannel<String> ac() {
      return w;
   }

   public static EventChannel<HitResult> bm() {
      return aG;
   }

   protected static <T extends Packet<?>> Consumer<Event<T>> h(Predicate<T> w) {
      return packetEvent -> {
         if (!packetEvent.d()) {
            boolean var2 = w.test(packetEvent.e());
            if (!var2) {
               packetEvent.cancel();
            }
         }
      };
   }

   public static PacketEventChannel ao() {
      return I;
   }

   public static EventChannel<ClientPlayerEntity> U() {
      return o;
   }

   public static EventChannel<me.matl114.events.impl.KalamaHelperHelperG> aj() {
      return D;
   }

   public static EventChannel<Integer> aF() {
      return Z;
   }

   public static EventChannel<ClientPlayerEntity> av() {
      return P;
   }

   public static Packet<?> r(ClientConnection connection, Packet<?> packet) {
      return unpackMultiPacket(connection, packet, true);
   }

   public static PacketEventChannel aq() {
      return K;
   }

   private static void E(ArrayDeque<ListenerPoint> re, Event<? extends Packet<?>> packet) {
      if (!packet.d()) {
         synchronized (re) {
            Iterator var3 = re.iterator();

            while (var3.hasNext()) {
               ListenerPoint var4 = (ListenerPoint)var3.next();
               boolean var5 = var4.handleValue(packet);
               if (var5) {
                  var3.remove();
               }

               if (packet.d()) {
                  return;
               }
            }
         }
      }
   }

   public static boolean isAsyncImportantPacket(Packet<?> packet) {
      return aX.contains(packet.getClass());
   }

   public static EventChannel<MovTasks$MovInfo> aA() {
      return U;
   }

   public static EventChannel<Void> P() {
      return j;
   }

   public static EventChannel<Boolean> aG() {
      return aa;
   }

   public static EventChannel<Integer> aB() {
      return V;
   }

   public static EventChannel<MinecraftClient> Y() {
      return s;
   }

   public static Map<PacketType<?>, Class<? extends Packet<?>>> K() {
      return a;
   }

   public static KalamaHelperHelperC<SlotClickAction> ak() {
      return E;
   }

   public static KalamaHelperHelperC<SoundInstance> bB() {
      return aV;
   }

   public static EventChannel<Vec3d> aC() {
      return W;
   }

   public static EventChannel<ChatHudLine> ab() {
      return v;
   }

   public static EventChannel<PlayerListEntry> aM() {
      return ag;
   }

   public static EventChannel<ClientPlayerEntity> V() {
      return p;
   }

   public static EventChannel<IHotKey> bv() {
      return aP;
   }

   public static EventChannel<Vec3d> ay() {
      return S;
   }

   protected static <T> Consumer<Event<T>> j(Consumer<T> w) {
      return packetEvent -> {
         if (!packetEvent.d()) {
            w.accept(packetEvent.e());
         }
      };
   }

   public static EventChannel<Input> aD() {
      return X;
   }

   public static EventChannel<Vec3d> az() {
      return T;
   }

   public static KalamaHelperHelperC<Screen> ag() {
      return A;
   }

   public static void postPacketListenerApplyPoint(Packet<?> packet, PacketListener listener) {
      if (MinecraftClient.getInstance().isOnThread()) {
         Event var2 = new Event<>(packet, false, false, listener);
         L.handleValue(var2);
      }
   }

   public static void k(Consumer<Packet<?>> packetListener, boolean isS2C) {
      if (isS2C) {
         v().k(j(packetListener));
      } else {
         w().k(j(packetListener));
      }
   }

   @Unique
   private static Packet<?> unpackMultiPacket(ClientConnection connection, Packet<?> packet, boolean isS2C) {
      if (packet instanceof BundleS2CPacket var3) {
         Iterable<Packet<? super ClientPlayPacketListener>> var4 = var3.getPackets();
         List<Packet<? super ClientPlayPacketListener>> var5 = new ArrayList<>();
         boolean var6 = false;

         for (Packet<? super ClientPlayPacketListener> var8 : var4) {
            Packet<? super ClientPlayPacketListener> var9 = (Packet<? super ClientPlayPacketListener>)unpackMultiPacket(connection, var8, isS2C);
            if (var9 != null) {
               var5.add(var9);
               if (var9 != var8) {
                  var6 = true;
               }
            } else {
               var6 = true;
            }
         }

         if (var6) {
            return var5.isEmpty() ? null : new BundleS2CPacket(var5);
         } else {
            return packet;
         }
      } else {
         return t(connection, packet, isS2C);
      }
   }

   public static EventChannel<ClientPlayerEntity> M() {
      return g;
   }

   private static <T extends Packet<?>, W extends Packet<?>> Class<W> g(Class<T> packet) {
      Class var1 = packet;

      while (Packet.class.isAssignableFrom(var1.getSuperclass())) {
         var1 = var1.getSuperclass();
      }

      return var1;
   }

   public static PacketEventChannel ap() {
      return J;
   }

   public static <T extends Packet<?>> void o(Class<T> clazz, Predicate<T> predicate) {
      e(clazz).k(h(predicate));
   }

   public static KalamaHelperHelperC<Vec3d> aN() {
      return ah;
   }

   public static KalamaHelperHelperC<Screen> af() {
      return z;
   }

   public static KalamaHelperHelperC<Entity> aR() {
      return al;
   }

   public static EventChannel<ClientConnection> bz() {
      return aT;
   }

   public static KalamaHelperHelperC<Entity> aP() {
      return aj;
   }

   public static EventChannel<MouseMoveAction> bs() {
      return aM;
   }

   public static KalamaHelperHelperC<SerializedEntry<?>> au() {
      return O;
   }

   public static EventChannel<ClientPlayerEntity> ax() {
      return R;
   }

   public static EventChannel<KeyboardAction> bp() {
      return aJ;
   }

   public static void callPacketHandleEvent(Packet<?> instance, PacketListener t, BiConsumer<Packet<?>, PacketListener> callback) {
      if (!prepacketListenerApplyPoint(instance, t)) {
         try {
            callback.accept(instance, t);
         } catch (OffThreadException var10) {
         } catch (ClassCastException | RejectedExecutionException var11) {
            throw var11;
         } catch (Throwable var12) {
            if (var12 instanceof CrashException var4 && var4.getCause() instanceof OutOfMemoryError) {
               throw var12;
            }

            if (handleException(var12, Listener$ExceptionType.rJ, instance, t)) {
               throw var12;
            }
         } finally {
            postPacketListenerApplyPoint(instance, t);
         }
      }
   }

   public static ClientConnectionAccess q() {
      return ClientConnectionAccess.of(e);
   }

   public static EventChannel<Void> bg() {
      return aA;
   }

   public static KalamaHelperHelperC<KalamaHelperHelperE> bw() {
      return aQ;
   }

   public static EventChannel<World> N() {
      return h;
   }

   public static EventChannel<CharTypedAction> bu() {
      return aO;
   }

   public static KalamaHelperHelperC<Entity> aO() {
      return ai;
   }

   public static EventChannel<HitResult> bl() {
      return aF;
   }

   public static EventChannel<Void> S() {
      return m;
   }

   protected static <T extends Packet<?>> Consumer<Event<T>> i(BiPredicate<ClientConnection, T> w) {
      return packetEvent -> {
         if (!packetEvent.d()) {
            boolean var2 = w.test((ClientConnection)packetEvent.getArgs(0), packetEvent.e());
            if (!var2) {
               packetEvent.cancel();
            }
         }
      };
   }

   public static EventChannel<ChunkPos> aX() {
      return ar;
   }

   public static EventChannel<Integer> aw() {
      return Q;
   }

   public static KalamaHelperHelperC<SlotClickAction> al() {
      return F;
   }

   static {
      b(CommonPackets.class);
      b(PlayPackets.class);
      b(LoginPackets.class);
      b(PingPackets.class);
      b(StatusPackets.class);
      b(HandshakePackets.class);
      b(ConfigPackets.class);
      b(CookiePackets.class);

      for (PacketType var1 : a.keySet()) {
         if (var1.side() == NetworkSide.SERVERBOUND) {
            b.put(var1.id(), var1);
         } else {
            c.put(var1.id(), var1);
         }
      }

      d = new ConcurrentHashMap<>();
      f = Util.memoize(clz -> {
         Class var1x = clz;

         while (var1x != Screen.class && var1x.getSuperclass() != Screen.class) {
            var1x = var1x.getSuperclass();
         }

         return var1x;
      });
      g = new EventChannel<>();
      h = new EventChannel<>();
      i = new EventChannel<>();
      j = new EventChannel<>();
      k = new EventChannel<>();
      l = new KalamaHelperHelperC<>(mapEvent -> mapEvent.getArgs(0), true);
      m = new EventChannel<>();
      n = new EventChannel<>();
      o = new EventChannel<>();
      p = new EventChannel<>();
      q = new EventChannel<>();
      r = new EventChannel<>();
      s = new EventChannel<>();
      t = new EventChannel<>();
      u = new EventChannel<>();
      v = new EventChannel<>();
      w = new EventChannel<>();
      x = new KalamaHelperHelperC<>(screen -> screen == null ? Screen.class : f.apply((Class<? extends Screen>)screen.getClass()));
      y = new KalamaHelperHelperC<>(screen -> screen == null ? Screen.class : f.apply((Class<? extends Screen>)screen.getClass()));
      z = new KalamaHelperHelperC<>(screen -> screen == null ? Screen.class : f.apply((Class<? extends Screen>)screen.getClass()));
      A = new KalamaHelperHelperC<>(screen -> screen == null ? Screen.class : f.apply((Class<? extends Screen>)screen.getClass()));
      B = new EventChannel<>();
      C = new KalamaHelperHelperC<>(screen -> screen == null ? Screen.class : f.apply((Class<? extends Screen>)screen.getClass()));
      D = new EventChannel<>();
      E = new KalamaHelperHelperC<>(Function.identity());
      F = new KalamaHelperHelperC<>(Function.identity());
      G = new EventChannel<>();
      H = new PacketEventChannel();
      I = new PacketEventChannel();
      J = new PacketEventChannel();
      K = new PacketEventChannel();
      L = new PacketEventChannel();
      M = new EventChannel<>();
      N = new EventChannel<>();
      O = new KalamaHelperHelperC<>(e -> e.<Entity>getArgs(0).getType(), true);
      P = new EventChannel<>();
      Q = new EventChannel<>();
      R = new EventChannel<>();
      S = new EventChannel<>();
      T = new EventChannel<>();
      U = new EventChannel<>();
      V = new EventChannel<>();
      W = new EventChannel<>();
      X = new EventChannel<>();
      Y = new EventChannel<>();
      Z = new EventChannel<>();
      aa = new EventChannel<>();
      ab = new EventChannel<>();
      ac = new EventChannel<>();
      ad = new EventChannel<>();
      ae = new EventChannel<>();
      af = new EventChannel<>();
      ag = new EventChannel<>();
      ah = new KalamaHelperHelperC<>(event -> event.<Entity>getArgs(0).getType(), true);
      ai = new KalamaHelperHelperC<>(Entity::getType);
      aj = new KalamaHelperHelperC<>(Entity::getType);
      ak = new KalamaHelperHelperC<>(Entity::getType);
      al = new KalamaHelperHelperC<>(Entity::getType);
      am = new KalamaHelperHelperC<>(event -> event.getArgs(0), true);
      an = new KalamaHelperHelperC<>(Entity::getType);
      ao = new KalamaHelperHelperC<>(Entity::getType);
      ap = new EventChannel<>();
      aq = new EventChannel<>();
      ar = new EventChannel<>();
      as = new EventChannel<>();
      at = new EventChannel<>();
      au = new EventChannel<>();
      av = new KalamaHelperHelperC<>(AbstractBlockState::getBlock);
      aw = new EventChannel<>();
      ax = new EventChannel<>();
      ay = new EventChannel<>();
      az = new EventChannel<>();
      aA = new EventChannel<>();
      aB = new EventChannel<>();
      aC = new EventChannel<>();
      aD = new EventChannel<>();
      aE = new EventChannel<>();
      aF = new EventChannel<>();
      aG = new EventChannel<>();
      aH = new EventChannel<>();
      aI = new EventChannel<>();
      aJ = new EventChannel<>();
      aK = new EventChannel<>();
      aL = new EventChannel<>();
      aM = new EventChannel<>();
      aN = new EventChannel<>();
      aO = new EventChannel<>();
      aP = new EventChannel<>();
      aQ = new KalamaHelperHelperC<>(KalamaHelperHelperE::Wx);
      aR = new KalamaHelperHelperC<>(KalamaHelperHelperI::a);
      aS = new EventChannel<>();
      aT = new EventChannel<>();
      aU = new KalamaHelperHelperC<>(eve -> eve.<ParticleEffect>getArgs(0).getType(), true);
      aV = new KalamaHelperHelperC<>(SoundInstance::getId);
      aW = new KalamaHelperHelperC<>(SoundInstance::getId);
      aX = ImmutableSet.<Class<?>>builder()
         .add(CustomPayloadS2CPacket.class)
         .add(StartChunkSendS2CPacket.class)
         .add(ChunkSentS2CPacket.class)
         .add(PingResultS2CPacket.class)
         .add(DisconnectS2CPacket.class)
         .add(ResetChatS2CPacket.class)
         .add(FeaturesS2CPacket.class)
         .build();
      aY = new ConcurrentHashMap<>();
      aZ = new ConcurrentHashMap<>();
      aq().l(ev -> onPacketEventCatch(aY, ev), Integer.MIN_VALUE);
      w().l(ev -> onPacketEventCatch(aY, ev), Integer.MIN_VALUE);
      ar().l(ev -> onPacketEventCatch(aZ, ev), Integer.MIN_VALUE);
      ao().l(ev -> onPacketEventCatch(aZ, ev), Integer.MIN_VALUE);
      bz().k(Listener::onClientConnectionEstablish);
   }

   public static <T extends Packet<?>> void B(ListenerPoint packet) {
      Class var2 = packet instanceof AbstractTypedPacketCatcher var1 ? var1.packetClass : Packet.class;
      ArrayDeque var6 = aY.computeIfAbsent(var2, k -> new ArrayDeque<>());
      synchronized (var6) {
         var6.addLast(packet);
      }
   }

   public static EventChannel<Void> be() {
      return ay;
   }

   public static KalamaHelperHelperC<Entity> aQ() {
      return ak;
   }

   public static EventChannel<Void> O() {
      return i;
   }

   public static boolean handleException(Throwable e, Listener$ExceptionType type, Object... objects) {
      Event var3 = new Event<>(new KalamaHelperHelperE(type, e), true, false, objects);
      bw().b(var3);
      return !var3.d();
   }

   public static <T extends Packet<?>> void C(ListenerPoint packet) {
      Class var2 = packet instanceof AbstractTypedPacketCatcher var1 ? var1.packetClass : Packet.class;
      ArrayDeque var6 = aZ.computeIfAbsent(var2, k -> new ArrayDeque<>());
      synchronized (var6) {
         var6.addLast(packet);
      }
   }

   public static KalamaHelperHelperC<Screen> ai() {
      return C;
   }

   public static EventChannel<me.matl114.events.impl.KalamaHelperHelperH> bk() {
      return aE;
   }

   public static Class<? extends Packet<?>> c(Identifier id, boolean s2c) {
      return a.entrySet()
         .stream()
         .filter(type -> Objects.equals(type.getKey().id(), id) && type.getKey().side() == (s2c ? NetworkSide.CLIENTBOUND : NetworkSide.SERVERBOUND))
         .findAny()
         .map(Entry::getValue)
         .orElse(null);
   }

   public static void init() {
   }

   public static EventChannel<Void> bd() {
      return ax;
   }

   public static PacketEventChannel an() {
      return H;
   }

   public static EventChannel<Packet<?>> v() {
      return J.v();
   }

   public static <T extends Packet<?>, W extends Packet<?>> Class<W> f(Class<T> packet) {
      return (Class<W>)d.computeIfAbsent(packet, Listener::g);
   }

   public static <T extends Packet<?>> void n(Class<T> clazz, Consumer<T> predicate) {
      e(clazz).k(j(predicate));
   }

   public static KalamaHelperHelperC<KalamaHelperHelperI<?>> bx() {
      return aR;
   }

   public static ClientConnection L() {
      return e;
   }

   public static Packet<?> s(ClientConnection connection, Packet<?> packet) {
      return unpackMultiPacket(connection, packet, false);
   }

   private static void H(Channel channel, Packet<?> packet) {
      ChannelFuture var2 = channel.writeAndFlush(packet);
      var2.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }

   public static EventChannel<PlayerListEntry> aL() {
      return af;
   }

   public static EventChannel<Void> T() {
      return n;
   }

   public static KalamaHelperHelperC<Particle> bA() {
      return aU;
   }

   private static void b(Class<?> clazz) {
      for (Field var4 : clazz.getDeclaredFields()) {
         if (Modifier.isStatic(var4.getModifiers()) && PacketType.class.isAssignableFrom(var4.getType())) {
            try {
               PacketType var5 = (PacketType)var4.get(null);
               if (var4.getGenericType() instanceof ParameterizedType var7) {
                  Class var9 = (Class)var7.getActualTypeArguments()[0];
                  a.put(var5, var9);
               }
            } catch (IllegalAccessException var8) {
               throw new RuntimeException(var8);
            }
         }
      }
   }

   public static EventChannel<ChannelPipeline> by() {
      return aS;
   }

   public static KalamaHelperHelperC<Entity> aT() {
      return an;
   }

   @Unique
   private static Packet<?> t(ClientConnection connection, Packet<?> packet, boolean s2c) {
      Event var3 = new Event<>(packet, true, true, connection);
      ap().handleValue(var3);
      return var3.d() ? null : (Packet)var3.e();
   }

   public static void m(BiPredicate<ClientConnection, Packet<?>> packetListener, boolean isS2C) {
      if (isS2C) {
         v().k(i(packetListener));
      } else {
         w().k(i(packetListener));
      }
   }

   public static EventChannel<me.matl114.events.impl.KalamaHelperHelperH> bj() {
      return aD;
   }

   public static EventChannel<ClientPlayerEntity> at() {
      return N;
   }

   public static EventChannel<MouseScrollAction> br() {
      return aL;
   }

   public static EventChannel<Vec3d> aJ() {
      return ad;
   }

   public static EventChannel<Map<BlockPos, BlockState>> bc() {
      return aw;
   }

   public static EventChannel<RecipeEntry<?>> am() {
      return G;
   }

   public static EventChannel<Boolean> aY() {
      return as;
   }

   public static EventChannel<KalamaHelperHelperD> aW() {
      return aq;
   }

   public static void sendPacketNoEvents(Packet<?> packet) {
      ClientPlayNetworkHandler var1 = MinecraftClient.getInstance().getNetworkHandler();
      if (var1 != null) {
         G(var1.getConnection(), packet);
      }
   }

   public static boolean prepacketListenerApplyPoint(Packet<?> packet, PacketListener listener) {
      if (!MinecraftClient.getInstance().isOnThread()) {
         return false;
      } else {
         Event var2 = new Event<>(packet, true, false, listener);
         K.handleValue(var2);
         return var2.d();
      }
   }

   public static KalamaHelperHelperC<Entity> aS() {
      return am;
   }

   public static EventChannel<Void> aZ() {
      return at;
   }

   public static EventChannel<Boolean> bf() {
      return az;
   }

   public static EventChannel<List<BiPredicate<BlockPos, BlockState>>> ba() {
      return au;
   }

   public static EventChannel<BlockEntityTickInvoker> aV() {
      return ap;
   }

   public static void onPacketEventCatch(Map<Class<?>, ArrayDeque<ListenerPoint>> packetCatchers, Event<? extends Packet<?>> packet) {
      Packet var2 = (Packet)packet.e();
      ArrayDeque var3 = (ArrayDeque)packetCatchers.get(Packet.class);
      if (var3 != null) {
         E(var3, packet);
      }

      if (!packet.d()) {
         ArrayDeque var4 = (ArrayDeque)packetCatchers.get(f(var2.getClass()));
         if (var4 != null) {
            E(var4, packet);
         }
      }
   }

   public static KalamaHelperHelperC<Screen> ae() {
      return y;
   }

   public static void G(ClientConnection connection, Packet<?> packet) {
      connection.submit(con -> {
         Channel var2 = con.channel;
         if (var2.eventLoop().inEventLoop()) {
            H(var2, packet);
         } else {
            var2.eventLoop().execute(() -> H(var2, packet));
         }
      });
   }

   public static KalamaHelperHelperC<Map<TagKey<?>, List<RegistryEntry<?>>>> R() {
      return l;
   }

   public static EventChannel<String> Z() {
      return t;
   }

   public static EventChannel<PlayerListEntry> aK() {
      return ae;
   }

   public static void l(Predicate<Packet<?>> packetListener, boolean isS2C) {
      if (isS2C) {
         v().k(h(packetListener));
      } else {
         w().k(h(packetListener));
      }
   }

   public static EventChannel<HandledScreen<?>> ah() {
      return B;
   }

   public static EventChannel<Language> X() {
      return r;
   }

   public static EventChannel<Vec3d> aH() {
      return ab;
   }

   public static EventChannel<UseItem> bh() {
      return aB;
   }

   public static EventChannel<Packet<?>> w() {
      return J.u();
   }

   public static KalamaHelperHelperC<BlockState> bb() {
      return av;
   }

   public static EventChannel<KalamaHelperHelperM> aI() {
      return ac;
   }

   public static EventChannel<Text> aa() {
      return u;
   }

   public static EventChannel<HitResult> bn() {
      return aH;
   }

   public static PacketEventChannel ar() {
      return L;
   }

   public static <T extends Packet<?>> EventChannel<T> e(Class<T> clazz) {
      return J.getChannel(clazz);
   }

   public static KalamaHelperHelperC<Entity> aU() {
      return ao;
   }

   public static <T extends Packet<?>> void p(Class<T> clazz, BiPredicate<ClientConnection, T> predicate) {
      e(clazz).k(i(predicate));
   }

   public static EventChannel<ClientPlayerEntity> aE() {
      return Y;
   }

   public static EventChannel<MouseClickAction> bq() {
      return aK;
   }

   public static void onClientConnectionEstablish(Event<ClientConnection> event) {
      if (event.getArgs(0) == NetworkSide.CLIENTBOUND && event.getArgs(1) instanceof ClientCookieRequestPacketListener) {
         e = (ClientConnection)event.b;
         Tasks.m(() -> {
            if (e != null && e.isChannelAbsent() && !e.isOpen()) {
               e = null;
               return true;
            } else {
               return false;
            }
         }, 20, 20);
      }
   }

   public static PacketType<?> d(Identifier id, boolean s2c) {
      return (s2c ? c : b).get(id);
   }

   public static EventChannel<GameRenderer> W() {
      return q;
   }

   public static EventChannel<Point> bo() {
      return aI;
   }
}
