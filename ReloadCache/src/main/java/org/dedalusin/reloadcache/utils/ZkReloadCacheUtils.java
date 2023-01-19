package org.dedalusin.reloadcache.utils;

import org.apache.curator.framework.CuratorFramework;

/**
 * @author chenchen30 <chenchen30@kuaishou.com>
 * Created on 2023-01-19
 */
public class ZkReloadCacheUtils {
    public static void reload(String path, CuratorFramework client) throws Exception {
        // 通知zk
        ZkUtils.setData(client, path, (System.currentTimeMillis() + "").getBytes());
    }
}
