package me.matl114.hacks.modules.combat;

import java.util.List;
import me.matl114.events.Event;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.modules.move.PlayerStateManager;
import me.matl114.hacks.utils.config.StringFormat;
import me.matl114.managers.Configs;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.NBTRef;
import net.minecraft.entity.player.PlayerEntity;

public class TotemLog extends BaseModule {
    public final FlagRef logDeath;
    public final NBTRef<StringFormat> logPopCountFormat;
    public final FlagRef logTotem;
    public final ModulePath hg = makePath(Configs.k, "totem");
    public final FlagRef logTotalCount;
    public final NBTRef<StringFormat> logDeathFormat;

    public TotemLog() {
        super("TotemLog");
        this.logTotem = this.flagBuilder(this.hg.add("log-totem")).build();
        this.logTotalCount = this.builder(this.hg.add("log-total-count"), Boolean.class)
                .defaultValue(true)
                .build();
        this.logDeath = this.builder(this.hg.add("log-death"), Boolean.class)
                .defaultValue(true)
                .build();
        this.logPopCountFormat = this.builder(this.hg.add("log-pop-count-format"), StringFormat.class)
                .defaultValue(new StringFormat(List.of("name", "count"), "{name} has triggered {count} totems", true))
                .build();
        this.logDeathFormat = this.builder(this.hg.add("log-death-format"), StringFormat.class)
                .defaultValue(
                        new StringFormat(List.of("name", "count"), "{name} died after trigger {count} totems", true))
                .build();
        this.bindFlag(this.logTotem);
    }

    public void onTotemPop(Event<PlayerEntity> event) {
        if (this.logTotem.get() && this.logDeath.get()) {
            Integer var2 = event.getArgs(1);
            if (var2 != null) {
                int var3 = var2;
                this.logSub(
                        "Totem",
                        this.logDeathFormat.get().formatText(((PlayerEntity) event.b).getNameForScoreboard(), var3));
            }
        }
    }

    @Override
    public void registerAll() {
        super.registerAll();
        this.registerListener(PlayerStateManager.or(), this::kN);
        this.registerListener(PlayerStateManager.os(), this::onTotemPop);
    }

    public void kN(Event<PlayerEntity> event) {
        if (this.logTotem.get() && this.logTotalCount.get()) {
            PlayerEntity var2 = (PlayerEntity) event.b;
            int var3 = event.<Integer>getArgs(0);
            this.logSub("Totem", this.logPopCountFormat.get().formatText(var2.getNameForScoreboard(), var3));
        }
    }
}
