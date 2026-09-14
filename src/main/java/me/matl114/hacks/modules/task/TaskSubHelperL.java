package me.matl114.hacks.modules.task;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Map.Entry;

public record TaskSubHelperL(Map<String, String> ipToFolder, Map<String, String> ipProxy) {
   public static final Codec<TaskSubHelperL> CODEC = RecordCodecBuilder.create(
      oinstance -> oinstance.group(
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("persistent-storage-name-mapper", Map.of()).forGetter(TaskSubHelperL::ipToFolder),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("ip-proxies", Map.of()).forGetter(TaskSubHelperL::ipProxy)
         )
         .apply(oinstance, TaskSubHelperL::new)
   );

    public TaskSubHelperL VE(Map<String, String> ipToFolder) {
       return this.ipToFolder == ipToFolder ? this : new TaskSubHelperL(ipToFolder, this.ipProxy);
    }

   public Map<String, String> ipToFolder() {
      return this.ipToFolder;
   }

    public String VD(String ip) {
       return this.ipProxy.getOrDefault(ip, ip);
    }

   public Map<String, String> ipProxy() {
      return this.ipProxy;
   }

    public TaskSubHelperL VF(Map<String, String> ipProxy) {
       return this.ipProxy == ipProxy ? this : new TaskSubHelperL(this.ipToFolder, ipProxy);
    }

   

   public String VC(String ip) {
      String var2 = this.VD(ip);

       for (Entry var4 : this.ipToFolder.entrySet()) {
         if (((String)var4.getKey()).equalsIgnoreCase(var2)) {
            return (String)var4.getValue();
         }
      }

      return var2;
   }
}
