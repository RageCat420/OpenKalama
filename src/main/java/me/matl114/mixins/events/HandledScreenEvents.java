package me.matl114.mixins.events;

import com.llamalad7.mixinextras.sugar.Local;
import me.matl114.events.RenderListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({HandledScreen.class})
@Environment(EnvType.CLIENT)
public class HandledScreenEvents extends Screen {
    protected HandledScreenEvents(Text title) {
        super(title);
    }

    @Inject(
            method = {"render"},
            at = {
                @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V",
                        shift = Shift.AFTER)
            })
    public void onRenderBegin(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        String var10000 =
                "\u0000\u0000\u0001\u0000\u0012{½þ\b \u0002 ´\u0007Q]¼\u0099ï(ø[º¡ûÛKcJ\u0012\u0088Ì0$9\u001f½³\u0097\u0006×nÛåþ&®ºiá@àéfü4\u008b,2 '§´Oùí\u009b;RI\u009d\u0018ýÔ][5ð[þ?â&\u001b5\u008eö\r\u009fýÐcØr\u0084\fâ>-bQákÓ\u0087;\u0099Ê³\u0093Å©3ô_\u0091yó·Øì:^_Ë')}Í÷\u0082Þb\u0093\u008c\u0015\u007f\u0097®?ê0±àè\u0013¹\u008dD\u0086ÒÎÿvkÞÃÒ\u0083Ó~J¡\u0092÷cø[Îêm\u0099s¹Ý(-ÔXÿ%üûÂ\u009d.W\u0087àLJ¶ZZ\u0019B\u001d¸èâ\u0004¥hoBÆïÜu|óx\u0084e\\Õ\u0018\u009dÀ8kË¯ç\"\u0095\u0004YÐÇ{Ù3\u0001>'DË£@kCaU,\u000f$j¾\u0099ÞÛ\u001c6Óæ\u000f%82\u0000\u0000\u0001\u0000vÔ&\u0093Î\u009f\u0014¨\u001e4Û8Zïµ¹\u0082\u0012ä\u0005å*\u0091mW\u0086&\\\u008dR\u0093¾F\u0096Å/«VPS\u0095µ,!ï\u0089m\u007fë\u0085?^\u001c\u0093æÚÞq=¢áyê\u0017<\u0088HhÓØL®Ã»¦[\u008f+_Ê\u009d\u0004\u0088nlQhf×ÅDJ\u0003I\u008eìçxä!ü_×Öl\u008f@¸»ðó´E\u0080´\u0093\u008f¯>\u008c0\u0087®ÎD³\tÍQ\u008fU8ÈFF\u0081\u0002ä\u0081(®>r)oQéò\u008b\u000boÇ$qø\\Äö¬\u001f\u0016ò¸\u008c=<\u008a\u0012¸\u0094s\u009eUþ\u001eDWè\u008eÁ\u007fwÝ\u0090E¹º3o \u0097\u0095*5»Üþ4\u0083\bÌ\u0014\u009e\u0011ù5ºÏÝÇdi\u0091\u0013©\u0088©\u0011\bÑz®\u0005\u0010?Z%Äs?æ\u001a§\u0093µØ/¦§-S\u0097#\u0007Öol\u001fåJT\u0000\u0000»½\u0098\u0000\u0000\u00003°2¡\u0015IíwY}\u001b ¾J>\u000f\u0095¡Y\u000e6\u008dA\u0012uñ\u0010u\u008a0.kZ1\u009dol\u0013&ÀÛË{z\u0011ð\u0098ÿ\u0016C\u0002S";
        RenderListener.renderHandledScreen(context, (HandledScreen<?>) (Object) this, mouseX, mouseY, delta);
    }

    @Inject(
            method = {"render"},
            at = {
                @At(
                        value = "INVOKE",
                        target =
                                "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlot(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/screen/slot/Slot;)V")
            },
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void onRenderSlot(
            DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci, @Local Slot slot) {
        RenderListener.renderSlotInScreen(context, (HandledScreen<?>) (Object) this, slot);
    }
}
