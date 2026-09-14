package me.matl114.managers.config;

import java.util.HashSet;
import java.util.Set;

public interface AutoRegisterType {
   Set<Class<? extends AutoRegisterType>> registered = new HashSet<>();
}
