package me.matl114.hacks.utils.config;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import me.matl114.managers.config.NBTParsable;
import me.matl114.managers.config.NBTRef;
import me.matl114.managers.config.NBTType;
import me.matl114.managers.config.Ref;
import me.matl114.managers.config.StringRef;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

public class EntityTypeRegex extends RegistryRegex<EntityType<?>>
        implements Predicate<EntityType<?>>, NBTParsable<RegistryRegex<EntityType<?>>> {
    public static final NBTType<EntityTypeRegex> TYPE = new NBTType<>(
            "entitytyperegex",
            Regex.TYPE.typeCodec().xmap(EntityTypeRegex::new, RegistryRegex::getParent),
            RegistryRegex::createTextEditWidget,
            new EntityTypeRegex(Regex.EMPTY));

    public EntityTypeRegex(Regex regex) {
        super(regex, Registries.ENTITY_TYPE);
    }

    @Override
    public <W extends RegistryRegex<EntityType<?>>> W withParent(Regex parent) {
        return (W) (new EntityTypeRegex(parent));
    }

    @Override
    public NBTType<RegistryRegex<EntityType<?>>> type() {
        return TYPE.cast();
    }

    @Override
    public Set<EntityType<?>> getFilterValue() {
        if (this.filterEntry == null) {
            this.filterEntry = new LinkedHashSet<>();
            EntityUtils.parseEntityWhiteList(this.parent.regex(), this.filterEntry);
        }

        return this.filterEntry;
    }

    @Override
    public <W> Optional<RegistryRegex<EntityType<?>>> tryTypeConvert(Ref<W> ref) {
        if (ref instanceof NBTRef<?> nbtType) {
            String nbtTypeName = nbtType.enumType;
            if (!Objects.equals(nbtTypeName, RegistryRegex.TYPE.typeName())) {
                return Optional.empty();
            }

            NBTParsable.registerNBTType(RegistryRegex.TYPE);
            NBTParsable<?> regex = nbtType.get();
            if (regex instanceof RegistryRegex regg && regg.registry == Registries.ENTITY_TYPE) {
                return Optional.of(new EntityTypeRegex(regg.parent));
            }
        } else if (ref instanceof StringRef stringRef) {
            Optional<Regex> regex = this.parent.tryTypeConvert(stringRef);
            if (regex.isPresent()) {
                return Optional.of(new EntityTypeRegex(regex.get()));
            }
        }

        return super.tryTypeConvert(ref);
    }

    @Override
    public List<Text> getRules() {
        return ChatUtils.parseTranslation("widget.nbt-parsable.entity-type-regex.rules.tooltips", "");
    }
}
