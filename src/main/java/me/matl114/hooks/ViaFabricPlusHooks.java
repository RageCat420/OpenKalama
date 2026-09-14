package me.matl114.hooks;

import com.google.common.base.Preconditions;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.api.ViaFabricPlusBase;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.Types;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import me.matl114.versioned.SupportVersion;
import net.minecraft.network.packet.PacketType;

public class ViaFabricPlusHooks implements IHooks {
   public static ViaFabricPlusHooks instance;

   public static ViaFabricPlusHooks getInstance() {
      if (instance == null) {
         try {
            instance = new ViaFabricPlusHooks.Impl();
         } catch (Throwable var5) {
            try {
               instance = new ViaFabricPlusHooks.ImplOld();
            } catch (Throwable var4) {
               try {
                  instance = new ViaFabricPlusHooks.ImplWTF();
               } catch (Throwable var3) {
                  instance = new ViaFabricPlusHooks.Default();
               }
            }
         }
      }

      return instance;
   }

   public abstract SupportVersion getCurrentVersion();

   public abstract boolean isViaEnabled();

   public abstract ViaFabricPlusHooks.ViaPacketWrapper createViaPacket();

   public static boolean isSupportEndTick() {
      return getInstance().getCurrentVersion().b(21, 2);
   }

   public static boolean isSupportDupRot() {
      return getInstance().getCurrentVersion().c(20, 7);
   }

   public static boolean isSupportInstaSneak() {
      return getInstance().getCurrentVersion().b(21, 6);
   }

   public static class AbstractViaFabricImpl extends ViaFabricPlusHooks {
      ProtocolVersion lastProtocol = null;
      SupportVersion lastVersion = null;

      public AbstractViaFabricImpl() {
         Class<?> viaClass = ProtocolVersion.class;
      }

      protected abstract ProtocolVersion getTargetVersion0();

      @Override
      public SupportVersion getCurrentVersion() {
         ProtocolVersion currentProtocol = this.getTargetVersion0();
         if (!Objects.equals(currentProtocol, this.lastProtocol) || this.lastVersion == null) {
            try {
               this.lastVersion = SupportVersion.parse((String)currentProtocol.getIncludedVersions().stream().findFirst().orElseThrow());
               this.lastProtocol = currentProtocol;
            } catch (Throwable var3) {
               this.lastVersion = SupportVersion.CURRENT;
               this.lastProtocol = currentProtocol;
            }
         }

         return this.lastVersion;
      }

      @Override
      public ViaFabricPlusHooks.ViaPacketWrapper createViaPacket() {
         return new ViaFabricPlusHooks.ViaPacketWrapperImpl();
      }

      @Override
      public boolean isViaEnabled() {
         return true;
      }
   }

   public static class Default extends ViaFabricPlusHooks {
      @Override
      public SupportVersion getCurrentVersion() {
         return SupportVersion.CURRENT;
      }

      @Override
      public boolean isViaEnabled() {
         return false;
      }

      @Override
      public ViaFabricPlusHooks.ViaPacketWrapper createViaPacket() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isEnabled() {
         return false;
      }
   }

   public static class Impl extends ViaFabricPlusHooks.AbstractViaFabricImpl {
      ViaFabricPlusBase base;

      @Override
      protected ProtocolVersion getTargetVersion0() {
         return this.base.getTargetVersion();
      }

      @Override
      public boolean isEnabled() {
         return true;
      }

      public Impl() {
         Class<?> clazz = ViaFabricPlus.class;
         this.base = Objects.requireNonNull(ViaFabricPlus.getImpl());
      }
   }

   public static class ImplOld extends ViaFabricPlusHooks.AbstractViaFabricImpl {
      de.florianmichael.viafabricplus.ViaFabricPlus base;

      public ImplOld() {
         Class<?> clazz = de.florianmichael.viafabricplus.ViaFabricPlus.class;
         this.base = Objects.requireNonNull(de.florianmichael.viafabricplus.ViaFabricPlus.global());
         Class<?> clazz2 = ProtocolTranslator.class;
      }

      @Override
      protected ProtocolVersion getTargetVersion0() {
         return ProtocolTranslator.getTargetVersion();
      }

      @Override
      public boolean isEnabled() {
         return true;
      }
   }

   public static class ImplWTF extends ViaFabricPlusHooks.AbstractViaFabricImpl {
      @Override
      protected ProtocolVersion getTargetVersion0() {
         return null;
      }

      @Override
      public SupportVersion getCurrentVersion() {
         return SupportVersion.CURRENT;
      }

      @Override
      public boolean isEnabled() {
         return false;
      }
   }

   public interface ViaPacketWrapper {
      default ViaFabricPlusHooks.ViaPacketWrapper writePacketType(String protocolVersion, PacketType<?> packetType) {
         return this.writePacketType(protocolVersion, packetType.id().getPath().toUpperCase(Locale.ROOT));
      }

      ViaFabricPlusHooks.ViaPacketWrapper writePacketType(String var1, String var2);

      ViaFabricPlusHooks.ViaPacketWrapper write(String var1, Object var2);

      void scheduleSendToServer(String var1, boolean var2);

      void sendToServer(String var1, boolean var2);

      void sendRaw(boolean var1);
   }

   public static class ViaPacketWrapperImpl implements ViaFabricPlusHooks.ViaPacketWrapper {
      PacketWrapper delegate;
      static Map<String, Map<String, com.viaversion.viaversion.api.protocol.packet.PacketType>> types = new HashMap<>();
      static final Map<String, Type<?>> typeMap = new HashMap<>();
      static Map<String, Class<?>> protocolCache;

      private static Map<String, com.viaversion.viaversion.api.protocol.packet.PacketType> computeAndGuessTypes(String protocolVersion) {
         protocolVersion = protocolVersion.toLowerCase(Locale.ROOT);
         String[] splits = protocolVersion.split("to");
         if (splits.length != 2) {
            throw new IllegalArgumentException("Can not parse protocol " + protocolVersion);
         } else {
            String higherProtocol = splits[1].toLowerCase(Locale.ROOT);
            String[] availableScannPath = new String[]{"ServerboundPackets", "ClientboundPackets"};
            Map<String, com.viaversion.viaversion.api.protocol.packet.PacketType> types = new HashMap<>();

            for (String scannPath : availableScannPath) {
               String fullPath = "com.viaversion.viaversion.protocols.v" + protocolVersion + ".packet." + scannPath + higherProtocol;

               try {
                  Class<?> clazz = Class.forName(fullPath);
                  if (Enum.class.isAssignableFrom(clazz) && com.viaversion.viaversion.api.protocol.packet.PacketType.class.isAssignableFrom(clazz)) {
                     for (Object re : clazz.getEnumConstants()) {
                        types.put(((Enum)re).name(), (com.viaversion.viaversion.api.protocol.packet.PacketType)re);
                     }
                  }
               } catch (Throwable var15) {
               }
            }

            return types;
         }
      }

      public static com.viaversion.viaversion.api.protocol.packet.PacketType getPacketType(String protocolVersion, String packetType) {
         String version = protocolVersion.startsWith("v") ? protocolVersion.substring(1) : protocolVersion;
         return types.computeIfAbsent(version, ViaFabricPlusHooks.ViaPacketWrapperImpl::computeAndGuessTypes).get(packetType);
      }

      public static <T> Type<T> getType(String type) {
         return Objects.requireNonNull((Type<T>)typeMap.get(type));
      }

      @Override
      public ViaFabricPlusHooks.ViaPacketWrapper writePacketType(String protocolVersion, String packetType) {
         com.viaversion.viaversion.api.protocol.packet.PacketType type = Objects.requireNonNull(getPacketType(protocolVersion, packetType));
         if (this.delegate == null) {
            this.delegate = PacketWrapper.create(type, ViaFabricPlus.getImpl().getPlayNetworkUserConnection());
         } else {
            this.delegate.setPacketType(type);
         }

         return this;
      }

      @Override
      public ViaFabricPlusHooks.ViaPacketWrapper write(String type, Object val) {
         Preconditions.checkNotNull(this.delegate, "Set packet type before write");
         this.delegate.write(Objects.requireNonNull(getType(type)), val);
         return this;
      }

      public static Class<?> getOrCache(String protocol) {
         String tryFindVersion = (protocol.startsWith("v") ? protocol.substring(1) : protocol).toLowerCase(Locale.ROOT);
         if (protocolCache.containsKey(tryFindVersion)) {
            return protocolCache.get(tryFindVersion);
         } else {
            String className = "com.viaversion.viaversion.protocols.v" + tryFindVersion + ".Protocol" + tryFindVersion.replace("to", "To");

            try {
               Class<?> clazz = Class.forName(className);
               protocolCache.put(tryFindVersion, clazz);
               return clazz;
            } catch (Throwable var4) {
               throw new RuntimeException(var4);
            }
         }
      }

      @Override
      public void scheduleSendToServer(String protocol, boolean skipPipeline) {
         Preconditions.checkNotNull(this.delegate, "Set packet type before send");
         this.delegate.scheduleSendToServer(getOrCache(protocol), skipPipeline);
      }

      @Override
      public void sendToServer(String protocol, boolean skipPipeline) {
         Preconditions.checkNotNull(this.delegate, "Set packet type before send");
      }

      @Override
      public void sendRaw(boolean currentThread) {
         Preconditions.checkNotNull(this.delegate, "Set packet type before send");
      }

      static {
         Field[] fields = Types.class.getDeclaredFields();

         for (Field field : fields) {
            field.setAccessible(true);
            if (Type.class.isAssignableFrom(field.getType()) && Modifier.isStatic(field.getModifiers())) {
               try {
                  Type type = (Type)field.get(null);
                  typeMap.put(field.getName(), type);
               } catch (Throwable var6) {
               }
            }
         }

         protocolCache = new HashMap<>();
      }
   }
}
