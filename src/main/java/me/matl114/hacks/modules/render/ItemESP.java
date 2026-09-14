package me.matl114.hacks.modules.render;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import me.matl114.accessors.events.EntityAccess;
import me.matl114.events.Event;
import me.matl114.events.Listener;
import me.matl114.events.RenderListener;
import me.matl114.hacks.api.BaseModule;
import me.matl114.hacks.api.ModulePath;
import me.matl114.hacks.utils.config.EntrySet;
import me.matl114.hacks.utils.config.NBTTypes;
import me.matl114.hacks.utils.config.PrimitiveList;
import me.matl114.hacks.utils.config.Regex;
import me.matl114.hacks.utils.config.TracingOption;
import me.matl114.hacks.utils.config.WrapColor;
import me.matl114.hacks.utils.render.HackUtilHelperB;
import me.matl114.hacks.utils.render.RenderCollectors;
import me.matl114.managers.Configs;
import me.matl114.managers.Tasks;
import me.matl114.managers.config.DoubleRef;
import me.matl114.managers.config.FlagRef;
import me.matl114.managers.config.NBTRef;
import me.matl114.utils.ChatUtils;
import me.matl114.utils.ItemStackUtils;
import me.matl114.utils.RenderUtils;
import me.matl114.utils.render.RenderCollector;
import me.matl114.versioned.api.VDrawContext;
import me.matl114.versioned.api.VItem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class ItemESP extends BaseModule {
   private static final String ITEM_ESP_METADATA_KEY = "kalama:item_esp_show_key";
   public FlagRef drawName;
   public NBTRef<EntrySet<Item>> dx;
   public FlagRef enableSimple;
   public DoubleRef nameScale;
   public NBTRef<WrapColor> specialColor;
   public final ModulePath gw = makePath(Configs.i, "detect-entity");
   public FlagRef enableItem;
   final RenderCollector<Box> gN;
   boolean pendingUpdateEntities;
   final RenderCollector<HackUtilHelperB> gP;
   public FlagRef nameDisplay;
   public FlagRef enableFrame;
   public List<NbtPredicate> dy;
   public NBTRef<WrapColor> color;
   public Set<Item> gz;
   public NBTRef<WrapColor> nameDisplayColor;
   public FlagRef nameDisplaySpecial;
   final RenderCollector<Vec3d> gO;
   public NBTRef<TracingOption> specialOptions;
   public FlagRef ae;
   public final ModulePath gx = this.gw.add("item-esp");
   public NBTRef<TracingOption> options;
   public NBTRef<WrapColor> specialNameDisplayColor;
   public NBTRef<PrimitiveList<NbtCompound>> nbtPredicate;

   @Override
   public void onEnableModule() {
      super.onEnableModule();
      this.kn();
   }

   @Override
   public void registerAll() {
      super.registerAll();
      this.registerListener(Listener.au().c(EntityType.ITEM), this::handleItemEntityItemData);
      this.registerListener(Listener.au().c(EntityType.ITEM_FRAME), this::kq);
      this.registerListener(Listener.au().c(EntityType.GLOW_ITEM_FRAME), this::kq);
      this.registerListener(Listener.T(), this::onUpdate);
      this.registerListener(RenderListener.q(), this::kr);
      this.registerListener(RenderListener.r(), this::ks);
   }

   public void onUpdate(Event<Void> eventVoid) {
      this.gN.clear();
      this.gO.clear();
      this.gP.clear();
      if (!checkNull()) {
         if (this.ae.get()) {
            boolean var2 = this.enableItem.get();
            boolean var3 = this.enableSimple.get();
            int var4 = this.color.get().withAlpha(255);
            int var5 = this.specialColor.get().withAlpha(255);
            int var6 = this.nameDisplayColor.get().withAlpha(255);
            int var7 = this.specialNameDisplayColor.get().withAlpha(255);
            TracingOption var8 = this.options.get();
            TracingOption var9 = this.specialOptions.get();
            if (var2 || var3) {
               for (Entity var11 : mc.world.getEntities()) {
                  if (var11 instanceof ItemEntity var12 || this.enableFrame.get() && var11 instanceof ItemFrameEntity) {
                     if (var2
                        && var11 instanceof EntityAccess var13
                        && !var13.isMetaEmpty()
                        && var13.getMetadata().b(this, "kalama:item_esp_show_key") instanceof Text var19) {
                        if (var9.box()) {
                           this.gN.submit(var11.getBoundingBox(), var5);
                        }

                        if (var9.line()) {
                           this.gO.submit(var11.getBoundingBox().getCenter(), var5);
                        }

                        if (this.nameDisplaySpecial.get()) {
                           this.gP.submit(new HackUtilHelperB(var19, var11.getBoundingBox().getCenter(), (float)this.nameScale.get()), var7);
                        }
                     } else if (var3) {
                        if (var8.box()) {
                           this.gN.submit(var11.getBoundingBox(), var4);
                        }

                        if (var8.line()) {
                           this.gO.submit(var11.getBoundingBox().getCenter(), var4);
                        }

                        if (this.nameDisplay.get()) {
                           MutableText var17;
                           if (var11 instanceof ItemEntity var15 && !var15.getStack().isEmpty()) {
                              ItemStack var16 = var15.getStack();
                              var17 = VItem.w().l(var16).append(ChatUtils.textFromLegacyString("&ex%d".formatted(var16.getCount())));
                           } else if (var11 instanceof ItemFrameEntity var18 && !var18.getHeldItemStack().isEmpty()) {
                              var17 = VItem.w().l(var18.getHeldItemStack());
                           } else {
                              var17 = null;
                           }

                           if (var17 != null) {
                              this.gP.submit(new HackUtilHelperB(var17, var11.getBoundingBox().getCenter(), (float)this.nameScale.get()), var6);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void markItemToRender(Entity entity) {
      this.kl(entity, Text.empty());
   }

   public void hG(List<NbtCompound> compound) {
      if (compound != null && !compound.isEmpty()) {
         this.dy = compound.stream().<NbtPredicate>map(NbtPredicate::new).toList();
      } else {
         this.dy = null;
      }

      this.kn();
   }

   public void onItemEntity(Entity itemEntity, ItemStack stack) {
      if (!stack.isEmpty() && this.testItem(stack)) {
         this.kl(itemEntity, VItem.w().l(stack).append(ChatUtils.textFromLegacyString("&ex%d".formatted(stack.getCount()))));
      } else {
         this.km(itemEntity);
      }
   }

   public void handleItemEntityItemData(Event<SerializedEntry<?>> entryUpdateEvent) {
      if (this.enableItem.get()) {
         SerializedEntry var2 = (SerializedEntry)entryUpdateEvent.e();
         if (var2.id() == 8 && var2.value() instanceof ItemStack var4 && entryUpdateEvent.getArgs(0) instanceof ItemEntity var5) {
            this.onItemEntity(var5, var4);
         }
      }
   }

   public boolean testItem(ItemStack stack) {
      return this.dx.get().test(stack.getItem()) || this.dy != null && this.testItemData(stack);
   }

   private boolean testItemData(ItemStack stack) {
      ComponentChanges var2 = stack.getComponentChanges();

      try {
         NbtCompound var3 = var2.isEmpty()
            ? new NbtCompound()
            : (NbtCompound)ComponentChanges.CODEC.encodeStart(ItemStackUtils.registry().getOps(NbtOps.INSTANCE), var2).getOrThrow();

         for (NbtPredicate var5 : this.dy) {
            if (var5.test(var3)) {
               return true;
            }
         }

         return false;
      } catch (Throwable var6) {
         return false;
      }
   }

   public void kj(EntrySet<Item> reg) {
      Set var2 = reg.set();
      if (!Objects.equals(var2, this.gz)) {
         this.gz = var2;
         this.kn();
      }
   }

   public void ks(Event<VDrawContext> vdrawEvent) {
      if (this.ae.get()) {
         this.gP.b((VDrawContext)vdrawEvent.b);
      }
   }

   public void kr(Event<MatrixStack> event) {
      if (this.ae.get()) {
         MatrixStack var2 = (MatrixStack)event.e();
         RenderUtils.startDrawVirtual(var2);

         try {
            this.gN.a(var2);
            this.gO.a(var2);
         } finally {
            RenderUtils.stopDrawVirtual(var2);
         }
      }
   }

   public void kl(Entity entity, Text text) {
      EntityAccess.of(entity).getMetadata().a(this, "kalama:item_esp_show_key", text);
   }

   public void km(Entity entity) {
      EntityAccess var2 = EntityAccess.of(entity);
      if (!var2.isMetaEmpty()) {
         var2.getMetadata().a(this, "kalama:item_esp_show_key", null);
      }
   }

   public ItemESP() {
      super("ItemESP");
      this.pendingUpdateEntities = false;
      this.gz = new HashSet<>();
      this.ae = this.flagBuilder(this.gx.addEnable()).build();
      this.enableSimple = this.flagBuilder(this.gx.add("enable-simple")).build();
      this.enableItem = this.flagBuilder(this.gx.add("enable-item")).updateListener(s -> this.kn()).build();
      this.enableFrame = this.flagBuilder(this.gx.add("enable-frame")).updateListener(s -> this.kn()).build();
      this.drawName = this.flagBuilder(this.gx.add("draw-name")).build();
      this.options = this.builder(this.gx.add("options"), TracingOption.class).defaultValue(new TracingOption(true, false)).build();
      this.nameDisplay = this.flagBuilder(this.gx.add("name-display")).build();
      this.nbtPredicate = this.builder(this.gx.add("nbt-predicate"), PrimitiveList.<NbtCompound>uA())
         .defaultValue(new PrimitiveList<NbtCompound>(NBTTypes.n, List.of()))
         .updateListener(s -> this.hG(s.list()))
         .<NBTRef>build();
      Class<EntrySet<Item>> varT = EntrySet.parameter();
      me.matl114.hacks.api.WrapperSettingBuilder varB = this.builder(this.gx.add("item-type"), varT);
      varB.defaultValue(
         new EntrySet(
            new Regex("^(.*ton_skull|netherite.*|.*_star|.*_apple|.*potion|tot.*|end_c.*l|obsi.*|.*anchor|expe.*|mace|ely.*|.*shulker.*|trident)$"),
            Registries.ITEM
         )
      );
      varB.updateListener((Object r) -> this.kj((EntrySet<Item>)r));
      this.dx = (NBTRef<EntrySet<Item>>)(Object)varB.build();
      this.specialOptions = this.builder(this.gx.add("special-options"), TracingOption.class).defaultValue(new TracingOption(true, true)).build();
      this.nameDisplaySpecial = this.builder(this.gx.add("name-display-special"), Boolean.class).defaultValue(true).build();
      this.color = this.builder(this.gx.add("color"), WrapColor.class).defaultValue(new WrapColor(Formatting.YELLOW)).build();
      this.specialColor = this.builder(this.gx.add("special-color"), WrapColor.class).defaultValue(new WrapColor("#ED0355")).build();
      this.nameDisplayColor = this.builder(this.gx.add("name-display-color"), WrapColor.class).defaultValue(new WrapColor(Formatting.WHITE)).build();
      this.specialNameDisplayColor = this.builder(this.gx.add("special-name-display-color"), WrapColor.class)
         .defaultValue(new WrapColor(Formatting.WHITE))
         .build();
      this.nameScale = this.doubleBuilder(this.gx.add("name-scale")).defaultValue(0.75).build();
      this.gN = RenderCollectors.createBoxCollector(true, false, false);
      this.gO = RenderCollectors.d();
      this.gP = RenderCollectors.f();
      this.bindFlag(this.ae);
   }

   public void kn() {
      if (!this.pendingUpdateEntities) {
         this.pendingUpdateEntities = true;
         Tasks.m(() -> {
            if (!this.pendingUpdateEntities) {
               return true;
            } else if (!checkNull() && (mc.currentScreen == null || mc.currentScreen instanceof HandledScreen)) {
               this.pendingUpdateEntities = false;
               if (this.enableItem.get()) {
                  for (Entity var2 : mc.world.getEntities()) {
                     if (var2 instanceof ItemEntity var3) {
                        this.onItemEntity(var3, var3.getStack());
                     } else if (var2 instanceof ItemFrameEntity var4 && this.enableFrame.get()) {
                        this.onItemEntity(var4, var4.getHeldItemStack());
                     }
                  }
               }

               return true;
            } else {
               return false;
            }
         }, 1, 1);
      }
   }

   public void kq(Event<SerializedEntry<?>> entryUpdateEvent) {
      if (this.enableItem.get() && this.enableFrame.get()) {
         SerializedEntry var2 = (SerializedEntry)entryUpdateEvent.e();
         if (var2.id() == 9 && var2.value() instanceof ItemStack var4 && entryUpdateEvent.getArgs(0) instanceof ItemFrameEntity var5) {
            this.onItemEntity(var5, var4);
         }
      }
   }
}
