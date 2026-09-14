package me.matl114.utils.commands.params;

public class ArgumentReader {
   int currentCursor;
   private final String[] args;

   public String[] n() {
      return this.getArgsInRange(0, this.currentCursor);
   }

   public String[] getArgsInRange(int startIndex, int endIndex) {
      String[] var3 = new String[endIndex - startIndex];
      System.arraycopy(this.args, startIndex, var3, 0, var3.length);
      return var3;
   }

   public String g() {
      return this.args[this.currentCursor];
   }

   public String m() {
      StringBuilder var1 = new StringBuilder();

      for (String var5 : this.n()) {
         var1.append(var5).append(" ");
      }

      return var1.toString();
   }

   public ArgumentReader c(int cursor) {
      this.currentCursor = cursor;
      return this;
   }

   public int q() {
      return this.args.length;
   }

   public ArgumentReader(ArgumentReader reader) {
      this.args = reader.args;
      this.currentCursor = reader.currentCursor;
   }

   public ArgumentReader(String command, String[] args) {
      String[] var3 = new String[args.length + 1];
      System.arraycopy(args, 0, var3, 1, args.length);
      var3[0] = command;
      this.args = var3;
      this.currentCursor = 1;
   }

   public String j() {
      return String.join(" ", this.k());
   }

   public String a(int index) {
      return this.args[index];
   }

   public ArgumentReader h() {
      this.currentCursor++;
      return this;
   }

   public String l() {
      return String.join(" ", this.n());
   }

   public ArgumentReader i() {
      this.currentCursor--;
      return this;
   }

   public String p(int index) {
      return this.args[index];
   }

   public int b() {
      return this.currentCursor;
   }

   public ArgumentReader d() {
      while (this.hasNext()) {
         this.f();
      }

      return this;
   }

   public String[] k() {
      return this.getArgsInRange(this.currentCursor, this.args.length);
   }

   public ArgumentReader(String[] args) {
      this.args = args;
      this.currentCursor = 0;
   }

   public boolean hasNext() {
      return this.currentCursor < this.args.length;
   }

   public String f() {
      String var1 = this.args[this.currentCursor];
      this.currentCursor++;
      return var1;
   }
}
