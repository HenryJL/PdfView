package com.render.pdfcore.engine.cache;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;

import com.render.pdfcore.engine.IPagePool;
import com.render.pdfcore.engine.page.Page;


public class LRUPageCachePool extends LruCache<CacheKey, Page> implements IPagePool {
    private long maxSize;

    public LRUPageCachePool(int maxSize) {
        super(maxSize);
        this.maxSize = maxSize;
    }

    @Override
    protected void entryRemoved(boolean evicted, @NonNull CacheKey key,
                                @NonNull Page oldValue, @Nullable Page newValue) {
        Log.d("PDFView", "entryRemoved() called with: evicted = [" + evicted + "], key = [" + key + "], oldValue = [" + oldValue + "], newValue = [" + newValue + "]");
        //oldValue.recycle();
        remove(key);
        key.recycle();
    }

    @Override
    public synchronized long getMaxSize() {
        return maxSize;
    }

    @Override
    public synchronized void put(int idx, Page page) {
        if (page == null) {
            throw new NullPointerException("bitmap must not be null.");
        }
        if (page.isRecycled()) {
            throw new IllegalStateException("Cannot pool recycled bitmap");
        }
        put(CacheKey.makeKey(idx), page);
    }

    @Override
    public synchronized Page get(int idx) {
        CacheKey cacheKey = CacheKey.makeKey(idx);
        return get(cacheKey);
    }

    @Override
    public synchronized void clearMemory() {
        if (size() == 0) {
            return;
        }
        trimToSize(0);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            clearMemory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.finalize();
    }
}
