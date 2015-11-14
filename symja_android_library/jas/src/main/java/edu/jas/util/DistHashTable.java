/*
 * $Id$
 */

package edu.jas.util;


import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
//import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.log4j.Logger;


/**
 * Distributed version of a HashTable. Implemented with a SortedMap / TreeMap to
 * keep the sequence order of elements.
 * @author Heinz Kredel
 */

public class DistHashTable<K, V> extends AbstractMap<K, V> /* implements Map<K,V> */{


    private static final Logger logger = Logger.getLogger(DistHashTable.class);


    private static boolean debug = logger.isDebugEnabled();


    protected final SortedMap<K, V> theList;


    protected final ChannelFactory cf;


    protected SocketChannel channel = null;


    protected DHTListener<K, V> listener = null;


    /**
     * Constructs a new DistHashTable.
     * @param host name or IP of server host.
     */
    public DistHashTable(String host) {
        this(host, DistHashTableServer.DEFAULT_PORT);
    }


    /**
     * DistHashTable.
     * @param host name or IP of server host.
     * @param port on server.
     */
    public DistHashTable(String host, int port) {
        this(new ChannelFactory(port + 1), host, port);
    }


    /**
     * DistHashTable.
     * @param cf ChannelFactory to use.
     * @param host name or IP of server host.
     * @param port on server.
     */
    public DistHashTable(ChannelFactory cf, String host, int port) {
        this.cf = cf;
        cf.init(); // why? see constructor
        try {
            channel = cf.getChannel(host, port);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        if (debug) {
            logger.debug("dl channel = " + channel);
        }
        //theList = new ConcurrentSkipListMap<K, V>(); // Java 1.6
        theList = new TreeMap<K, V>();
        listener = new DHTListener<K, V>(channel, theList);
        // listener.start() is in initialize()
    }


    /**
     * DistHashTable.
     * @param sc SocketChannel to use.
     */
    public DistHashTable(SocketChannel sc) {
        cf = null;
        channel = sc;
        //theList = new ConcurrentSkipListMap<K, V>(); // Java 1.6
        theList = new TreeMap<K, V>();
        listener = new DHTListener<K, V>(channel, theList);
        // listener.start() is in initialize()
    }


    /**
     * Hash code.
     */
    @Override
    public int hashCode() {
        return theList.hashCode();
    }


    /**
     * Equals.
     */
    @Override
    public boolean equals(Object o) {
        return theList.equals(o);
    }


    /**
     * Contains key.
     */
    @Override
    public boolean containsKey(Object o) {
        return theList.containsKey(o);
    }


    /**
     * Contains value.
     */
    @Override
    public boolean containsValue(Object o) {
        return theList.containsValue(o);
    }


    /**
     * Get the values as Collection.
     */
    @Override
    public Collection<V> values() {
        synchronized (theList) {
            return new ArrayList<V>(theList.values());
            //return theList.values();
        }
    }


    /**
     * Get the keys as set.
     */
    @Override
    public Set<K> keySet() {
        synchronized (theList) {
            return theList.keySet();
        }
    }


    /**
     * Get the entries as Set.
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        synchronized (theList) {
            return theList.entrySet();
        }
    }


    /**
     * Get the internal list, convert from Collection.
     */
    // To be fixed?, but is ok.
    public List<V> getValueList() {
        synchronized (theList) {
            return new ArrayList<V>(theList.values());
        }
    }


    /**
     * Get the internal sorted map. For synchronization purpose in normalform.
     */
    public SortedMap<K, V> getList() {
        return theList;
    }


    /**
     * Size of the (local) list.
     */
    @Override
    public int size() {
        synchronized (theList) {
            return theList.size();
        }
    }


    /**
     * Is the List empty?
     */
    @Override
    public boolean isEmpty() {
        synchronized (theList) {
            return theList.isEmpty();
        }
    }


    /**
     * List key iterator.
     */
    public Iterator<K> iterator() {
        synchronized (theList) {
            return theList.keySet().iterator();
        }
    }


    /**
     * List value iterator.
     */
    public Iterator<V> valueIterator() {
        synchronized (theList) {
            return theList.values().iterator();
        }
    }


    /**
     * Put object to the distributed hash table. Blocks until the key value pair
     * is send and received from the server.
     * @param key
     * @param value
     */
    public void putWait(K key, V value) {
        //V o = 
        put(key, value); // = send
        // assume key does not change multiple times before test:
        while (!value.equals(getWait(key))) {
            //System.out.print("#");
        }
    }


    /**
     * Put object to the distributed hash table. Returns immediately after
     * sending, does not block.
     * @param key
     * @param value
     */
    @Override
    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("null keys or values not allowed");
        }
        try {
            DHTTransport<K, V> tc = DHTTransport.<K, V> create(key, value);
            channel.send(tc);
            //System.out.println("send: "+tc+" @ "+listener);
        } catch (IOException e) {
            logger.info("send, exception " + e);
            e.printStackTrace();
        } catch (Exception e) {
            logger.info("send, exception " + e);
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Get value under key from DHT. Blocks until the object is send and
     * received from the server (actually it blocks until some value under key
     * is received).
     * @param key
     * @return the value stored under the key.
     */
    public V getWait(K key) {
        V value = null;
        try {
            synchronized (theList) {
                //value = theList.get(key);
                value = get(key);
                while (value == null) {
                    //System.out.print("^");
                    theList.wait(100);
                    value = theList.get(key);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        return value;
    }


    /**
     * Get value under key from DHT. If no value is jet available null is
     * returned.
     * @param key
     * @return the value stored under the key.
     */
    @Override
    public V get(Object key) {
        synchronized (theList) {
            return theList.get(key);
        }
    }


    /**
     * Clear the List. 
     * Clearance request is distributed to all clients.
     */
    @Override
    public void clear() {
        synchronized (theList) {
            theList.clear();
        }
        // done after 11 month: send clear message to others
        try {
            DHTTransport<K, V> tc = new DHTTransportClear<K, V>();
            channel.send(tc);
            //System.out.println("send: "+tc+" @ "+listener);
        } catch (IOException e) {
            logger.info("send, exception " + e);
            e.printStackTrace();
        } catch (Exception e) {
            logger.info("send, exception " + e);
            e.printStackTrace();
        }
    }


    /**
     * Initialize and start the list thread.
     */
    public void init() {
        if (listener == null) {
            return;
        }
        if (listener.isDone()) {
            return;
        }
        if (debug) {
            logger.debug("initialize " + listener);
        }
        synchronized (theList) {
            listener.start();
        }
    }


    /**
     * Terminate the list thread.
     */
    public void terminate() {
        if (cf != null) {
            cf.terminate();
        }
        if (channel != null) {
            channel.close();
        }
        //theList.clear();
        if (listener == null) {
            return;
        }
        if (debug) {
            logger.debug("terminate " + listener);
        }
        listener.setDone();
        try {
            while (listener.isAlive()) {
                //System.out.print("+");
                listener.interrupt();
                listener.join(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        listener = null;
    }

}


/**
 * Thread to comunicate with the list server.
 */
class DHTListener<K, V> extends Thread {


    private static final Logger logger = Logger.getLogger(DHTListener.class);


    private static boolean debug = logger.isDebugEnabled();


    private final SocketChannel channel;


    private final SortedMap<K, V> theList;


    private boolean goon;


    DHTListener(SocketChannel s, SortedMap<K, V> list) {
        channel = s;
        theList = list;
        goon = true;
    }


    boolean isDone() {
        return !goon;
    }


    void setDone() {
        goon = false;
    }


    /**
     * run.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        logger.debug("running ");
        Object o;
        DHTTransport<K, V> tc;
        //goon = true;
        while (goon) {
            tc = null;
            o = null;
            try {
                o = channel.receive();
                if (debug) {
                    logger.debug("receive(" + o + ")");
                }
                if (this.isInterrupted()) {
                    goon = false;
                    break;
                }
                if (o == null) {
                    goon = false;
                    break;
                }
                if (o instanceof DHTTransportClear) {
                    logger.debug("receive, clear");
                    synchronized (theList) {
                        theList.clear();
                        theList.notifyAll();
                    }
                    continue;
                }
                if (o instanceof DHTTransport) {
                    tc = (DHTTransport<K, V>) o;
                    K key = tc.key();
                    if (key != null) {
                        logger.info("receive, put(key=" + key + ")");
                        V val = tc.value();
                        synchronized (theList) {
                            theList.put(key, val);
                            theList.notifyAll();
                        }
                    }
                }
            } catch (IOException e) {
                goon = false;
                logger.info("receive, IO exception " + e);
                //e.printStackTrace();
            } catch (ClassNotFoundException e) {
                goon = false;
                logger.info("receive, CNF exception " + e);
                e.printStackTrace();
            } catch (Exception e) {
                goon = false;
                logger.info("receive, exception " + e);
                e.printStackTrace();
            }
        }
    }

}
