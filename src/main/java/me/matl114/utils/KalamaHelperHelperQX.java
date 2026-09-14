package me.matl114.utils;

import com.google.common.base.Preconditions;
import java.util.Iterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

class KalamaHelperHelperQX implements Iterator<BlockPos> {
    double f;
    BlockPos i;
    BlockPos val$pos;
    double k;
    int l;
    double m;
    double n;
    int o;
    double p;
    double q;
    int r;
    double s;
    int c;
    int d;
    int e;
    double g;
    double h;

    KalamaHelperHelperQX(
            BlockPos var1,
            double var2,
            int var4,
            double var5,
            double var7,
            int var9,
            double var10,
            double var12,
            int var14,
            double var15) {
        this.val$pos = var1;
        this.k = var2;
        this.l = var4;
        this.m = var5;
        this.n = var7;
        this.o = var9;
        this.p = var10;
        this.q = var12;
        this.r = var14;
        this.s = var15;
        this.c = this.val$pos.getX();
        this.d = this.val$pos.getY();
        this.e = this.val$pos.getZ();
        this.f = this.k * (this.l > 0 ? 1.0 - MathHelper.fractionalPart(this.m) : MathHelper.fractionalPart(this.m));
        this.g = this.n * (this.o > 0 ? 1.0 - MathHelper.fractionalPart(this.p) : MathHelper.fractionalPart(this.p));
        this.h = this.q * (this.r > 0 ? 1.0 - MathHelper.fractionalPart(this.s) : MathHelper.fractionalPart(this.s));
        this.i = this.val$pos;
    }

    @Override
    public boolean hasNext() {
        if (this.i != null) {
            return true;
        } else if (!(this.f <= 1.0) && !(this.g <= 1.0) && !(this.h <= 1.0)) {
            return false;
        } else {
            if (this.f < this.g) {
                if (this.f < this.h) {
                    this.c = this.c + this.l;
                    this.f = this.f + this.k;
                } else {
                    this.e = this.e + this.r;
                    this.h = this.h + this.q;
                }
            } else if (this.g < this.h) {
                this.d = this.d + this.o;
                this.g = this.g + this.n;
            } else {
                this.e = this.e + this.r;
                this.h = this.h + this.q;
            }

            this.i = new BlockPos(this.c, this.d, this.e);
            return true;
        }
    }

    public BlockPos next() {
        BlockPos var1 = this.i;
        Preconditions.checkNotNull(var1);
        this.i = null;
        return var1;
    }
}
