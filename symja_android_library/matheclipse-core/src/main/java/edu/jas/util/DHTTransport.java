/*
 * $Id$
 */

package edu.jas.util;


import java.io.IOException;
import java.io.Serializable;


/**
 * Transport container for a distributed version of a HashTable.
 * <b>Note:</b> Contains code for timing of marshalled versus plain
 * object serialization which can be removed later.
 *
 * @author Heinz Kredel
 */
public abstract class DHTTransport<K, V> implements Serializable {


    public static final Stor stor = Stor.marshal; //Stor.plain;
    static long etime = 0L; // encode marshalled
    static long dtime = 0L; // decode marshalled
    static long ertime = 0L; // encode plain raw
    static long drtime = 0L; // decode plain raw

    ;


    /**
     * protected constructor.
     */
    protected DHTTransport() {
    }

    /**
     * Create a new DHTTransport Container.
     *
     * @param key
     * @param value
     */
    public static <K, V> DHTTransport<K, V> create(K key, V value) throws IOException {
        switch (stor) {
            case plain:
                return new DHTTransportPlain<K, V>(key, value);
            default:
                throw new IllegalArgumentException("this should not happen");
        }
    }

    /**
     * Get the key from this DHTTransport Container.
     */
    public abstract K key() throws IOException, ClassNotFoundException;

    /**
     * Get the value from this DHTTransport Container.
     */
    public abstract V value() throws IOException, ClassNotFoundException;

    /**
     * toString.
     */
    @Override
    public String toString() {
        return this.getClass().getName();

    }


    public static enum Stor { // storage and transport class
        marshal, plain
    }

}


/**
 * Transport container to signal termination for a distributed version
 * of a HashTable. Contains no objects.
 */
class DHTTransportTerminate<K, V> extends DHTTransport<K, V> {

    /**
     * Get the key from this DHTTransport Container.
     */
    public K key() throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("this should not happen");
    }


    /**
     * Get the value from this DHTTransport Container.
     */
    public V value() throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("this should not happen");
    }

}


/**
 * Transport container to signal clearing contents of the
 * other HashTables including the server. Contains no objects.
 */
class DHTTransportClear<K, V> extends DHTTransport<K, V> {

    /**
     * Get the key from this DHTTransport Container.
     */
    public K key() throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("this should not happen");
    }


    /**
     * Get the value from this DHTTransport Container.
     */
    public V value() throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("this should not happen");
    }

}


/**
 * Transport container for a distributed version of a HashTable. Immutable
 * objects. Uses plain objects.
 */
class DHTTransportPlain<K, V> extends DHTTransport<K, V> {


    protected final K key;


    protected final V value;


    /**
     * Constructs a new DHTTransport Container.
     *
     * @param key
     * @param value
     */
    public DHTTransportPlain(K key, V value) throws IOException {
        this.key = key;
        this.value = value;
    }


    /**
     * Get the key from this DHTTransport Container.
     */
    public K key() throws IOException, ClassNotFoundException {
        return this.key;
    }


    /**
     * Get the value from this DHTTransport Container.
     */
    public V value() throws IOException, ClassNotFoundException {
        return this.value;
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return super.toString() + "(" + key + "," + value + ")";
    }


    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        long t = System.currentTimeMillis();
        out.defaultWriteObject();
        t = System.currentTimeMillis() - t;
        synchronized (DHTTransport.class) {
            ertime += t;
        }
    }


    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        long t = System.currentTimeMillis();
        in.defaultReadObject();
        t = System.currentTimeMillis() - t;
        synchronized (DHTTransport.class) {
            drtime += t;
        }
    }

}
