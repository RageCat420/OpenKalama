package me.matl114.hacks.modules.chat;

import com.mojang.datafixers.util.Pair;
import java.security.spec.AlgorithmParameterSpec;

public interface ChatSubHelperM {
   Pair<AlgorithmParameterSpec, byte[]> splitIV(byte[] var1);

   Pair<AlgorithmParameterSpec, byte[]> generateIV();
}
