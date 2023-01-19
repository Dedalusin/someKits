package org.dedalusin.reloadcache.collection;

/**
 * @author chenchen30 <chenchen30@kuaishou.com>
 * Created on 2023-01-19
 */
public interface ReloadCache<T> {

    void reload();

    T get();

}
