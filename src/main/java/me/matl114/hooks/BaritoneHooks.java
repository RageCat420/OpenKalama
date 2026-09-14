package me.matl114.hooks;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.Settings;
import baritone.api.Settings.Setting;
import baritone.api.event.events.ChatEvent;
import baritone.api.event.events.RotationMoveEvent;
import baritone.api.event.events.RotationMoveEvent.Type;
import baritone.api.event.listener.AbstractGameEventListener;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalComposite;
import baritone.api.pathing.goals.GoalGetToBlock;
import baritone.api.utils.BetterBlockPos;
import baritone.process.ElytraProcess;
import baritone.process.elytra.ElytraBehavior;
import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import me.matl114.events.Event;
import me.matl114.events.channels.EventChannel;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.Holder;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.PrimitiveList;
import me.matl114.hacks.utils.config.PrimitiveMap;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.hacks.utils.move.goal.GoalBlockPos;
import me.matl114.hacks.utils.move.goal.GoalDirection;
import me.matl114.hacks.utils.move.goal.GoalDynamic;
import me.matl114.hacks.utils.move.goal.GoalFollow;
import me.matl114.hacks.utils.move.goal.GoalList;
import me.matl114.hacks.utils.move.goal.GoalNear;
import me.matl114.hacks.utils.move.goal.GoalNearBlockPos;
import me.matl114.hacks.utils.move.goal.IPathGoal;
import me.matl114.hooks.impl.baritone.BaritoneFuture;
import me.matl114.hooks.impl.baritone.GoalDynamicGoal;
import me.matl114.hooks.impl.baritone.GoalNearManhattan;
import me.matl114.hooks.impl.baritone.GoalYawDirection;
import me.matl114.utils.config.ValueAccessor;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;

public abstract class BaritoneHooks implements IHooks {
   private static BaritoneHooks instance;
   public static List<BlockPos> currentNetherElytraPath = List.of();
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   public static final EventChannel<BaritoneFuture> landingEvent = new EventChannel<>();
   public static final EventChannel<BlockPos> elytraPathingEvent = new EventChannel<>();
   public static final EventChannel<Vec2f> moveRotEvent = new EventChannel<>();

   public static BaritoneHooks getInstance() {
      if (instance == null) {
         try {
            try {
               instance = new BaritoneHooks.MeteorBaritoneImpl();
            } catch (Throwable var1) {
               instance = new BaritoneHooks.UnknownBaritoneImpl();
            }
         } catch (Throwable var2) {
            instance = new BaritoneHooks.Default();
         }
      }

      return instance;
   }

   public abstract boolean handleCommand(String var1);

   public abstract Map<String, ValueAccessor<?>> getAllSettings();

   public abstract <T> ValueAccessor<T> getSetting(String var1);

   public abstract boolean isBaritoneElytraProcessing();

   public abstract boolean isBaritonePathing();

   public abstract void setBaritoneNetherPathSupplier(Supplier<List<BlockPos>> var1);

   public abstract void updateBaritoneNetherPath();

   public final List<BlockPos> getCurrentNetherPath() {
      return currentNetherElytraPath;
   }

   public abstract void setBaritoneCurrentElytraDestination(@Nullable BlockPos var1);

   public abstract void setBaritoneCurrentPathingDestination(@Nullable BlockPos var1);

   public abstract Vec2f getBaritoneCurrentMoveRot(ClientPlayerEntity var1);

   public abstract void setBaritoneCurrentGoal(IPathGoal var1);

   public abstract boolean isBaritoneGoalPathingActive();

   public abstract void cancelBaritone();

   public abstract String getCommandPrefix();

   public abstract boolean isBaritoneAPISupported();

   public abstract boolean isBaritoneVersionSupported();

   public static EventChannel<BaritoneFuture> getLandingEvent() {
      return landingEvent;
   }

   public static EventChannel<BlockPos> getElytraPathingEvent() {
      return elytraPathingEvent;
   }

   public static EventChannel<Vec2f> getMoveRotEvent() {
      return moveRotEvent;
   }

   public abstract static class AbstractBaritoneVersion extends BaritoneHooks {
      final Settings settings;
      final Map<String, ValueAccessor<?>> settingsMap = new LinkedHashMap<>();
      final ValueAccessor<String> prefix;

      public void onMoveRot(RotationMoveEvent event) {
         if (event.getType() == Type.MOTION_UPDATE && !moveRotEvent.d()) {
            Vec2f vec2f = new Vec2f(event.getPitch(), event.getYaw());
            Event<Vec2f> eventMe = new Event<>(vec2f, false, true);
            moveRotEvent.catchEvent(eventMe);
            if (vec2f != eventMe.b) {
               event.setPitch(eventMe.b.x);
               event.setYaw(eventMe.b.y);
            }
         }
      }

      public AbstractBaritoneVersion() {
         Class<?> checkClass = BaritoneAPI.class;
         this.settings = BaritoneAPI.getSettings();
         this.buildMap();
         this.prefix = this.getSetting("prefix");
         BaritoneAPI.getProvider().getPrimaryBaritone().getGameEventHandler().registerEventListener(new AbstractGameEventListener() {
            public void onPlayerRotationMove(RotationMoveEvent rotationMoveEvent) {
               AbstractBaritoneVersion.this.onMoveRot(rotationMoveEvent);
            }
         });
      }

      private void buildMap() {
         for (Setting re : this.settings.allSettings) {
            String name = re.getName();
            ValueAccessor accessor = ValueAccessor.of(() -> re.value, va -> re.value = va);
            this.settingsMap.put(name, accessor);
         }

         for (Field field : this.settings.getClass().getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
               field.setAccessible(true);
               String string = field.getName();

               try {
                  if (field.get(this.settings) instanceof Setting<?> setting) {
                     Object re = setting.value;
                     if (re == null) {
                        this.settingsMap.remove(string);
                     } else if (!(re instanceof Boolean) && !(re instanceof Number) && !(re instanceof String)) {
                        if (re instanceof Color) {
                           Setting<Color> colorValue = (Setting<Color>)setting;
                           ValueAccessor<?> accessor = ValueAccessor.of(() -> new WrapColor(colorValue.value), v -> colorValue.value = new Color(v.asRGB()));
                           this.settingsMap.put(string, accessor);
                        } else if (setting.value instanceof List) {
                           java.lang.reflect.Type listType = ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
                           java.lang.reflect.Type type = ((ParameterizedType)listType).getActualTypeArguments()[0];
                           if (type == Block.class) {
                              Setting<List<Block>> blockValue = (Setting<List<Block>>)setting;
                              ValueAccessor<EntrySet<Block>> accessor = ValueAccessor.of(
                                 () -> new EntrySet<>(Registries.BLOCK, blockValue.value), lst -> blockValue.value = lst.list()
                              );
                              this.settingsMap.put(string, accessor);
                           } else if (type == Item.class) {
                              Setting<List<Item>> itemValue = (Setting<List<Item>>)setting;
                              ValueAccessor<EntrySet<Item>> accessor = ValueAccessor.of(
                                 () -> new EntrySet<>(Registries.ITEM, itemValue.value), lst -> itemValue.value = lst.list()
                              );
                              this.settingsMap.put(string, accessor);
                           } else {
                              this.settingsMap.remove(string);
                           }
                        } else if (setting.value instanceof Map) {
                           if (setting != this.settings.buildValidSubstitutes && setting != this.settings.buildSubstitutes) {
                              this.settingsMap.remove(string);
                           } else {
                              Setting<Map<Block, List<Block>>> settingMapList = (Setting<Map<Block, List<Block>>>)setting;
                              ValueAccessor<PrimitiveMap<Holder<Block>, PrimitiveList<Holder<Block>>>> wtf = ValueAccessor.of(
                                 () -> {
                                    Map<Block, List<Block>> map = settingMapList.value;
                                    Map<Holder<Block>, PrimitiveList<Holder<Block>>> map2 = map.entrySet()
                                       .stream()
                                       .collect(
                                          Collectors.toMap(
                                             s -> Holder.of(Registries.BLOCK, s.getKey()),
                                             s -> new PrimitiveList<>(
                                                NBTTypes.t.cast(),
                                                s.getValue().stream().map(sss -> Holder.of(Registries.BLOCK, sss)).toList(),
                                                Holder.of(Registries.BLOCK, null)
                                             ),
                                             (k, v) -> v
                                          )
                                       );
                                    return new PrimitiveMap<>(
                                       NBTTypes.t.cast(),
                                       NBTTypes.z.cast(),
                                       map2,
                                       Holder.of(Registries.BLOCK, null),
                                       new PrimitiveList<>(NBTTypes.t.cast(), List.of(), Holder.of(Registries.BLOCK, null))
                                    );
                                 },
                                 v -> {
                                    Map<Holder<Block>, PrimitiveList<Holder<Block>>> map3 = v.map();
                                    settingMapList.value = map3.entrySet()
                                       .stream()
                                       .filter(s -> s.getKey().entry() != null)
                                       .collect(
                                          Collectors.toMap(
                                             s -> s.getKey().entry(),
                                             s -> s.getValue().list().stream().filter(ss -> ss.entry() != null).map(Holder::entry).toList(),
                                             (k, v2) -> v2
                                          )
                                       );
                                 }
                              );
                              this.settingsMap.put(string, wtf);
                           }
                        } else {
                           this.settingsMap.remove(string);
                        }
                     }
                  }
               } catch (Throwable var13) {
                  this.settingsMap.remove(string);
               }
            }
         }
      }

      @Override
      public Map<String, ValueAccessor<?>> getAllSettings() {
         return Collections.unmodifiableMap(this.settingsMap);
      }

      @Override
      public boolean isEnabled() {
         return true;
      }

      @Override
      public boolean handleCommand(String command) {
         String pfx = (String)BaritoneAPI.getSettings().prefix.value;
         command = command.startsWith(pfx) ? command : pfx + command;
         ChatEvent var4 = new ChatEvent(command);
         IBaritone var3;
         if ((var3 = BaritoneAPI.getProvider().getBaritoneForPlayer(BaritoneHooks.mc.player)) != null) {
            var3.getGameEventHandler().onSendChatMessage(var4);
            if (var4.isCancelled()) {
               return true;
            }
         }

         return false;
      }

      @Override
      public <T> ValueAccessor<T> getSetting(String name) {
         return (ValueAccessor<T>)(Object)this.settingsMap.get(name);
      }

      @Override
      public String getCommandPrefix() {
         return this.prefix == null ? "#" : this.prefix.getValue();
      }

      @Override
      public boolean isBaritoneAPISupported() {
         return true;
      }

      @Override
      public boolean isBaritoneElytraProcessing() {
         return BaritoneAPI.getProvider().getPrimaryBaritone().getElytraProcess().isActive();
      }

      @Override
      public boolean isBaritonePathing() {
         return BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing();
      }

      @Override
      public Vec2f getBaritoneCurrentMoveRot(ClientPlayerEntity player) {
         RotationMoveEvent moveEvent = new RotationMoveEvent(Type.MOTION_UPDATE, player.getYaw(), player.getPitch());
         BaritoneAPI.getProvider().getPrimaryBaritone().getGameEventHandler().onPlayerRotationMove(moveEvent);
         return new Vec2f(moveEvent.getPitch(), moveEvent.getYaw());
      }

      @Override
      public void setBaritoneCurrentElytraDestination(BlockPos pos) {
         if (pos != null) {
            BaritoneAPI.getProvider().getPrimaryBaritone().getElytraProcess().pathTo(pos);
         } else {
            BaritoneAPI.getProvider().getPrimaryBaritone().getElytraProcess().onLostControl();
         }
      }

      @Override
      public void cancelBaritone() {
         BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
      }

      @Override
      public void updateBaritoneNetherPath() {
         BaritoneAPI.getProvider().getPrimaryBaritone().getElytraProcess().resetState();
      }

      @Override
      public void setBaritoneCurrentPathingDestination(BlockPos pos) {
         if (pos != null) {
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalBlock(pos));
         } else {
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().onLostControl();
         }
      }

      @Override
      public void setBaritoneCurrentGoal(IPathGoal goal) {
         if (goal == null) {
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().onLostControl();
         } else {
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(this.convertGoal(goal));
         }
      }

      public Goal convertGoal(IPathGoal goal) {
         return (Goal)(switch (goal) {
            case GoalBlockPos pos -> new GoalBlock(pos.qC());
            case GoalNear near -> new GoalNearManhattan(near.center(), near.radius());
            case GoalNearBlockPos nearx -> new GoalGetToBlock(nearx.qC());
            case GoalList list -> new GoalComposite(list.AF().stream().map(this::convertGoal).toArray(Goal[]::new));
            case GoalFollow entity -> new GoalDynamicGoal(entity.Pc()::getPos, 0.3 + entity.Pc().getDimensions(entity.Pc().getPose()).width() / 2.0F);
            case GoalDynamic dynamic -> new GoalDynamicGoal(dynamic.supplier(), dynamic.radius());
            case GoalDirection direction -> new GoalYawDirection(BaritoneHooks.mc.player.getBlockPos(), direction.yaw());
            default -> throw new MatchException(null, null);
         });
      }

      @Override
      public boolean isBaritoneGoalPathingActive() {
         return BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().isActive();
      }
   }

   public static class Default extends BaritoneHooks {
      @Override
      public boolean isEnabled() {
         return false;
      }

      @Override
      public boolean handleCommand(String command) {
         return false;
      }

      @Override
      public Map<String, ValueAccessor<?>> getAllSettings() {
         return Map.of();
      }

      @Override
      public <T> ValueAccessor<T> getSetting(String name) {
         return null;
      }

      @Override
      public boolean isBaritoneElytraProcessing() {
         return false;
      }

      @Override
      public boolean isBaritonePathing() {
         return false;
      }

      @Override
      public void setBaritoneNetherPathSupplier(Supplier<List<BlockPos>> blockPos) {
      }

      @Override
      public void updateBaritoneNetherPath() {
      }

      @Override
      public void setBaritoneCurrentElytraDestination(BlockPos pos) {
      }

      @Override
      public void setBaritoneCurrentPathingDestination(BlockPos pos) {
      }

      @Override
      public Vec2f getBaritoneCurrentMoveRot(ClientPlayerEntity player) {
         return new Vec2f(player.getPitch(), player.getYaw());
      }

      @Override
      public void setBaritoneCurrentGoal(IPathGoal goal) {
      }

      @Override
      public boolean isBaritoneGoalPathingActive() {
         return false;
      }

      @Override
      public void cancelBaritone() {
      }

      @Override
      public String getCommandPrefix() {
         return "#";
      }

      @Override
      public boolean isBaritoneAPISupported() {
         return false;
      }

      @Override
      public boolean isBaritoneVersionSupported() {
         return false;
      }
   }

   public static class MeteorBaritoneImpl extends BaritoneHooks.AbstractBaritoneVersion {
      public static Supplier<List<BlockPos>> netherPathSupplier;

      public MeteorBaritoneImpl() {
         Class<?> clazz = ElytraProcess.class;
         clazz = ElytraBehavior.class;
         clazz = BetterBlockPos.class;
      }

      @Override
      public boolean isBaritoneVersionSupported() {
         return true;
      }

      @Override
      public void setBaritoneNetherPathSupplier(Supplier<List<BlockPos>> blockPos) {
         netherPathSupplier = blockPos;
      }
   }

   public static class UnknownBaritoneImpl extends BaritoneHooks.AbstractBaritoneVersion {
      @Override
      public void setBaritoneNetherPathSupplier(Supplier<List<BlockPos>> blockPos) {
      }

      @Override
      public boolean isBaritoneVersionSupported() {
         return false;
      }
   }
}
