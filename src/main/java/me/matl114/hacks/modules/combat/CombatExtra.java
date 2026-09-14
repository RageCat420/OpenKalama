package me.matl114.hacks.modules.combat;

import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.impl.KalamaHelperHelperI;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.api.ModulePreset;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.utils.Debug;
import me.matl114.versioned.api.VItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;

public class CombatExtra extends BaseModule {
    public final DoubleRef boatReachRange;
    public final FlagRef shieldingAttack;
    public final FlagRef cancelInterval;
    public static CombatExtra INSTANCE;
    public final FlagRef shieldingSetbackLog;
    public final FlagRef ridingAttack;
    public final ModulePath sf = makePath(Configs.k, "attack");
    public final DoubleRef sg =
            this.doubleBuilder(this.sf.add("att-range")).defaultValue(0.0).build();
    public int shieldExceptionspam;

    public void Br(Event<HitResult> event) {}

    // $VF: Unable to simplify switch on enum
    // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a
    // copy of the class file (if you have the rights to distribute it!)
    public void gA(Event<KalamaHelperHelperI<ModulePreset>> event) {
        switch ((ModulePreset) ((KalamaHelperHelperI) event.b).b()) {
            case fd:
                this.sg.set(3.0);
                this.boatReachRange.set(3.0);
                break;
            case fg:
            case fh:
                this.sg.set(0.0);
                this.boatReachRange.set(3.0);
                break;
            default:
                this.sg.set(0.0);
                this.boatReachRange.set(0.0);
        }
    }

    public double getAttackRange() {
        double var1 = this.ridingBypass(mc.player) ? this.boatReachRange.get() : this.sg.get();
        return mc.player.getAttributeValue(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE) + var1;
    }

    public CombatExtra() {
        super("CombatExtra");
        this.boatReachRange = this.doubleBuilder(this.sf.add("boat-reach-range"))
                .defaultValue(0.0)
                .build();
        this.shieldingSetbackLog =
                this.flagBuilder(this.sf.add("shielding-setback-log")).build();
        this.shieldingAttack = this.flagBuilder(this.sf.add("shielding-attack")).build();
        this.ridingAttack = this.flagBuilder(this.sf.add("riding-attack")).build();
        this.cancelInterval = this.flagBuilder(this.sf.add("cancel-interval")).build();
        this.shieldExceptionspam = 0;
        INSTANCE = this;
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.ap().getChannel(EntityTrackerUpdateS2CPacket.class), this::onShieldSetback);
        this.registerListener(Listener.ap().getChannel(CooldownUpdateS2CPacket.class), this::asyncUpdateShieldCooldown);
        this.registerListener(Listener.bm(), this::Br);
        this.registerListener(Listener.bx().c(ModulePreset.class), this::gA);
    }

    public void onShieldSetback(Event<EntityTrackerUpdateS2CPacket> trackerUpdateS2CPacketEvent) {
        if (!trackerUpdateS2CPacketEvent.d()) {
            EntityTrackerUpdateS2CPacket var2 = (EntityTrackerUpdateS2CPacket) trackerUpdateS2CPacketEvent.e();
            if (this.shieldingSetbackLog.get()
                    && mc.player != null
                    && var2.id() == mc.player.getId()
                    && mc.player.isUsingItem()
                    && VItem.w().f(mc.player.getActiveItem())
                    && !mc.player
                            .getItemCooldownManager()
                            .isCoolingDown(mc.player.getActiveItem().getItem())) {
                for (SerializedEntry var4 : var2.trackedValues()) {
                    if (var4.id() == 8) {
                        byte var5 = ((Number) var4.value()).byteValue();
                        boolean var6 = (var5 & 1) > 0;
                        Hand var7 = (var5 & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
                        if (!var6 && var7 == mc.player.getActiveHand() && this.shieldExceptionspam + 4 < Tasks.b()) {
                            this.shieldExceptionspam = Tasks.b();
                            Debug.b(Text.literal("[AC] 阻挡异常盾牌禁用").formatted(Formatting.RED));
                        }
                    }
                }
            }
        }
    }

    public double getAttackAtTargetRange(Entity entity) {
        double var2 =
                !this.ridingBypass(mc.player) && !this.ridingBypass(entity) ? this.sg.get() : this.boatReachRange.get();
        return mc.player.getAttributeValue(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE) + var2;
    }

    private boolean ridingBypass(Entity entity) {
        return entity.hasVehicle();
    }

    public void asyncUpdateShieldCooldown(Event<CooldownUpdateS2CPacket> packetEvent) {
        if (!packetEvent.d()) {
            CooldownUpdateS2CPacket var2 = (CooldownUpdateS2CPacket) packetEvent.e();
            if (var2.cooldown() > 0) {
                try {
                    synchronized (CombatExtra.class) {
                        mc.player.getItemCooldownManager().set(var2.item(), var2.cooldown());
                        packetEvent.cancel();
                    }
                } catch (Throwable var6) {
                }
            }
        }
    }
}
