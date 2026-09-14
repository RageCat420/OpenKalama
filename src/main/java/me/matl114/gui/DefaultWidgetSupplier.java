package me.matl114.gui;

import java.util.List;
import me.matl114.accessors.gui.TextFieldAccess;
import me.matl114.gui.basic.AbstractElement;
import me.matl114.gui.basic.ButtonAction;
import me.matl114.gui.basic.ColorSampler;
import me.matl114.gui.basic.ElementHandler;
import me.matl114.gui.basic.KalamaHelperHelperP;
import me.matl114.gui.basic.RenderHandler;
import me.matl114.gui.basic.TextProvider;
import me.matl114.gui.complex.RawTextElement;
import me.matl114.gui.elements.ButtonElement;
import me.matl114.gui.elements.IconElement$SimpleIconElement;
import me.matl114.gui.elements.TextFieldElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class DefaultWidgetSupplier implements WidgetSupplier {
    public static final DefaultWidgetSupplier INSTANCE = new DefaultWidgetSupplier();

    @Override
    public ElementHandler g(KalamaHelperHelperO builder) {
        RawTextElement var2 = new RawTextElement(
                this.safeTextProvider(builder.q),
                builder.r == null ? ColorSampler.WHITE : builder.r,
                builder.alignment);
        return this.applyCommon(var2, builder);
    }

    private void l(AbstractElement element, List<RenderHandler> renders) {
        if (renders != null) {
            for (RenderHandler var4 : renders) {
                if (var4 != null) {
                    element.cE(var4);
                }
            }
        }
    }

    private ElementHandler applyCommon(AbstractElement element, KalamaHelperHelperAX<?> builder) {
        element.setShowTooltips(builder.b);
        if (builder.c != null) {
            element.aO(builder.c);
        }

        this.k(element, builder.d);
        this.l(element, builder.e);
        this.m(element, builder.f);
        Object var3 = element;
        if (builder.h != null) {
            var3 = element.ah(builder.h);
        }

        if (builder.g != null) {
            var3 = ((ElementHandler) var3).ag(builder.g);
        }

        return (ElementHandler) var3;
    }

    private ButtonAction n(ButtonAction action) {
        return action == null ? ButtonAction.c() : action;
    }

    @Override
    public ElementHandler f(KalamaHelperHelperL builder) {
        return this.i(builder);
    }

    private void m(AbstractElement element, List<KalamaHelperHelperP> handlers) {
        if (handlers != null) {
            for (KalamaHelperHelperP var4 : handlers) {
                if (var4 != null) {
                    element.cF(var4);
                }
            }
        }
    }

    private DefaultWidgetSupplier() {}

    private void k(AbstractElement element, List<RenderHandler> renders) {
        if (renders != null) {
            for (RenderHandler var4 : renders) {
                if (var4 != null) {
                    element.cD(var4);
                }
            }
        }
    }

    private TextProvider safeTextProvider(TextProvider provider) {
        return provider == null ? TextProvider.c(Text.empty()) : provider;
    }

    @Override
    public ElementHandler h(KalamaHelperHelperZ builder) {
        TextFieldWidget var10000;
        if (builder.t != null) {
            var10000 = builder.t;
        } else {
            Object var10007 = builder.u == null ? Text.empty() : builder.u;
            var10000 = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 0, 0, (Text) var10007);
        }

        TextFieldWidget var2 = var10000;
        if (builder.F != null) {
            var2.setTextPredicate(builder.F);
        }

        var2.setMaxLength(builder.w);
        if (builder.C != null) {
            var2.setEditable(builder.C);
        }

        if (builder.D != null) {
            var2.setPlaceholder(builder.D);
        }

        if (builder.E != null) {
            var2.setSuggestion(builder.E);
        }

        if (builder.G != null) {
            var2.setEditableColor(builder.G);
        }

        if (builder.H != null) {
            var2.setUneditableColor(builder.H);
        }

        TextFieldElement var3 = new TextFieldElement(var2);
        var3.aZ(builder.v == null ? "" : builder.v);
        if (builder.I != null) {
            var2.setCursorToEnd(builder.I);
        }

        if (builder.z != null) {
            var3.bg(builder.z);
        }

        if (builder.A != null) {
            var3.bh(builder.A);
        }

        if (builder.B != null) {
            var3.bi(builder.B);
        }

        if (builder.x != null || builder.y != null) {
            var3.be(str -> {
                if (builder.x != null) {
                    builder.x.accept(str);
                }

                if (builder.y != null) {
                    builder.y.valueChange(TextFieldAccess.of(var2), str);
                }
            });
        }

        return this.applyCommon(var3, builder);
    }

    private ElementHandler i(KalamaHelperHelperD<?> builder) {
        Identifier var2 = builder.k != null ? builder.k : builder.j;
        Identifier var3 = builder.j != null ? builder.j : builder.k;
        IconElement$SimpleIconElement var4 =
                new IconElement$SimpleIconElement(var3, var2, builder.l, this.n(builder.i));
        var4.setActive(builder.m);
        if (builder.n != null) {
            var4.cw(builder.n);
        }

        if (builder.o != null) {
            var4.cs(builder.o);
        }

        if (builder.p != null) {
            var4.cu(builder.p);
        }

        return this.applyCommon(var4, builder);
    }

    @Override
    public ElementHandler e(KalamaHelperHelperS builder) {
        ButtonElement var2 = new ButtonElement(this.safeTextProvider(builder.textProvider), this.n(builder.i));
        if (builder.j != null) {
            var2.cA(builder.j);
        }

        if (builder.k != null) {
            var2.cC(builder.k);
        }

        var2.cy(builder.l);
        var2.setActive(builder.m);
        if (builder.n != null) {
            var2.cw(builder.n);
        }

        if (builder.o != null) {
            var2.cs(builder.o);
        }

        if (builder.p != null) {
            var2.cu(builder.p);
        }

        return this.applyCommon(var2, builder);
    }
}
