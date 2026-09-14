package me.matl114.hacks.modules.move;

import me.matl114.events.Event;
import me.matl114.hacks.utils.entity.HackUtilHelperJ;
import me.matl114.hacks.utils.entity.LegalMovementManager;
import net.minecraft.client.network.ClientPlayerEntity;

class MoveSubHelperB implements HackUtilHelperJ {
    @Override
    public void iC(Event<LegalMovementManager> movementManagerEvent) {
        ElytraExtra var2 = ElytraExtra.INSTANCE;
        ClientPlayerEntity var3 = ((LegalMovementManager) movementManagerEvent.b).c.a;
        if (var2 != null && var3.isFallFlying()) {
            var3.setVelocity(var2.agw(var3.getVelocity()));
        }
    }

    @Override
    public int priority() {
        return 2147483646;
    }
}
