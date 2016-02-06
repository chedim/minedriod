package com.onkiup.minedroid.exprop;

import com.onkiup.minedroid.MineDroid;
import com.onkiup.minedroid.Modification;
import com.onkiup.minedroid.gui.events.Event;
import com.onkiup.minedroid.EventBase;
import com.onkiup.minedroid.net.ExPropDeltaPacket;
import com.onkiup.minedroid.net.ExPropDeltaRequestPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.io.*;
import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.util.*;

/**
 * Created by chedim on 8/2/15.
 */
public abstract class ExProps extends EventBase implements IExtendedEntityProperties, Serializable {

    private static HashMap<Thread, List<WeakReference<ExProps>>> syncronizables
            = new HashMap<Thread, List<WeakReference<ExProps>>>();
    private HashMap<Field, Integer> fieldHashes = new HashMap<Field, Integer>();
    private Entity entity;
    private World world;

    protected ExProps() {
    }

    @Override
    public void init(Entity entity, World world) {
        this.entity = entity;
        this.world = world;
        if (isSyncronizable()) {
            synchronized (syncronizables) {
                List<WeakReference<ExProps>> threadList = syncronizables.get(Thread.currentThread());
                if (threadList == null) {
                    threadList = new ArrayList<WeakReference<ExProps>>();
                    syncronizables.put(Thread.currentThread(), threadList);
                }
                threadList.add(new WeakReference<ExProps>(this));
            }
        }
    }

    private String getTagName() {
        return getClass().getName();
    }

    @Override
    public void saveNBTData(NBTTagCompound tag) {
        Class s = getClass();
        String name = s.getName();
        NBTTagCompound me = new NBTTagCompound();
        for (Field field : s.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (Modifier.isStatic(field.getModifiers())) continue;
                if (Modifier.isFinal(field.getModifiers())) continue;
                Object value = field.get(this);
                if (value == null) continue;
                if (value == null || Serializable.class.isAssignableFrom(value.getClass())) {
                    writeSerializable(me, field.getName(), (Serializable) value);
                }
            } catch (IllegalAccessException e) {
                continue;
            } catch (ClassCastException e) {
                continue;
            }
        }
        tag.setTag(getTagName(), me);
    }

    @Override
    public void loadNBTData(NBTTagCompound tag) {
        Class s = getClass();
        NBTTagCompound me = tag.getCompoundTag(getTagName());
        if (me == null) return;
        for (Field field : s.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (Modifier.isStatic(field.getModifiers())) continue;
                if (Modifier.isFinal(field.getModifiers())) continue;
                String name = field.getName();
                Object val = readSerializable(me, name);
                if (val == null) continue;
                field.set(this, val);
            } catch (IllegalAccessException e) {
                continue;
            } catch (ClassCastException e) {
                continue;
            }
        }
    }

    protected static void writeSerializable(NBTTagCompound tag, String name, Serializable value) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream writer = new ObjectOutputStream(out);
            writer.writeObject(value);
            tag.setByteArray(name, out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static Object readSerializable(NBTTagCompound tag, String name) {
        try {
            if (!tag.hasKey(name)) throw new ClassCastException("Value not found");
            byte[] data = tag.getByteArray(name);
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream reader = new ObjectInputStream(in);
            return reader.readObject();
        } catch (ClassCastException e) {
            throw e;
        } catch (Exception e) {
            return null;
        }
    }

    public Class[] getApplicableClasses() {
        return new Class[]{Entity.class};
    }

    public static ExProps create(Entity entity, Class<? extends ExProps> type) {
        ExProps props = null;
        try {
            props = type.newInstance();
            Class entityClass = entity.getClass();
            boolean isAssignable = false;
            for (Class applicable : props.getApplicableClasses()) {
                if (isAssignable = applicable.isAssignableFrom(entityClass)) break;
            }
            if (!isAssignable) {
                throw new Exception("Properties of class '" + type.getName() +
                        "' cannot be applied to entity of class '" + entity.getClass().getName() + "'");
            }
            props.init(entity, entity.getEntityWorld());
//            props.populateHashes();
            props.register(entity);
            return props;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void register(Entity entity) {
        entity.registerExtendedProperties(getClass().getName(), this);
    }

    public static ExProps get(int id, Class<? extends ExProps> type) {
        Entity entity = null;
        try {
            Class.forName("net.minecraft.client.Minecraft");
            Minecraft.getMinecraft().theWorld.getEntityByID(id);
        } catch (ClassNotFoundException e) {
            entity = MinecraftServer.getServer().getEntityWorld().getEntityByID(id);
        }

        if (entity == null) return null;
        return get(entity, type);
    }

    public static ExProps get(Entity entity, Class<? extends ExProps> type) {
        if (entity == null) return null;
        ExProps props = getIfExists(entity, type);
        if (props == null) {
            props = create(entity, type);
        }
        return props;
    }

    public static ExProps getIfExists(Entity entity, Class<? extends ExProps> type) {
        return (ExProps) entity.getExtendedProperties(type.getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!obj.getClass().equals(getClass())) return false;

        for (Field f : getClass().getFields()) {
            try {
                Object m = f.get(this), o = f.get(obj);
                if (m == f) continue;
                else if (m == null) return false;
                else if (!m.equals(o)) return false;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (Field f : getClass().getFields()) {
            try {
                result = 37 * result + getObjHash(f.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * Recursive object value hashcode calculation
     * @param v
     * @return
     */
    private int getObjHash(Object v) {
        int result = 0;
        if (v != null) {
            Class t = v.getClass();
            if (t.isPrimitive() || v instanceof Map || v instanceof List) {
                result = v.hashCode();
            } else if (t.isArray()) {
                for (int i = 0; i < Array.getLength(v); i++) {
                    result = result * 37 + getObjHash(Array.get(v, i));
                }
            } else {
                for (Field f: v.getClass().getFields()) {
                    try {
                        if (Modifier.isStatic(f.getModifiers())) continue;
                        f.setAccessible(true);
                        Object val = f.get(v);
                        if (!Serializable.class.isAssignableFrom(val.getClass())) continue;
                        result = result * 37 + getObjHash(val);
                    } catch (IllegalAccessException e) {
                        continue;
                    }
                }
            }
        }
        return result;
    }

    protected boolean isSyncronizable() {
        return false;
    }

    public boolean allowClientCreation() {
        return false;
    }

    protected void populateHashes() {
        for (Field field : getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (Modifier.isPrivate(field.getModifiers())) continue;
                if (Modifier.isStatic(field.getModifiers())) continue;
                Object value = field.get(this);
                Integer newHash = getObjHash(value);
                fieldHashes.put(field, newHash);
            } catch (IllegalAccessException e) {
                continue;
            }
        }
    }

    public HashMap<String, Object> getDelta(boolean clear) {
        HashMap<String, Object> delta = new HashMap<String, Object>();
        for (Field field : getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (Modifier.isPrivate(field.getModifiers())) continue;
                Object value = field.get(this);
                Integer oldHash = fieldHashes.get(field);
                Integer newHash = getObjHash(value);
                if (!newHash.equals(oldHash)) delta.put(field.getName(), value);
                if (clear) fieldHashes.put(field, newHash);
            } catch (IllegalAccessException e) {
                continue;
            }
        }
        return delta;
    }

    public HashMap<String, Integer> getHashes() {
        return new HashMap<String, Integer>((Map) fieldHashes);
    }

    public HashMap<String, Object> getDelta(HashMap<String, Integer> from) {
        HashMap<String, Object> delta = new HashMap<String, Object>();
        for (Field field : getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (Modifier.isPrivate(field.getModifiers())) continue;
                Object value = field.get(this);
                Integer oldHash = from.get(field);
                Integer newHash = getObjHash(value);
                if (!newHash.equals(oldHash)) delta.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                continue;
            }
        }
        return delta;
    }

    public boolean applyDelta(HashMap<String, Object> delta) {
        boolean result = false;
        for (Field field: getClass().getDeclaredFields()) {
            String name = field.getName();
            if (delta.containsKey(name)) {
                try {
                    Object value = delta.get(name);
                    if (Modification.isServerThread() && !callFieldListener(name, value)) continue;
                    field.setAccessible(true);
                    Integer oldHash = fieldHashes.get(field);
                    Integer hash = getObjHash(value);
                    if (!hash.equals(oldHash)) {
                        field.set(this, value);
                        fieldHashes.put(field, hash);
                        result = true;
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return result;
    }

    protected static List<WeakReference<ExProps>> getSyncronizables() {
        synchronized (syncronizables) {
            List<WeakReference<ExProps>> tl = syncronizables.get(Thread.currentThread());
            if (tl == null) return null;
            List<WeakReference<ExProps>> remove = new ArrayList<WeakReference<ExProps>>();
            List<WeakReference<ExProps>> result = new ArrayList<WeakReference<ExProps>>();
            for (WeakReference<ExProps> reference: tl) {
                if (reference.get() == null) remove.add(reference);
                else result.add(reference);
            }
            tl.removeAll(remove);
            return result;
        }
    }

    protected Boolean callFieldListener(String name, Object value) {
        try {
            Method m = getClass().getDeclaredMethod(name+"Delta", Object.class);
            m.setAccessible(true);
            if (m.getReturnType() != Boolean.class) return true;
            return (Boolean) m.invoke(this, value);
        } catch (NoSuchMethodException e) {
            return true;
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public World getWorld() {
        return world;
    }

    public boolean hadChanged() {
        for (Field field : getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (Modifier.isPrivate(field.getModifiers())) continue;
                Object value = field.get(this);
                Integer oldHash = fieldHashes.get(field);
                Integer newHash;
                if (value == null) newHash = 0;
                else newHash = value.hashCode();
                if (!newHash.equals(oldHash)) return true;
            } catch (IllegalAccessException e) {
                continue;
            }
        }
        return false;
    }

    public interface Changed<X extends ExProps> extends Event<X> {}
}
