package javax.script;

import java.util.*;

// Referenced classes of package javax.script:
 //            Namespace

public class SimpleNamespace
    implements Namespace
{

    private Map map;

    public SimpleNamespace(Map m)
    {
        map = m;
    }

    public SimpleNamespace()
    {
        this(((Map) (new HashMap())));
    }

    public Object put(Object name, Object value)
    {
        if(name == null)
        {
            throw new NullPointerException("Name may not be null.");
        }
        if(!(name instanceof String))
        {
            throw new ClassCastException("Specified key is not a String");
        } else
        {
            map.put(name, value);
            return value;
        }
    }

    public void putAll(Map toMerge)
    {
        Entry entry;
        for(Iterator it = toMerge.entrySet().iterator(); it.hasNext(); put((String)entry.getKey(), entry.getValue()))
        {
            entry = (Entry)it.next();
        }

    }

    public void clear()
    {
        map.clear();
    }

    public boolean containsKey(Object key)
    {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value)
    {
        return map.containsValue(value);
    }

    public Set entrySet()
    {
        return map.entrySet();
    }

    public Object get(Object key)
    {
        return map.get(key);
    }

    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    public Set keySet()
    {
        return map.keySet();
    }

    public Object remove(Object key)
    {
        return map.remove(key);
    }

    public int size()
    {
        return map.size();
    }

    public Collection values()
    {
        return map.values();
    }
}
