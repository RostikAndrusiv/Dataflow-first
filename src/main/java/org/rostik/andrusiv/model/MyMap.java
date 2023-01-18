package org.rostik.andrusiv.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MyMap implements Map<String, Object>, Serializable {
    private static final long serialVersionUID = 1L;
    
    private final Map<String, Object> internalMap;

    public MyMap() {
        this.internalMap = new LinkedHashMap<>();
    }

    public MyMap(Map<String, Object> map) {
        this.internalMap = new LinkedHashMap<>(map);
    }

    public int size() {
        return this.internalMap.size();
    }

    public boolean isEmpty() {
        return this.internalMap.isEmpty();
    }

    public boolean containsValue(Object value) {
        return this.internalMap.containsValue(value);
    }

    public boolean containsKey(Object key) {
        return this.internalMap.containsKey(key);
    }

    public Object get(Object key) {
        return this.internalMap.get(key);
    }

    public Object put(String key, Object value) {
        return this.internalMap.put(key, value);
    }

    public Object remove(Object key) {
        return this.internalMap.remove(key);
    }

    public void putAll(Map<? extends String, ?> map) {
        this.internalMap.putAll(map);
    }

    public void clear() {
        this.internalMap.clear();
    }

    public Set<String> keySet() {
        return this.internalMap.keySet();
    }

    public Collection<Object> values() {
        return this.internalMap.values();
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return this.internalMap.entrySet();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            MyMap document = (MyMap)o;
            return this.internalMap.equals(document.internalMap);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.internalMap.hashCode();
    }

    public String toString() {
        return "MyMap{" + this.internalMap + '}';
    }
}
