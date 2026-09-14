package me.matl114.utils.commands.commandGroup;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface KalamaHelperHelperD {
   void setFallbackCommand(SubCommand var1, me.matl114.utils.commands.params.api.KalamaHelperHelperF var2);

   default <T extends KalamaHelperHelperD> T withFallback(SubCommand fallbackCommand, Supplier<Stream<String>> fallbackTabSuggestor) {
      this.setFallbackCommand(fallbackCommand, me.matl114.utils.commands.params.api.KalamaHelperHelperF.g(fallbackTabSuggestor));
      return (T)(Object)this;
   }

   SubCommand aA();

   default <R extends KalamaHelperHelperD, W extends SubCommand> KalamaHelperHelperG<R, W> subBuilder(KalamaHelperHelperE<W> builder) {
      return new KalamaHelperHelperG<>((R)(Object)this, builder);
   }

   void registerSub(SubCommand var1);

   Collection<SubCommand> az();
}
