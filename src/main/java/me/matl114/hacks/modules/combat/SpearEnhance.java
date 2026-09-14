package me.matl114.hacks.modules.combat;

import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntSupplier;
import me.matl114.accessors.interfaces.MetadataHolder;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.events.impl.UseItem;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.move.LegacySnapRotManager;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.hooks.ViaFabricPlusHooks;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.NetworkUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.ResourceUtils;
import me.matl114.utils.render.RenderCollector;
import me.matl114.versioned.SupportVersion;
import me.matl114.versioned.api.VItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.mutable.MutableInt;

public class SpearEnhance extends BaseModule {
   public final Map<Item, Identifier> dZ;
   public final FlagRef resetSpearAutoFocusTarget;
   public final FlagRef fixOldVersionSpear;
   public final ModulePath dI = makePath(Configs.k, "spear-module");
   public final NBTRef<WrapColor> renderKineticPlayersColor;
   public static SpearEnhance INSTANCE;
   public final FlagRef resetSpearSpeedRotEnable;
   private static final Optional<RegistryEntry<SoundEvent>> dY = Optional.ofNullable(tryRegisterSound("item.spear.use"));
   public final FlagRef resetSpearSpeedOnlyCombat;
   private final Map<Item, Identifier> dU;
   public final FlagRef renderKineticPlayers;
   public final FlagRef dJ = this.flagBuilder(this.dI.add("spear-auto-restart")).build();
   public final FlagRef fixOldVersionSpearSound;
   public final FlagRef fixOldVersionSpearPiercing;
   public final FlagRef replaceViaSpearModel;
   public final FlagRef resetSpearSpeedAuto;
   private static final String dV = "kalama:spear_module/last_kinetic_time";
   public final KeyBindRef resetSpearSpeedRotHotkey;
   boolean forceSpearReset;
   private static final Optional<RegistryEntry<SoundEvent>> dX = Optional.ofNullable(tryRegisterSound("item.spear.hit"));

   public void fU(Event<Set<Identifier>> event) {
      if (event.getArgs(1).equals(new Identifier("minecraft", "blocks"))) {
         ((Set)event.e()).addAll(ResourceUtils.lookupResources(event.getArgs(0), "kalama", "kalama", "textures", ".png", s -> s.startsWith("spear")));
      }
   }

   public long getLastKineticTime(Entity player) {
      return player instanceof MetadataHolder var2
            && !var2.isMetaEmpty()
            && var2.getMetadata().b(this, "kalama:spear_module/last_kinetic_time") instanceof Number var4
         ? var4.longValue()
         : -2147483648L;
   }

   public boolean onPiercing(IntSupplier seq) {
      if (VItem.w().b(mc.player.getStackInHand(Hand.MAIN_HAND)) && ViaFabricPlusHooks.getInstance().isViaEnabled()) {
         if (SupportVersion.CURRENT.b(21, 6)) {
            ViaFabricPlusHooks.ViaPacketWrapper var2 = ViaFabricPlusHooks.getInstance().createViaPacket();
            var2.writePacketType("v1_21_5to1_21_6", PlayPackets.PLAYER_ACTION);
            var2.write("VAR_INT", 7);
            var2.write("LONG", 0L);
            var2.write("BYTE", (byte)0);
            var2.write("VAR_INT", seq.getAsInt());
            var2.scheduleSendToServer("v1_21_6to1_21_7", true);
         } else {
            PlayerActionC2SPacket var12 = new PlayerActionC2SPacket(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN);
            ByteBuf var3 = NetworkUtils.createBytebuf();
            Listener.q().getOutboundState().codec().encode(var3, (net.minecraft.network.packet.Packet)var12);
            int var4 = VarInts.read(var3);
            VarInts.read(var3);
            long var5 = var3.readLong();
            short var7 = var3.readUnsignedByte();
            int var8 = VarInts.read(var3);
            var3.release();
            var3 = NetworkUtils.createBytebuf();

            try {
               VarInts.write(var3, var4);
               VarInts.write(var3, 7);
               var3.writeLong(var5);
               var3.writeByte(var7);
               VarInts.write(var3, var8);
               Listener.q().sendByteBuf(var3.retain());
            } finally {
               var3.release();
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean canSpearKineticAttack() {
      return hR(mc.player);
   }

   public void onUsePiercing(Event<PlayerActionC2SPacket> eventPiercing) {
      if (this.fixOldVersionSpearPiercing.get()
         && ViaFabricPlusHooks.getInstance().getCurrentVersion().c(21, 9)
         && ((PlayerActionC2SPacket)eventPiercing.b).getAction().ordinal() == 7
         && this.onPiercing(() -> ((PlayerActionC2SPacket)eventPiercing.b).getSequence())) {
         eventPiercing.cancel();
      }
   }

   public void fV(Event<Set<Identifier>> event) {
      ((Set)event.e()).addAll(ResourceUtils.lookupResources(event.getArgs(0), "kalama", "kalama", "models", ".json", s -> s.startsWith("spear")));
   }

   public void onRender(Event<MatrixStack> event) {
      if (this.renderKineticPlayers.get()) {
         RenderUtils.startDrawVirtual((MatrixStack)event.b);

         try {
            int var2 = this.renderKineticPlayersColor.get().withAlpha(64);
            RenderCollector var3 = RenderCollectors.createBoxCollector(false, true, false);

            for (AbstractClientPlayerEntity var5 : mc.world.getPlayers()) {
               if (var5 != mc.getCameraEntity() && hR(var5)) {
                  var3.submit(var5.getBoundingBox(), var2);
               }
            }

            var3.a((MatrixStack)event.b);
            var3.clear();
         } finally {
            RenderUtils.stopDrawVirtual((MatrixStack)event.b);
         }
      }
   }

   public boolean hasRealComponent(ItemStack stack) {
      Item var2 = stack.getItem();
      Identifier var3 = this.dU.get(var2);
      return var3 != null && VItem.w().b(stack);
   }

   public static ItemStack getSpear() {
      return mc.player.getActiveItem();
   }

   private static RegistryEntry<SoundEvent> tryRegisterSound(String sytr) {
      try {
         Identifier var1 = Identifier.ofVanilla(sytr);
         return Registry.registerReference(Registries.SOUND_EVENT, var1, SoundEvent.of(var1));
      } catch (Throwable var2) {
         return null;
      }
   }

   public void onSpearUse(Event<UseItem> eventAction) {
      if (!checkNull()) {
         if (this.fixOldVersionSpearSound.get()) {
            Hand var2 = ((UseItem)eventAction.b).c();
            ItemStack var3 = mc.player.getStackInHand(var2);
            if (this.dU.containsKey(var3.getItem()) && VItem.w().b(var3)) {
               MutableInt var4 = new MutableInt(0);
               Tasks.q(
                  () -> {
                     if (var4.getAndIncrement() > 20) {
                        return true;
                     } else if (mc.player.isUsingItem()) {
                        if (mc.player.getActiveHand() == var2 && ItemStack.areItemsAndComponentsEqual(var3, mc.player.getActiveItem())) {
                           dY.ifPresent(
                              sound -> mc.player
                                 .getEntityWorld()
                                 .playSound(mc.player, mc.player.getX(), mc.player.getY(), mc.player.getZ(), sound, mc.player.getSoundCategory(), 1.0F, 1.0F)
                           );
                        }

                        return true;
                     } else {
                        return false;
                     }
                  },
                  1,
                  1
               );
            }
         }
      }
   }

   public void setForceSpearReset(boolean forceSpearReset) {
      this.forceSpearReset = forceSpearReset;
   }

   public void onPreTick(Event<ClientPlayerEntity> tickEvent) {
      if (isUsingSpear(mc.player)) {
         ItemStack var2 = getSpear();
         int var3 = getMaxKineticTime(var2);
         Hand var4 = mc.player.getActiveHand();
         if (this.dJ.get() && mc.player.getItemUseTime() > var3) {
            mc.interactionManager.stopUsingItem(mc.player);
            mc.interactionManager.interactItem(mc.player, var4);
         }
      }
   }

   public static int getMaxKineticTime(ItemStack stack) {
      if (!VItem.w().b(stack)) {
         return 0;
      } else {
         Item var1 = stack.getItem();
         float var2;
         if (var1 == Items.DIAMOND_SWORD) {
            var2 = 10.0F;
         } else if (var1 == Items.NETHERITE_SWORD) {
            var2 = 8.75F;
         } else if (var1 == Items.IRON_SWORD) {
            var2 = 11.25F;
         } else if (var1 != Items.STONE_SWORD && var1 != Items.GOLDEN_SWORD) {
            if (var1 != Items.WOODEN_SWORD) {
               return 0;
            }

            var2 = 15.0F;
         } else {
            var2 = 13.75F;
         }

         return (int)var2 * 20;
      }
   }

   public SpearEnhance() {
      super("SpearEnhance");
      this.renderKineticPlayers = this.flagBuilder(this.dI.add("render-kinetic-players")).build();
      this.replaceViaSpearModel = this.builder(this.dI.add("replace-via-spear-model"), Boolean.class).defaultValue(true).build();
      this.fixOldVersionSpear = this.builder(this.dI.add("fix-old-version-spear"), Boolean.class).defaultValue(true).build();
      this.fixOldVersionSpearPiercing = this.builder(this.dI.add("fix-old-version-spear-piercing"), Boolean.class).defaultValue(true).build();
      this.fixOldVersionSpearSound = this.builder(this.dI.add("fix-old-version-spear-sound"), Boolean.class).defaultValue(true).build();
      this.renderKineticPlayersColor = this.builder(this.dI.add("render-kinetic-players-color"), WrapColor.class)
         .defaultValue(new WrapColor(Formatting.YELLOW))
         .build();
      this.resetSpearSpeedRotEnable = this.flagBuilder(this.dI.add("reset-spear-speed-rot-enable")).build();
      this.resetSpearSpeedRotHotkey = this.toggleHotkey(
            this.dI.add("reset-spear-speed-rot-hotkey"), new MultiKeyBind(), this.dI.add("reset-spear-speed-rot-enable")
         )
         .build();
      this.resetSpearSpeedAuto = this.flagBuilder(this.dI.add("reset-spear-speed-auto")).build();
      this.resetSpearSpeedOnlyCombat = this.flagBuilder(this.dI.add("reset-spear-speed-only-combat")).build();
      this.resetSpearAutoFocusTarget = this.flagBuilder(this.dI.add("reset-spear-auto-focus-target")).build();
      this.dU = new HashMap<>();
      this.dU.put(Items.WOODEN_SWORD, new Identifier("kalama", "spear/wooden_spear"));
      this.dU.put(Items.STONE_SWORD, new Identifier("kalama", "spear/stone_spear"));
      this.dU.put(Items.IRON_SWORD, new Identifier("kalama", "spear/iron_spear"));
      this.dU.put(Items.GOLDEN_SWORD, new Identifier("kalama", "spear/golden_spear"));
      this.dU.put(Items.DIAMOND_SWORD, new Identifier("kalama", "spear/diamond_spear"));
      this.dU.put(Items.NETHERITE_SWORD, new Identifier("kalama", "spear/netherite_spear"));
      this.forceSpearReset = false;
      this.dZ = new HashMap<>();
      this.dZ.put(Items.WOODEN_SWORD, new Identifier("kalama", "spear/wooden_spear_in_hand"));
      this.dZ.put(Items.STONE_SWORD, new Identifier("kalama", "spear/stone_spear_in_hand"));
      this.dZ.put(Items.IRON_SWORD, new Identifier("kalama", "spear/iron_spear_in_hand"));
      this.dZ.put(Items.GOLDEN_SWORD, new Identifier("kalama", "spear/golden_spear_in_hand"));
      this.dZ.put(Items.DIAMOND_SWORD, new Identifier("kalama", "spear/diamond_spear_in_hand"));
      this.dZ.put(Items.NETHERITE_SWORD, new Identifier("kalama", "spear/netherite_spear_in_hand"));
      INSTANCE = this;
   }

   public void onSpearEntity(Event<EntityStatusS2CPacket> eventPost) {
      if (!checkNull()) {
         if (this.fixOldVersionSpearSound.get() && ((EntityStatusS2CPacket)eventPost.b).getStatus() == 2) {
            Entity var2 = ((EntityStatusS2CPacket)eventPost.b).getEntity(mc.world);
            if (var2 instanceof LivingEntity var3 && var3.isUsingItem()) {
               ItemStack var4 = var3.getActiveItem();
               if (this.dU.containsKey(var4.getItem()) && VItem.w().b(var4)) {
                  dX.ifPresent(hitSound -> mc.world.playSoundFromEntity(var3, (SoundEvent)hitSound.value(), var2.getSoundCategory(), 1.0F, 1.0F));
               }
            }
         }
      }
   }

   public static boolean isUsingSpear(PlayerEntity player) {
      return player != null && player.isUsingItem() && VItem.w().b(player.getActiveItem());
   }

   public void onPostTick(Event<ClientPlayerEntity> eventPostTick) {
      if (!checkNull()) {
         if (eventPostTick.b == mc.player && (this.resetSpearSpeedRotEnable.get() || this.forceSpearReset) && ViaFabricPlusHooks.isSupportDupRot()) {
            this.forceSpearReset = false;
            boolean var2 = true;
            if (this.resetSpearSpeedAuto.get() && isUsingSpear(mc.player)) {
               var2 = false;
            }

            if (this.resetSpearSpeedOnlyCombat.get()) {
               boolean var3 = TargetSelector.INSTANCE.akC(25.0).stream().anyMatch(s -> s instanceof PlayerEntity var1 && isUsingSpear(var1));
               if (!var3) {
                  var2 = false;
               }
            }

            if (var2) {
               Vec3d var5 = PlayerStateManager.INSTANCE.nR();
               if (this.resetSpearAutoFocusTarget.get() && isUsingSpear(mc.player)) {
                  Entity var4 = TargetSelector.INSTANCE.akK(30.0, true, pl -> pl instanceof PlayerEntity);
                  if (var4 != null) {
                     var5 = var4.dimensions
                        .getBoxAt(PositionPredict.INSTANCE.spearPredictArgument.get().predict(var4))
                        .getCenter()
                        .subtract(mc.player.getEyePos());
                  }
               }

               LegacySnapRotManager.INSTANCE.ahs(var5, true);
            }
         }
      }
   }

   public void onUsingStab(Event<HitResult> eventStab) {
      if (this.fixOldVersionSpearPiercing.get()
         && ViaFabricPlusHooks.getInstance().getCurrentVersion().c(21, 9)
         && VItem.w().b(mc.player.getStackInHand(Hand.MAIN_HAND))
         && !mc.interactionManager.isFlyingLocked()
         && this.onPiercing(() -> 0)) {
         mc.player.swingHand(Hand.MAIN_HAND);
         eventStab.cancel();
      }
   }

   public void onReplaceSpearModel(Event<BakedModel> eventIdentifier) {
      if (!eventIdentifier.d() && eventIdentifier.b == null) {
         if (this.replaceViaSpearModel.get()) {
            ItemStack var2 = eventIdentifier.getArgs(0);
            if (this.dU.containsKey(var2.getItem()) && VItem.w().b(var2)) {
               Identifier var3 = this.dU.get(var2.getItem());
               if (var3 != null) {
                  BakedModel var4 = RenderListener.getCustomModelOf(var3);
                  if (var4 != null) {
                     eventIdentifier.context(var4);
                  }
               }
            }
         }
      }
   }

   public float getTimeSinceLastKineticAttack(Entity pl, float tickProgress) {
      long var3 = this.getLastKineticTime(pl);
      return var3 < 0L ? 0.0F : (float)(mc.world.getTime() - var3) + tickProgress;
   }

   public void onEntityStatus(Event<EntityStatusS2CPacket> event) {
      if (!checkNull()) {
         if (((EntityStatusS2CPacket)event.b).getStatus() == 2
            && ((EntityStatusS2CPacket)event.b).getEntity(mc.world) instanceof PlayerEntity var3
            && var3 instanceof MetadataHolder var4) {
            var4.getMetadata().a(this, "kalama:spear_module/last_kinetic_time", mc.world.getTime());
         }
      }
   }

   public static boolean hR(PlayerEntity player) {
      if (isUsingSpear(player)) {
         if (mc.player.getItemUseTime() < 8) {
            return false;
         } else {
            ItemStack var1 = getSpear();
            int var2 = getMaxKineticTime(var1);
            return player.getItemUseTime() < var2;
         }
      } else {
         return false;
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.U(), this::onPreTick);
      this.registerListener(RenderListener.q(), this::onRender);
      this.registerListener(RenderListener.n(), this::onReplaceSpearModel);
      this.registerListener(Listener.at(), this::onPostTick);
      this.registerListener(Listener.ap().getChannel(PlayerActionC2SPacket.class), this::onUsePiercing);
      this.registerListener(Listener.bm(), this::onUsingStab);
      this.registerListener(RenderListener.v(), this::fV);
      this.registerListener(RenderListener.w(), this::fU);
      this.registerListener(Listener.ap().getChannel(EntityStatusS2CPacket.class), this::onEntityStatus);
      this.registerListener(Listener.ar().getChannel(EntityStatusS2CPacket.class), this::onSpearEntity);
      this.registerListener(Listener.bi(), this::onSpearUse);
   }
}
