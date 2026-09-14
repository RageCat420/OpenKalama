package me.matl114.hacks.modules.combat;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.DoubleStream;
import me.matl114.commands.MainCommand;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.DrawableWidget;
import me.matl114.hacks.ChatTasks;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.HotKeyUtils;
import me.matl114.hacks.utils.config.EntityTypeRegex;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.OptionalPrimitive;
import me.matl114.hacks.utils.config.PrimitivePairList;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.hacks.utils.entity.CameraEntity;
import me.matl114.managers.Configs;
import me.matl114.managers.FileManager;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.ListRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.file.FileStorage;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.Debug;
import me.matl114.utils.MathUtils;
import me.matl114.utils.RaycastUtils;
import me.matl114.utils.WorldUtils;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperD;
import me.matl114.utils.commands.commandGroup.KalamaHelperHelperH;
import me.matl114.utils.commands.commandGroup.SubCommand;
import me.matl114.utils.commands.commandGroup.TaskSubCommand;
import me.matl114.utils.commands.commandGroup.TreeSubCommand;
import me.matl114.utils.commands.params.KalamaHelperHelperA;
import me.matl114.utils.config.BaseAttrKeyValue;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus.Experimental;

public class TargetSelector extends BaseModule {
   public final FileStorage aA;
   public static final String SY = "friend-list";
   public final FlagRef attNamed;
   public final FlagRef fakePlayerAndNpcDetect;
   private static final double[] cd = new double[]{1.62, 1.27, 0.4};
   public final NBTRef<Regex> friends;
   private static final double[] cc = new double[]{0.4, 1.62, 1.27};
   public final ModulePath Ju = makePath(Configs.k, "attack");
   public static TargetSelector INSTANCE;
   public final FlagRef attInvulnerable;
   public final FlagRef bV = this.flagBuilder(this.Ju.add("use-grim-expand-eye-height")).build();
   @Experimental
   public final FlagRef attTeammate;
   public final DoubleRef playerAttackMultiply;
   public final FlagRef attHostile;
   public CombatSubHelperM SZ;
   public final NBTRef<EntityTypeRegex> SL = this.builder(this.Ju.add("whitelist"), EntityTypeRegex.class)
      .defaultValue(new EntityTypeRegex(new Regex("^(monster|!endermite|player)$")))
      .build();
   public final NBTRef<OptionalPrimitive<String>> sendOnAddFriend;
   public final FlagRef oppositeAttackMultiply;
   public final KeyBindRef addFriendHotkey;
   public final FlagRef attFriend;
   public final KeyBindRef attFriendHotkey;

   private boolean isSameTeam(PlayerEntity pl, EquipmentSlot slot) {
      ItemStack var3 = pl.getEquippedStack(slot);
      if (var3.contains(DataComponentTypes.DYED_COLOR)) {
         ItemStack var4 = mc.player.getEquippedStack(slot);
         if (var4.contains(DataComponentTypes.DYED_COLOR) && Objects.equals(var3.get(DataComponentTypes.DYED_COLOR), var4.get(DataComponentTypes.DYED_COLOR))) {
            return true;
         }
      }

      return false;
   }

   public Entity akN(boolean commonBow) {
      return this.searchAimableEntity(commonBow, null);
   }

   public Entity searchAttackEntity(double nearby, boolean autoSelect) {
      return this.akJ(nearby, autoSelect, 0);
   }

   public Entity akL(double nearby, boolean autoSelect, int tickPredict, Predicate<Entity> predicate) {
      Predicate<Entity> var6 = predicate != null ? e -> this.canAttack(e) && predicate.test(e) : this::canAttack;
      return this.searchAttack(nearby, autoSelect, tickPredict, var6);
   }

   public Entity searchAimableEntity(boolean commonBow, Predicate<Entity> predicate) {
      Predicate<Entity> var3 = commonBow ? this::akv : this::canAttack;
      Predicate<Entity> var4 = predicate != null ? e -> var3.test(e) && predicate.test(e) : var3;
      return this.searchAimable(var4);
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerCommandBootstrap(this::akS);
   }

   public boolean onAddFriend() {
      if (mc.crosshairTarget.getType() == Type.ENTITY && ((EntityHitResult)mc.crosshairTarget).getEntity() instanceof PlayerEntity var2 && var2 != mc.player) {
         this.addFriend(var2.getNameForScoreboard(), "");
         return true;
      } else {
         return false;
      }
   }

   public TargetSelector() {
      super("TargetSelector");
      this.friends = this.builder(this.Ju.add("friends"), Regex.class).defaultValue(new Regex("^(.*NPC.*)$")).build();
      this.addFriendHotkey = this.hotkey(this.Ju.add("add-friend-hotkey"))
         .defaultValue(new MultiKeyBind())
         .registerHotkey(HotKeyUtils.c(this::onAddFriend))
         .build();
      this.sendOnAddFriend = this.builder(this.Ju.add("send-on-add-friend"), OptionalPrimitive.type(String.class))
         .defaultValue(new OptionalPrimitive<>(false, NBTTypes.g, "/msg %s I have just added you to my friends list!"))
         .build();
      this.attFriend = this.builder(this.Ju.add("att-friend"), FlagRef.TYPE).defaultValue(true).build();
      this.attFriendHotkey = this.moduleEntry(this.Ju.add("att-friend-hotkey"), new MultiKeyBind(), this.Ju.add("att-friend")).build();
      this.attNamed = this.builder(this.Ju.add("att-named"), Boolean.class).defaultValue(true).build();
      this.attTeammate = this.builder(this.Ju.add("att-teammate"), Boolean.class).defaultValue(true).build();
      this.attHostile = this.builder(this.Ju.add("att-hostile"), Boolean.class).defaultValue(true).build();
      this.attInvulnerable = this.flagBuilder(this.Ju.add("att-invulnerable")).build();
      this.oppositeAttackMultiply = this.flagBuilder(this.Ju.add("opposite-attack-multiply")).build();
      this.playerAttackMultiply = this.builder(this.Ju.add("player-attack-multiply"), DoubleRef.TYPE)
         .defaultValue(0.0)
         .show(this.oppositeAttackMultiply::get)
         .build();
      this.fakePlayerAndNpcDetect = this.flagBuilder(this.Ju.add("fake-player-and-npc-detect")).build();
      this.aA = FileManager.getInstance().o("friends.nbt");
      this.SZ = new CombatSubHelperM(List.of(), Map.of());
      ModulePath var1 = this.Ju.add("friend-list");
      if (var1.getConfig().contains(var1.toPath())) {
         ListRef var2 = var1.getConfig().getList(var1.toPath());
         var1.getConfig().setValueNoNew(null, var1.toPath());
         if (var2 != null) {
            this.akk(new CombatSubHelperM(var2.get(), Map.of()));
         }
      }

      try {
         this.SZ = this.aA.readOrThrow(CombatSubHelperM.CODEC);
      } catch (Throwable var3) {
      }

      INSTANCE = this;
   }

   public List<Entity> getAimableEntities(Predicate<Entity> predicate) {
      ArrayList var2 = new ArrayList();

      for (Entity var5 : ImmutableList.copyOf(mc.world.getEntities())) {
         if (predicate.test(var5) && canPlayerDirectlySee(var5)) {
            var2.add(var5);
         }
      }

      return var2;
   }

   public Entity akK(double nearby, boolean autoSelect, Predicate<Entity> predicate) {
      return this.akL(nearby, autoSelect, 0, predicate);
   }

   public boolean akv(Entity target) {
      return this.checkWeapon(target, true) && this.canAttack(target);
   }

   public CombatSubHelperM akT() {
      return this.SZ;
   }

   public boolean isWithinAttackRange(Vec3d pos, Entity entity) {
      return this.akm(pos, entity.getBoundingBox(), CombatExtra.INSTANCE.getAttackAtTargetRange(entity));
   }

   public static boolean canPlayerDirectlySee(Entity entity) {
      return entity.getPos().subtract(mc.player.getPos()).horizontalLengthSquared() < 90000.0
         && !RaycastUtils.raycastAnySolidBlock(mc.player, mc.player.getEyePos(), entity.getEyePos());
   }

   public void akt() {
      List var1 = this.SZ.toPairList();
      NBTRef var2 = new NBTRef<>(new PrimitivePairList<>("widget.friend-list.friend-name", "widget.friend-list.friend-alias", NBTTypes.g, NBTTypes.g, var1));
      BaseAttrKeyValue var3 = var2.createKeyValue("");
      DrawableWidget var4 = var3.generateValueWidget(0, 0, 300, 20);
      var3.addListener(pairList -> this.akk(CombatSubHelperM.create(pairList.list())));
      var4.mouseClicked(150.0, 10.0, 0);
   }

   public Entity akJ(double nearby, boolean autoSelect, int tickPredict) {
      return this.akL(nearby, autoSelect, tickPredict, null);
   }

   public boolean canAttack(Entity target) {
      if (mc.player == null) {
         return false;
      } else if (target != null && target != mc.player && !(target instanceof CameraEntity)) {
         if (target instanceof LivingEntity var2 && var2.getHealth() <= 0.0F) {
            return false;
         } else if (!this.SL.get().test(target.getType()) && !this.passHostileCheck(target)) {
            return false;
         } else if (!this.isNotFriend(target)) {
            return false;
         } else {
            return !this.isNotTeamMate(target) ? false : this.isNotInvulnerable(target);
         }
      } else {
         return false;
      }
   }

   public DoubleStream getPotentialEyeHeights() {
      if (this.bV.get()) {
         double var1 = mc.player.getScale();
         return !mc.player.isFallFlying() && !mc.player.isUsingRiptide() && !mc.player.isSwimming()
            ? DoubleStream.concat(Arrays.stream(cd).map(s -> s * var1), DoubleStream.of(mc.player.dimensions.eyeHeight()))
            : DoubleStream.concat(Arrays.stream(cc).map(s -> s * var1), DoubleStream.of(mc.player.dimensions.eyeHeight()));
      } else {
         return DoubleStream.of(mc.player.dimensions.eyeHeight());
      }
   }

   public boolean isNotInvulnerable(Entity e) {
      if (!this.attInvulnerable.get() && e instanceof PlayerEntity var2) {
         if (var2.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) < 1.0E-6) {
            return false;
         }

         if (var2.isCreative()) {
            return false;
         }

         if (var2.isInvulnerable()) {
            return false;
         }
      }

      return true;
   }

   public List<Entity> akC(double nearbyOverride) {
      return this.getAttackableEntities(nearbyOverride, 0, this::canAttack);
   }

   private boolean passHostileCheck(Entity e) {
      if (this.attHostile.get()) {
         if (e instanceof BeeEntity var2) {
            if (var2.getAngerTime() > 0) {
               return true;
            }
         } else if (e instanceof EndermanEntity var3) {
            if (var3.isAngry()) {
               return true;
            }
         } else if (e instanceof WolfEntity var4) {
            if (var4.getAngerTime() > 0) {
               return true;
            }
         } else if (e instanceof ZombifiedPiglinEntity var5) {
            ;
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean isNotFriend(Entity e) {
      if (e instanceof PlayerEntity var2) {
         String var5 = var2.getNameForScoreboard();
         Regex var4 = this.friends.get();
         return var4 != null && var4.test(var5) ? false : this.attFriend.get() || !this.isInFriendList(var2);
      } else if (e.hasCustomName()) {
         if (this.attNamed.get()) {
            Regex var3 = this.friends.get();
            return var3 == null || !var3.test(ChatUtils.q(e.getCustomName()));
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public void akk(CombatSubHelperM strings) {
      this.SZ = strings;
      this.aA.f(CombatSubHelperM.CODEC, this.SZ);
   }

   public boolean akm(Vec3d pos, Box box, double range) {
      return box.squaredMagnitude(pos) > MathUtils.a(range + 3.0 + mc.player.dimensions.eyeHeight())
         ? false
         : this.getPotentialEyeHeights().mapToObj(s -> pos.add(0.0, s, 0.0)).anyMatch(ps -> box.squaredMagnitude(ps) < MathUtils.a(range));
   }

   public boolean isTargetInRange(Entity e, double nearby, int ticks) {
      if (mc.player == null) {
         return false;
      } else {
         nearby = Math.max(nearby, CombatExtra.INSTANCE.getAttackAtTargetRange(e));
         Vec3d var5 = mc.player.getPos().add(mc.player.getVelocity().multiply(mc.player.isFallFlying() ? ticks : 0.0));
         return this.akm(var5, e.getBoundingBox(), nearby);
      }
   }

   public Entity searchAttack(double nearby, boolean autoSelect, int tickPredict, Predicate<Entity> combinedPredicate) {
      if (mc.player == null) {
         return null;
      } else {
         if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == Type.ENTITY) {
            Entity var6 = ((EntityHitResult)mc.crosshairTarget).getEntity();
            if (!autoSelect || combinedPredicate.test(var6)) {
               return var6;
            }
         }

         HitResult var8 = RaycastUtils.createEntityOnlyCrossHairResult(mc.player, nearby, 1.0F, combinedPredicate);
         if (var8 != null && var8.getType() == Type.ENTITY && combinedPredicate.test(((EntityHitResult)var8).getEntity())) {
            return ((EntityHitResult)var8).getEntity();
         } else if (!autoSelect && mc.crosshairTarget.getType() == Type.BLOCK && CombatTasks.notSuitableForAttack(mc.player.getMainHandStack())) {
            return null;
         } else {
            List var7 = this.getAttackableEntities(nearby, tickPredict, combinedPredicate);
            var7.sort(Comparator.comparingDouble(e -> this.getEntityWeight(e, mc.player)));
            return !var7.isEmpty() ? (Entity)var7.get(0) : null;
         }
      }
   }

   public List<Entity> getAttackableEntities(double nearbyOverride, int ticks, Predicate<Entity> predicate) {
      ArrayList var5 = new ArrayList();

      for (Entity var8 : ImmutableList.copyOf(mc.world.getEntities())) {
         if (predicate.test(var8) && this.isTargetInRange(var8, nearbyOverride, ticks)) {
            var5.add(var8);
         }
      }

      return var5;
   }

   @Override
   public void addCustomWidgets(Consumer<DrawableWidget> acceptor, int dx, int dy, int dblank) {
      acceptor.accept(this.createExecuteButton("widget.friend-list.edit-friend-list", ButtonAction.a(this::akt), 0, dblank, dx, dy));
      acceptor.accept(this.createTitleLabel("widget.friend-list.command", 0, dblank, dx, dy));
   }

   public void addFriend(String friends, String alias) {
      CombatSubHelperM var3 = this.SZ;
      if (var3.contains(friends, alias)) {
         this.logI18NSub("Friends", "message.module.target-selector.friends.already-added", new Object[]{friends});
      } else {
         this.logI18NSub("Friends", "message.module.target-selector.friends.added", new Object[]{friends});
         var3 = var3.aai(friends).withAdd(friends, alias);
         this.akk(var3);
         this.akr(friends);
      }
   }

   private double getEntityWeight(Entity e, PlayerEntity player) {
      Vec3d var3 = player.getEyePos();
      Vec3d var4 = player.getRotationVector().normalize();
      Vec3d var5 = e.getPos().subtract(var3).normalize();
      double var6 = var5.x * var4.x + var5.z * var4.z;
      if (this.oppositeAttackMultiply.get()) {
         if (var6 >= 0.0) {
            return -(var6 / (e.getPos().subtract(var3).horizontalLength() + 1.0E-10) + (e instanceof PlayerEntity ? this.playerAttackMultiply.get() : 0.0));
         } else {
            double var8 = var6 / (var5.length() * var4.length() + 1.0E-10);
            return -(var8 + (e instanceof PlayerEntity ? this.playerAttackMultiply.get() : 0.0));
         }
      } else {
         return -(
            Math.abs(var6 / (e.getPos().subtract(var3).horizontalLength() + 1.0E-10)) + (e instanceof PlayerEntity ? this.playerAttackMultiply.get() : 0.0)
         );
      }
   }

   private void akS(MainCommand mainCommand) {
      TreeSubCommand var2 = mainCommand.bC().a("friends_command").k();
      var2.subBuilder(SubCommand.bp())
         .u("friends")
         .z(
            m -> m.<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
               .u("list")
               .x("message.command.friends_command.friends.list.help")
               .z(e -> e.executor(KalamaHelperHelperH.g(() -> {
                  Debug.b(Text.literal("== 当前好友列表 ==").formatted(Formatting.GREEN));

                  for (String var2x : this.SZ.aal()) {
                     Debug.b(var2x);
                  }
               })))
               .r()
               .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
               .u("add")
               .x("message.command.friends_command.friends.add.help")
               .A(KalamaHelperHelperA.a().B("name").d(WorldUtils::getPlayerListNames).v())
               .A(KalamaHelperHelperA.a().B("alias").l("<请输入别名>").b("").v())
               .z(e -> e.executor(KalamaHelperHelperH.i(arg -> this.addFriend(arg.o(), arg.f()))))
               .r()
               .<KalamaHelperHelperD, TaskSubCommand>subBuilder(SubCommand.bo())
               .u("remove")
               .x("message.command.friends_command.friends.remove.help")
               .A(KalamaHelperHelperA.a().B("name").d(() -> this.SZ.aak()).v())
               .z(e -> e.executor(KalamaHelperHelperH.i(arg -> this.aks(arg.o()))))
               .r()
               .subBuilder(SubCommand.bo())
               .u("gui")
               .x("message.command.friends_command.friends.gui.help")
               .z(e -> e.executor(KalamaHelperHelperH.g(this::akt)))
               .r()
         )
         .r();
   }

   public void aks(String friend) {
      CombatSubHelperM var2 = this.SZ;
      if (var2.aaf(friend)) {
         this.logI18NSub("Friends", "message.module.target-selector.friends.removed", new Object[]{friend});
         var2 = var2.aai(friend);
         this.akk(var2);
      } else {
         this.logI18NSub("Friends", "message.module.target-selector.friends.not-added", new Object[]{friend});
      }
   }

   public Entity searchAimable(Predicate<Entity> combinedPredicate) {
      if (mc.player == null) {
         return null;
      } else {
         if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == Type.ENTITY) {
            Entity var2 = ((EntityHitResult)mc.crosshairTarget).getEntity();
            if (combinedPredicate.test(var2)) {
               return var2;
            }
         }

         HitResult var6 = RaycastUtils.createEntityOnlyCrossHairResult(mc.player, 25.0, 1.0F, combinedPredicate);
         if (var6 != null && var6.getType() == Type.ENTITY) {
            return ((EntityHitResult)var6).getEntity();
         } else {
            List var3 = this.getAimableEntities(combinedPredicate);
            Vec3d var4 = mc.player.getEyePos();
            Vec3d var5 = mc.player.getRotationVector().normalize();
            if (var3.isEmpty()) {
               return null;
            } else {
               var3.sort(Comparator.comparingDouble(e -> -e.getEyePos().subtract(var4).normalize().dotProduct(var5)));
               return (Entity)var3.get(0);
            }
         }
      }
   }

   public List<Entity> akF(boolean commonBow) {
      return this.getAimableEntities(commonBow ? this::akv : this::canAttack);
   }

   public void akr(String friendName) {
      OptionalPrimitive var2 = this.sendOnAddFriend.get();
      if (var2.isPresent()) {
         ChatTasks.sayMessage(((String)var2.getValue()).formatted(friendName), false);
      }
   }

   public boolean isNotTeamMate(Entity e) {
      if (this.attTeammate.get() && e instanceof PlayerEntity var2) {
         if (var2.getScoreboardTeam() != null && var2.getScoreboardTeam() == mc.player.getScoreboardTeam()) {
            Team var3 = var2.getScoreboardTeam();
            if (!var3.isFriendlyFireAllowed()) {
               return false;
            }
         }

         for (EquipmentSlot var6 : EquipmentSlot.values()) {
            if (this.isSameTeam(var2, var6)) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public boolean checkWeapon(Entity e, boolean commonBow) {
      return !commonBow || !(e instanceof EndermanEntity) && !(e instanceof ShulkerEntity);
   }

   public boolean isInFriendList(PlayerEntity e) {
      CombatSubHelperM var2 = this.SZ;
      return var2 != null && var2.aaf(e.getNameForScoreboard());
   }

   public Optional<Vec3d> getBestAttackEyePos(Vec3d pos, Box box, double range) {
      if (box.squaredMagnitude(pos) > MathUtils.a(range + 3.0 + mc.player.dimensions.eyeHeight())) {
         return Optional.empty();
      } else {
         List<Vec3d> var5 = this.getPotentialEyeHeights().mapToObj(s -> pos.add(0.0, s, 0.0)).toList();
         Vec3d var6 = pos.add(mc.player.getEyePos().subtract(mc.player.getPos()));
         double var7 = box.squaredMagnitude(var6);

         for (Vec3d var10 : var5) {
            double var11 = box.squaredMagnitude(var10);
            if (var11 < var7) {
               var7 = var11;
               var6 = var10;
            }
         }

         return MathUtils.a(range) >= var7 ? Optional.of(var6) : Optional.empty();
      }
   }

   public Vec3d akn(Vec3d pos, Box box) {
      List<Vec3d> var3 = this.getPotentialEyeHeights().mapToObj(s -> pos.add(0.0, s, 0.0)).toList();
      Vec3d var4 = pos.add(mc.player.getEyePos().subtract(mc.player.getPos()));
      double var5 = box.squaredMagnitude(var4);

      for (Vec3d var8 : var3) {
         double var9 = box.squaredMagnitude(var8);
         if (var9 < var5) {
            var5 = var9;
            var4 = var8;
         }
      }

      return var4;
   }
}
