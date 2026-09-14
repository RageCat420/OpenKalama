package me.matl114.hacks.utils.config;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import me.matl114.managers.config.ListRef;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTType;
import me.matl114.managers.config.Ref;
import me.matl114.utils.config.WrapperFactory;

public record RegexList(List<Pattern> patterns) implements NBTParsable<RegexList>, Predicate<String> {
   public static final NBTType<RegexList> TYPE = NBTTypes.createListLke(
      "regexlist", NBTTypes.k, WrapperFactory.of(RegexList::new, RegexList::patterns), 300, 20
   );

   @Override
   public NBTType<RegexList> type() {
      return TYPE;
   }

   public boolean test(String string) {
      return this.patterns.stream().anyMatch(pattern -> pattern.matcher(string).matches());
   }

   public static Optional<RegexList> parse(List<String> strings) {
      try {
         return Optional.of(new RegexList(strings.stream().map(Pattern::compile).toList()));
      } catch (Throwable var2) {
         return Optional.empty();
      }
   }

   @Override
   public <W> Optional<RegexList> tryTypeConvert(Ref<W> ref) {
      return ref instanceof ListRef listRef ? parse(listRef.get()) : Optional.empty();
   }
}
