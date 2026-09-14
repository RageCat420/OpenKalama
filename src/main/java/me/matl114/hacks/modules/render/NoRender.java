package me.matl114.hacks.modules.render;

import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.EntityTypeRegex;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.input.MultiKeyBind;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;

public class NoRender extends BaseModule {
   public final FlagRef fireOverlay;
   public final FlagRef noDarkness;
   public final FlagRef weatherEffect;
   public final ModulePath sW;
   public final FlagRef forceNo;
   public final NBTRef<EntrySet<StatusEffect>> to;
   public final FlagRef vignette;
   public final FlagRef wallOverlay;
   public final FlagRef useBow;
   public final FlagRef forceNo3;
   public final ModulePath sV;
   public final FlagRef fly;
   public final ModulePath sU;
   public final ModulePath sX;
   public final FlagRef distanceFog;
   public final FlagRef itemOverlay;
   public final FlagRef portalOverlay;
   public final ModulePath sT;
   public final FlagRef liquidOverlay;
   public static NoRender INSTANCE;
   public final NBTRef<EntityTypeRegex> types;
   public final ModulePath sY;
   public final ModulePath aD;
   public final FlagRef slowDown;
   public final FlagRef speedUp;
   public final FlagRef noBlindness;
   public final KeyBindRef J;
   public final FlagRef forceNo2;
   public final FlagRef ae;
   public final FlagRef blockRandomEffect;
   public final FlagRef freezeOverlay;
   public final FlagRef invisibility;
   public final ModulePath ow = makePath(Configs.i, "render");
   public final FlagRef noNausea;
   public final NBTRef<EntrySet<ParticleType<?>>> tx;
   public final FlagRef guiOverlay;

   public boolean CT() {
      return this.isActive() && this.fly.get();
   }

   public boolean CH() {
      return this.isActive() && this.wallOverlay.get();
   }

   public void doCancelEffect(Event<EntityStatusEffectS2CPacket> packet) {
      if (!checkNull()) {
         if (this.ae.get() && this.forceNo.get() && this.to.get().test((StatusEffect)((EntityStatusEffectS2CPacket)packet.b).getEffectId().value())) {
            packet.cancel();
         }
      }
   }

   public boolean CQ() {
      return this.CP();
   }

   public boolean CY() {
      return this.isActive() && this.noDarkness.get();
   }

   public boolean CI() {
      return this.isActive() && this.liquidOverlay.get();
   }

   public boolean CW() {
      return this.isActive() && this.useBow.get();
   }

   public boolean CJ() {
      return this.isActive() && this.fireOverlay.get();
   }

   public boolean CN() {
      return this.isActive() && this.guiOverlay.get();
   }

   public boolean CU() {
      return this.isActive() && this.slowDown.get();
   }

   public boolean CK() {
      return this.isActive() && this.freezeOverlay.get();
   }

   public boolean CS() {
      return this.isActive() && this.weatherEffect.get();
   }

   public void doCancelSpawn(Event<EntitySpawnS2CPacket> packet) {
      if (!checkNull()) {
         if (this.ae.get() && this.forceNo2.get() && this.types.get().test(((EntitySpawnS2CPacket)packet.b).getEntityType())) {
            packet.cancel();
         }
      }
   }

   public boolean CR() {
      return this.isActive() && this.blockRandomEffect.get();
   }

   public NoRender() {
      super("NoRender");
      this.aD = this.ow.add("no-render");
      this.sT = this.aD.add("overlay");
      this.sU = this.aD.add("eff-setting");
      this.sV = this.aD.add("world-effect");
      this.sW = this.aD.add("fov-effect");
      this.sX = this.aD.add("entity");
      this.sY = this.aD.add("particle");
      this.ae = this.flagBuilder(this.aD.addEnable()).build();
      this.J = this.moduleEntry(this.aD.addHotkey(), new MultiKeyBind(), this.aD.addEnable()).build();
      this.wallOverlay = this.flagBuilder(this.sT.add("wall-overlay")).build();
      this.liquidOverlay = this.flagBuilder(this.sT.add("liquid-overlay")).build();
      this.fireOverlay = this.flagBuilder(this.sT.add("fire-overlay")).build();
      this.freezeOverlay = this.flagBuilder(this.sT.add("freeze-overlay")).build();
      this.itemOverlay = this.flagBuilder(this.sT.add("item-overlay")).build();
      this.portalOverlay = this.flagBuilder(this.sT.add("portal-overlay")).build();
      this.guiOverlay = this.flagBuilder(this.sT.add("gui-overlay")).build();
      this.vignette = this.flagBuilder(this.sT.add("vignette")).build();
      this.distanceFog = this.flagBuilder(this.sV.add("distance-fog")).build();
      this.blockRandomEffect = this.flagBuilder(this.sV.add("block-random-effect")).build();
      this.weatherEffect = this.flagBuilder(this.sV.add("weather-effect")).build();
      this.noNausea = this.flagBuilder(this.sU.add("no-nausea")).build();
      this.noDarkness = this.flagBuilder(this.sU.add("no-darkness")).build();
      this.noBlindness = this.flagBuilder(this.sU.add("no-blindness")).build();
      this.forceNo = this.flagBuilder(this.sU.add("force-no")).build();
      this.to = this.builder(this.sU.add("types"), EntrySet.<StatusEffect>parameter())
         .defaultValue(new EntrySet<StatusEffect>(new Regex("^(blindness|darkness|nausea)$"), Registries.STATUS_EFFECT))
         .build();
      this.fly = this.flagBuilder(this.sW.add("fly")).build();
      this.slowDown = this.flagBuilder(this.sW.add("slow-down")).build();
      this.speedUp = this.flagBuilder(this.sW.add("speed-up")).build();
      this.useBow = this.flagBuilder(this.sW.add("use-bow")).build();
      this.forceNo2 = this.flagBuilder(this.sX.add("force-no")).build();
      this.types = this.builder(this.sX.add("types"), EntityTypeRegex.class).defaultValue(new EntityTypeRegex(new Regex("^()$"))).build();
      this.invisibility = this.flagBuilder(this.sX.add("invisibility")).build();
      this.forceNo3 = this.flagBuilder(this.sY.add("force-no")).build();
      this.tx = this.builder(this.sY.add("types"), EntrySet.<ParticleType<?>>parameter()).defaultValue(new EntrySet<ParticleType<?>>(new Regex("^()$"), Registries.PARTICLE_TYPE)).build();
      this.bindFlag(this.ae);
      INSTANCE = this;
   }

   public boolean CP() {
      return this.isActive() && this.distanceFog.get();
   }

   public boolean CM() {
      return this.isActive() && this.portalOverlay.get();
   }

   public boolean CX() {
      return this.isActive() && this.noNausea.get();
   }

   public boolean CO() {
      return this.isActive() && this.vignette.get();
   }

   public boolean Da() {
      return this.isActive() && this.invisibility.get();
   }

   public boolean CV() {
      return this.isActive() && this.speedUp.get();
   }

   public void doParticleSpawnWeather(Event<Particle> event) {
      if (!checkNull()) {
         if (this.ae.get() && this.weatherEffect.get() && mc.world.isRaining()) {
            event.cancel();
         }
      }
   }

   public boolean CZ() {
      return this.isActive() && this.noBlindness.get();
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.ap().getChannel(EntityStatusEffectS2CPacket.class), this::doCancelEffect);
      this.registerListener(Listener.ap().getChannel(EntitySpawnS2CPacket.class), this::doCancelSpawn);
      this.registerListener(Listener.bA().c(ParticleTypes.RAIN), this::doParticleSpawnWeather);
      this.registerListener(Listener.bA().c(ParticleTypes.SNOWFLAKE), this::doParticleSpawnWeather);
      this.registerListener(Listener.bA(), this::doParticleSpawnTyped);
   }

   public boolean CL() {
      return this.isActive() && this.itemOverlay.get();
   }

   public void doParticleSpawnTyped(Event<Particle> event) {
      if (!checkNull()) {
         if (this.ae.get() && this.forceNo3.get()) {
            ParticleEffect var2 = event.getArgs(0);
            if (this.tx.get().test(var2.getType())) {
               event.cancel();
            }
         }
      }
   }
}
