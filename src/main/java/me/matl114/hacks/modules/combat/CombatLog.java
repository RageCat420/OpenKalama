package me.matl114.hacks.modules.combat;

import java.util.List;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.StringFormat;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.utils.KalamaHelperHelperB;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.registry.RegistryKey;

public class CombatLog extends BaseModule {
   public final NBTRef<StringFormat> logSmashFormat;
   public final NBTRef<StringFormat> logHitFormat;
   public final NBTRef<StringFormat> logKineticFormat;
   public final FlagRef logKinetic;
   public ModulePath yu = makePath(Configs.k, "combat-info");
   public final FlagRef logOtherHitMe;
   public final FlagRef logHit;
   public final FlagRef logMeHitOther;
   public ModulePath yv = this.yu.add("combat-log");
   public final FlagRef logSmash;
   public final FlagRef ae = this.flagBuilder(this.yv.addEnable()).build();

   public CombatLog() {
      super("CombatLog");
      this.logMeHitOther = this.flagBuilder(this.yv.add("log-me-hit-other")).build();
      this.logOtherHitMe = this.flagBuilder(this.yv.add("log-other-hit-me")).build();
      this.logHit = this.flagBuilder(this.yv.add("log-hit")).build();
      this.logHitFormat = this.builder(this.yv.add("log-hit-format"), StringFormat.class)
         .defaultValue(new StringFormat(List.of("attacker", "target", "damageType"), "{attacker} hit {target}, type: {damageType}", true))
         .build();
      this.logSmash = this.flagBuilder(this.yv.add("log-smash")).build();
      this.logSmashFormat = this.builder(this.yv.add("log-smash-format"), StringFormat.class)
         .defaultValue(new StringFormat(List.of("attacker", "target"), "{attacker} smash {target}", true))
         .build();
      this.logKinetic = this.flagBuilder(this.yv.add("log-kinetic")).build();
      this.logKineticFormat = this.builder(this.yv.add("log-kinetic-format"), StringFormat.class)
         .defaultValue(new StringFormat(List.of("attacker", "target"), "{attacker} spear {target}", true))
         .build();
      this.bindFlag(this.ae);
   }

   public void logDamage(String from, String to, RegistryKey<DamageType> source) {
      if (this.logSmash.get() && KalamaHelperHelperB.a(source, "mace_smash")) {
         this.log(this.logSmashFormat.get().formatText(from, to));
      } else if (this.logKinetic.get() && KalamaHelperHelperB.a(source, "spear")) {
         this.log(this.logKineticFormat.get().formatText(from, to));
      } else if (this.logHit.get()) {
         this.log(this.logHitFormat.get().formatText(from, to, source == null ? "null" : source.getValue().getPath()));
      }
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.ap().getChannel(EntityDamageS2CPacket.class), this::onEntityDamage);
   }

   public void onEntityDamage(Event<EntityDamageS2CPacket> e) {
      if (!checkNull()) {
         if (this.ae.get()
            && ((EntityDamageS2CPacket)e.b).sourceCauseId() == mc.player.getId()
            && mc.world.getEntityById(((EntityDamageS2CPacket)e.b).entityId()) instanceof PlayerEntity var3
            && this.logMeHitOther.get()) {
            RegistryKey var7 = (RegistryKey)((EntityDamageS2CPacket)e.b).sourceType().getKey().orElse(null);
            this.logDamage("you", var3.getNameForScoreboard(), var7);
         } else if (this.ae.get()
            && ((EntityDamageS2CPacket)e.b).entityId() == mc.player.getId()
            && mc.world.getEntityById(((EntityDamageS2CPacket)e.b).sourceCauseId()) instanceof PlayerEntity var4
            && this.logOtherHitMe.get()) {
            RegistryKey var6 = (RegistryKey)((EntityDamageS2CPacket)e.b).sourceType().getKey().orElse(null);
            this.logDamage(var4.getNameForScoreboard(), "you", var6);
         }
      }
   }
}
