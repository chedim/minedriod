package com.onkiup.minedroid.gui.events;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by chedim on 4/26/15.
 */
public class EventBase {
    protected HashMap<Class, ArrayList<WeakReference<Event>>> eventListeners = new HashMap<Class, ArrayList<WeakReference<Event>>>();

    public void on(Event listener) {
        Class eventType = getEventClass(listener.getClass());
        if (!eventListeners.containsKey(eventType)) {
            eventListeners.put(eventType, new ArrayList<WeakReference<Event>>());
        }

        eventListeners.get(eventType).add(new WeakReference<Event>(listener));
    }

    public void off(Event listener) {
        Class eventType = getEventClass(listener.getClass());
        if (!eventListeners.containsKey(eventType)) {
            return;
        }

        ArrayList<WeakReference<Event>> listeners = eventListeners.get(eventType);
        ArrayList<WeakReference> rm = new ArrayList<WeakReference>();
        for (WeakReference reference: listeners) {
            if (listener == reference.get()) {
                rm.add(reference);
            }
        }
        listeners.removeAll(rm);
    }

    public void fireEvent(Class type, Object args) {
        if (!eventListeners.containsKey(type)) return;
        ArrayList<WeakReference<Event>> listeners = eventListeners.get(type);
        ArrayList<WeakReference<Event>> rm = new ArrayList<WeakReference<Event>>();
        for (WeakReference<Event> reference: listeners) {
            Event listener = reference.get();
            if (listener == null) {
                rm.add(reference);
                continue;
            }
            listener.handle(args);
        }

        listeners.removeAll(rm);
    }

    public Class getEventClass(Class c) {
        Class[] interfaces = c.getInterfaces();
        for (int i=0; i<interfaces.length; i++) {
            if (Event.class.isAssignableFrom(interfaces[i])) return interfaces[i];
        }

        return null;
    }

}
