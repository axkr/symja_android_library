package javax.script;

import java.util.Map;

public interface Namespace
    extends Map
{

    public abstract Object put(Object obj, Object obj1);

    public abstract void putAll(Map map);
}
