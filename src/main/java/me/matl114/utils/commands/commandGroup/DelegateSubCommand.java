package me.matl114.utils.commands.commandGroup;

import java.util.List;
import java.util.stream.Stream;
import me.matl114.utils.commands.interruption.PermissionDenyError;
import me.matl114.utils.commands.params.ArgumentInputStream;
import me.matl114.utils.commands.params.ArgumentReader;
import me.matl114.utils.commands.params.api.CommandExecution;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DelegateSubCommand implements SubCommand {
    String b;
    CustomTabExecutor a;
    String c;

    @Override
    public String c() {
        return this.b;
    }

    public DelegateSubCommand n(String permissionNode) {
        this.c = permissionNode;
        return this;
    }

    @Override
    public List<String> e(CommandExecution sender, ArgumentReader arguments) {
        return this.be(sender) && this.a != null ? this.a.e(sender, arguments) : List.of();
    }

    @Nullable
    @Override
    public String a() {
        return this.c;
    }

    public CustomTabExecutor i() {
        return this.a;
    }

    public String k() {
        return this.c;
    }

    public DelegateSubCommand l(CustomTabExecutor delegate) {
        this.a = delegate;
        return this;
    }

    public DelegateSubCommand m(String name) {
        this.b = name;
        return this;
    }

    public DelegateSubCommand(String name, CustomTabExecutor delegate) {
        this.b = name;
        this.a = delegate;
    }

    @Override
    public boolean onCustomCommand(CommandExecution sender, ArgumentReader arguments) {
        if (this.be(sender)) {
            return this.a != null ? this.a.onCustomCommand(sender, arguments) : false;
        } else {
            throw new PermissionDenyError(this.c, arguments);
        }
    }

    @NotNull
    @Override
    public ArgumentInputStream b(CommandExecution execution, ArgumentReader args) {
        return this.a != null
                ? this.a.b(execution, args)
                : new ArgumentInputStream(execution, args, List.of(), List.of());
    }

    public String j() {
        return this.b;
    }

    @Override
    public void setPermission(String permission) {
        this.c = permission;
    }

    @Override
    public Stream<String> getHelp(String prefix) {
        return this.a != null ? this.a.getHelp(prefix) : Stream.empty();
    }

    @Override
    public Stream<String> f(CommandExecution sender, ArgumentReader arguments) {
        return this.be(sender) && this.a != null ? this.a.f(sender, arguments) : Stream.empty();
    }
}
