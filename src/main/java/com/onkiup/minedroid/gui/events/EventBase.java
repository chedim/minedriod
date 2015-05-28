package com.onkiup.minedroid.gui.events;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Parent class for all event emitters
 */
public class EventBase {
    protected HashMap<Class, ArrayList<WeakReference<Event>>> eventListeners = new HashMap<Class, ArrayList<WeakReference<Event>>>();

    /**
     * Binds event listener via WeakReference (event type is determined by listener class)
     * @param listener event listener
     */
    public void on(Event listener) {
        Class eventType = getEventClass(listener.getClass());
        if (!eventListeners.containsKey(eventType)) {
            eventListeners.put(eventType, new ArrayList<WeakReference<Event>>());
        }

        eventListeners.get(eventType).add(new WeakReference<Event>(listener));
    }

    /**
     * Unbinds event listener
     * @param listener Listener to unbind
     */
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

    /**
     * Fires event
     * @param type Event type class
     * @param args Event argument
     */
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

    /**
     * Returns event class for event listener
     * @param c event listener class
     * @return event class
     */
    public Class getEventClass(Class c) {
        Class[] interfaces = c.getInterfaces();
        for (Class anInterface : interfaces) {
            if (Event.class.isAssignableFrom(anInterface)) return anInterface;
        }

        return null;
    }

}
