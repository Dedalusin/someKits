package org.dedalusin.reloadcache.collection.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.dedalusin.reloadcache.collection.ReloadCache;
import org.dedalusin.reloadcache.utils.ZkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author chenchen30 <chenchen30@kuaishou.com>
 * Created on 2023-01-19
 */
public class ZkNotifyReloadCache<T> implements ReloadCache<T> {
    private static final Logger logger = LoggerFactory.getLogger(ZkNotifyReloadCache.class);
    private Supplier<T> cacheGetter;
    private String path;
    private volatile T cache;
    private final NodeCache nodeCache;

    public ZkNotifyReloadCache(ZkNotifyReloadCacheBuilder<T> builder) throws Exception {
        this.setCacheGetter(builder.cacheGetter);
        this.setPath(builder.path);
        builder.client.start();
        if (ZkUtils.checkNodeExist(builder.client, builder.path)) {
            ZkUtils.create(builder.client, builder.path, "0".getBytes());
        }
        this.nodeCache = new NodeCache(builder.client, path);
        this.nodeCache.start(true);
        this.nodeCache.getListenable().addListener(this::reload);
    }

    public Supplier<T> getCacheGetter() {
        return cacheGetter;
    }

    public void setCacheGetter(Supplier<T> cacheGetter) {
        this.cacheGetter = cacheGetter;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void reload() {
        synchronized (ZkNotifyReloadCache.class) {
            this.cache = cacheGetter.get();
        }
    }

    /**
     * 单例+懒加载
     */
    @Override
    public T get() {
        if (cache == null) {
            synchronized (ZkNotifyReloadCache.class) {
                if (cache == null) {
                    cache = cacheGetter.get();
                }
            }
        }
        return cache;
    }

    public static <T> ZkNotifyReloadCacheBuilder<T> newBuilder() {
        return new ZkNotifyReloadCacheBuilder<>();
    }

    public static class ZkNotifyReloadCacheBuilder<T> {
        private Supplier<T> cacheGetter;
        private String path;
        private CuratorFramework client;

        public CuratorFramework getClient() {
            return client;
        }

        public ZkNotifyReloadCacheBuilder<T> setClient(CuratorFramework client) {
            this.client = client;
            return this;
        }

        public String getPath() {
            return path;
        }

        public ZkNotifyReloadCacheBuilder<T> setPath(String path) {

            this.path = path;
            return this;
        }

        public Supplier<T> getCacheGetter() {
            return cacheGetter;
        }

        public ZkNotifyReloadCacheBuilder<T> setCacheGetter(Supplier<T> cacheGetter) {
            checkNotNull(cacheGetter);
            this.cacheGetter = cacheGetter;
            return this;
        }

        public ZkNotifyReloadCache<T> build() throws Exception {
            checkParams();
            return new ZkNotifyReloadCache<T>(this);
        }

        private void checkParams() {
            if (StringUtils.isBlank(path)) {
                throw new IllegalArgumentException("zk path shouldn't be empty or null");
            }
            if (cacheGetter == null) {
                throw new IllegalArgumentException("cacheGetter shouldn't be null");
            }
        }
    }
}
