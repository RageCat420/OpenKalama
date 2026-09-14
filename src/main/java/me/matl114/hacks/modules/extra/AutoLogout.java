package me.matl114.hacks.modules.extra;

import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.hacks.CombatTasks;
import me.matl114.hacks.MainTasks;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.IntRef;
import me.matl114.managers.config.KeyBindRef;
import me.matl114.managers.input.MultiKeyBind;
import me.matl114.utils.InventoryUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

public class AutoLogout extends BaseModule {
    public final IntRef totemLeft2;
    public final FlagRef totemTrigger;
    public final IntRef minHeight2;
    public final FlagRef health;
    public final FlagRef minHeight;
    public final KeyBindRef hotkey;
    public final IntRef totemTrigger2;
    public final FlagRef strangerPlayer;
    public final IntRef health2;
    public final ModulePath Bq = makePath(Configs.j, "auto-logout");
    public final FlagRef ae = this.flagBuilder(this.Bq.add("enable")).build();
    public final FlagRef totemLeft;

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(Listener.V(), this::onTick);
        this.registerListener(Listener.ap().getChannel(EntityStatusS2CPacket.class), this::onPacketTotem);
        this.registerListener(Listener.ar().getChannel(EntitySpawnS2CPacket.class), this::onPlayerSpawn);
    }

    public void onTick(Event<ClientPlayerEntity> event) {
        if (this.ae.get()) {
            ClientPlayerEntity var2 = (ClientPlayerEntity) event.b;
            if (this.health.get() && var2.getHealth() <= this.health2.get()) {
                MainTasks.q();
                return;
            }

            if (this.totemLeft.get()) {
                double var3 = InventoryUtils.computePlayerInventory(
                        v -> v.getItem() == Items.TOTEM_OF_UNDYING ? (double) v.getCount() : null, false);
                if (var3 <= this.totemLeft2.get()) {
                    MainTasks.q();
                    return;
                }
            }

            if (this.minHeight.get() && var2.getY() < this.minHeight2.get()) {
                MainTasks.q();
                return;
            }
        }
    }

    public AutoLogout() {
        super("AutoLogout");
        this.hotkey = this.toggleHotkey(this.Bq.add("hotkey"), new MultiKeyBind(), this.Bq.add("enable"))
                .build();
        this.health = this.flagBuilder(this.Bq.add("health").add("enable")).build();
        this.health2 = this.builder(this.Bq.add("health").add("threshold"), IntRef.TYPE)
                .defaultValue(4)
                .build();
        this.totemLeft =
                this.flagBuilder(this.Bq.add("totem-left").add("enable")).build();
        this.totemLeft2 = this.builder(this.Bq.add("totem-left").add("threshold"), IntRef.TYPE)
                .defaultValue(1)
                .build();
        this.totemTrigger =
                this.flagBuilder(this.Bq.add("totem-trigger").add("enable")).build();
        this.totemTrigger2 = this.builder(this.Bq.add("totem-trigger").add("threshold"), IntRef.TYPE)
                .defaultValue(99)
                .build();
        this.minHeight =
                this.flagBuilder(this.Bq.add("min-height").add("enable")).build();
        this.minHeight2 = this.builder(this.Bq.add("min-height").add("threshold"), IntRef.TYPE)
                .defaultValue(256)
                .build();
        this.strangerPlayer =
                this.flagBuilder(this.Bq.add("stranger-player").add("enable")).build();
        this.bindFlag(this.ae);
    }

    public void onPlayerSpawn(Event<EntitySpawnS2CPacket> event) {
        if (this.ae.get() && this.strangerPlayer.get() && mc.player != null) {
            EntitySpawnS2CPacket var2 = (EntitySpawnS2CPacket) event.e();
            if (var2.getEntityType() == EntityType.PLAYER && var2.getEntityId() != mc.player.getId()) {
                Entity var3 = mc.world.getEntityById(var2.getEntityId());
                if (var3 != null && CombatTasks.l().isNotFriend(var3)) {
                    MainTasks.q();
                }
            }
        }
    }

    public void onPacketTotem(Event<EntityStatusS2CPacket> event) {
        EntityStatusS2CPacket var2 = (EntityStatusS2CPacket) event.e();
        if (this.ae.get()
                && this.totemTrigger.get()
                && var2.getStatus() == 35
                && mc.world != null
                && mc.player != null) {
            Entity var3 = var2.getEntity(mc.world);
            if (var3 != null && var3.getId() == mc.player.getId()) {
                int var4 = this.totemTrigger2.get();
                double var5 = InventoryUtils.computePlayerInventory(
                                v -> v.getItem() == Items.TOTEM_OF_UNDYING ? (double) v.getCount() : null, false)
                        - 1.0;
                if (var5 <= var4) {
                    MainTasks.q();
                    return;
                }
            }
        }
    }
}
