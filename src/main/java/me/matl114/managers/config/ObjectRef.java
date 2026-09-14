package me.matl114.managers.config;

import me.matl114.utils.config.BaseAttrKeyValue;

public abstract class ObjectRef<T> extends Ref<T> {
    private T object;

    @Override
    public final T getValue() {
        return this.get();
    }

    public T get() {
        return this.object;
    }

    @Override
    public final void setValue(T value) {
        this.set(value);
    }

    @Override
    public abstract Object getAsPrimitive();

    protected abstract T validateAndCast(Object var1);

    public void set(T val) {
        T cas = this.validateAndCast(val);
        if (this.validateUpdateValue(cas)) {
            this.object = cas;
            this.callUpdate();
        }
    }

    public ObjectRef(T object) {
        this.object = object;
    }

    public static class JustOnlyObjectRef extends ObjectRef<Object> {
        public JustOnlyObjectRef(Object object) {
            super(object);
        }

        public <W> boolean copyValueTo(Ref<W> otherRef) {
            if (otherRef.getClass() == ObjectRef.JustOnlyObjectRef.class) {
                ((ObjectRef.JustOnlyObjectRef) otherRef).set(this.get());
                return true;
            } else {
                return false;
            }
        }

        @Override
        public BaseAttrKeyValue<Object> _createKeyValue0(String key) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Object validateAndCast(Object val) {
            return val;
        }

        @Override
        public Object getAsPrimitive() {
            return this.get().toString();
        }

        @Override
        public <W> boolean isSameTypeWith(Ref<W> ref) {
            return ref.getClass() == ObjectRef.JustOnlyObjectRef.class;
        }

        @Override
        public <W> boolean copyValueFrom(Ref<W> otherRef) {
            if (otherRef.getClass() == ObjectRef.JustOnlyObjectRef.class) {
                this.set(((ObjectRef.JustOnlyObjectRef) otherRef).get());
                return true;
            } else {
                return false;
            }
        }
    }
}
