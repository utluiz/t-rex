package br.com.starcode.trex.util;

import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Utility class for use a ResourceBundle like a Map.
 * It's a ready only class.
 */
public class ResourceBundleMap implements Map<String, String> {

    protected ResourceBundle resource;
    
    public ResourceBundleMap(ResourceBundle resource) {
        this.resource = resource;
    }
    
    public int size() {
        return 0;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean containsKey(Object key) {
        return resource.containsKey(key == null ? null : key.toString());
    }

    public boolean containsValue(Object value) {
        return false;
    }

    public String get(Object key) {
        return resource.getString(key == null ? null : key.toString());
    }

    public String put(String key, String value) {
        return null;
    }

    public String remove(Object key) {
        return null;
    }

    public void putAll(Map<? extends String, ? extends String> m) {
    }

    public void clear() {
    }

    public Set<String> keySet() {
        return resource.keySet();
    }

    public Collection<String> values() {
        return null;
    }

    public Set<java.util.Map.Entry<String, String>> entrySet() {
        return null;
    }

    
    
}
