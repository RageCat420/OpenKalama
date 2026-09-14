package me.matl114.utils.commands.params.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import me.matl114.utils.commands.params.api.CommandExecution;
import me.matl114.utils.commands.params.types.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

record KalamaHelperHelperH(KalamaHelperHelperB state) implements EntitySelector {
   public KalamaHelperHelperB wp() {
      return this.state;
   }

   public List<Entity> wo(CommandExecution execution) {
      Vec3d var2 = this.state.origin(execution);
      Box var3 = this.state.box(var2);
      List var4 = this.state
         .v(execution)
         .stream()
         .filter(entity -> entity != null && !entity.isRemoved())
         .filter(entity -> this.state.d == null || this.state.d.testSquared(entity.squaredDistanceTo(var2)))
         .filter(entity -> var3 == null || var3.intersects(entity.getBoundingBox()))
         .filter(this.state.w())
         .collect(Collectors.toCollection(ArrayList::new));
      this.state.n.tN(var2, var4);
      return var4.size() > this.state.limit ? List.copyOf(var4.subList(0, this.state.limit)) : List.copyOf(var4);
   }

   @Override
   public String asString() {
      return this.state.a;
   }
}
