package me.matl114.managers;

import me.matl114.managers.input.KeyCode;
import me.matl114.managers.task.TaskManager;
import me.matl114.managers.task.ToggleManager;

public class TaskManagers {
   private static final TaskManager g = TaskManager.f();
   public static final String f = "hotkeys";
   public static final String b = "button-task";
   public static final String c = "hotkeys-toggle";
   public static final String a = "button-toggle";
   public static final String e = "toggle";
   private static final ToggleManager h = ToggleManager.of();
   public static final String d = "simple-toggle";

   public static ToggleManager c() {
      return h;
   }

   public static void init() {
      KeyCode.init();
   }

   public static TaskManager b() {
      return g;
   }
}
