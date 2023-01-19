package org.dedalusin.reloadcache;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.dedalusin.reloadcache.collection.impl.ZkNotifyReloadCache;
import org.dedalusin.reloadcache.utils.ZkReloadCacheUtils;
import org.dedalusin.reloadcache.utils.ZkUtils;
import org.junit.Test;

/**
 * @author chenchen30 <chenchen30@kuaishou.com>
 * Created on 2023-01-19
 */
public class ZkTest {
    @Test
    public void zkReloadTest() throws Exception {
        ZkNotifyReloadCache<Object> reloadCache = ZkNotifyReloadCache.newBuilder()
                .setCacheGetter(() -> System.currentTimeMillis())
                .setClient(ZkUtils.getClient())
                .setPath("/test/zkReload")
                .build();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        // 固定频率查询
        executor.scheduleAtFixedRate(() -> System.out.println(reloadCache.get()), 1000L, 3000L, TimeUnit.MILLISECONDS);
        Thread.sleep(2000L);
        ZkReloadCacheUtils.reload("/test/zkReload", ZkUtils.getClient());
        Thread.sleep(10000L);
    }
}
