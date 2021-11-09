
/*
 * Copyright 2018-present KunMinX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibroadlink.library.base.callback.livedata.unpeek;

import android.text.TextUtils;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class ProtectedUnPeekLiveData<T> extends LiveData<T> {

    private final static int START_VERSION = -1;

    private final AtomicInteger mCurrentVersion = new AtomicInteger(START_VERSION);

    protected boolean isAllowNullValue;

    /**
     * <p>
     * state 是可变且私用的，event 是只读且公用的，
     * state 的倒灌是应景的，event 倒灌是不符预期的，
     * <p>
     * 如果这样说还不理解，详见《LiveData 唯一可信源 读写分离设计》的解析：
     * https://xiaozhuanlan.com/topic/2049857631
     *
     * @param owner    activity 传入 this，fragment 建议传入 getViewLifecycleOwner
     * @param observer observer
     */
    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        super.observe(owner, createObserverWrapper(observer, mCurrentVersion.get()));
    }

    /**
     * @param observer observer
     */
    @Override
    public void observeForever(@NonNull Observer<? super T> observer) {
        super.observeForever(createObserverForeverWrapper(observer, mCurrentVersion.get()));
    }

    /**
     * @param owner    activity 传入 this，fragment 建议传入 getViewLifecycleOwner
     * @param observer observer
     */
    public void observeSticky(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
        super.observe(owner, createObserverWrapper(observer, START_VERSION));
    }

    /**
     * @param observer observer
     */
    public void observeStickyForever(@NonNull Observer<? super T> observer) {
        super.observeForever(createObserverForeverWrapper(observer, START_VERSION));
    }

    /**
     * postValue 最终还是会经过这里
     *
     * @param value value
     */
    @Override
    protected void setValue(T value) {
        mCurrentVersion.getAndIncrement();
        super.setValue(value);
    }

    /**
     * 1.添加一个包装类，自己维护一个版本号判断，用于无需 map 的帮助也能逐一判断消费情况
     * 2.重写 equals 方法和 hashCode，在用于手动 removeObserver 时，忽略版本号的变化引起的变化
     */
    class ObserverWrapper implements Observer<T> {
        private final Observer<? super T> mObserver;
        private int mVersion = START_VERSION;
        private boolean mIsForever;

        public ObserverWrapper(@NonNull Observer<? super T> observer, int version, boolean isForever) {
            this(observer, version);
            this.mIsForever = isForever;
        }

        public ObserverWrapper(@NonNull Observer<? super T> observer, int version) {
            this.mObserver = observer;
            this.mVersion = version;
        }

        @Override
        public void onChanged(T t) {
            if (mCurrentVersion.get() > mVersion && (t != null || isAllowNullValue)) {
                mObserver.onChanged(t);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ObserverWrapper that = (ObserverWrapper) o;
            return Objects.equals(mObserver, that.mObserver);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mObserver);
        }

        @NonNull
        @Override
        public String toString() {
            return mIsForever ? "IS_FOREVER" : "";
        }
    }

    @Override
    public void removeObserver(@NonNull Observer<? super T> observer) {
        if (TextUtils.isEmpty(observer.toString())) {
            super.removeObserver(observer);
        } else {
            super.removeObserver(createObserverWrapper(observer, START_VERSION));
        }
    }

    private ObserverWrapper createObserverForeverWrapper(@NonNull Observer<? super T> observer, int version) {
        return new ObserverWrapper(observer, version, true);
    }

    private ObserverWrapper createObserverWrapper(@NonNull Observer<? super T> observer, int version) {
        return new ObserverWrapper(observer, version);
    }

    /**
     * 手动将消息从内存中清空，
     * 以免无用消息随着 SharedViewModel 的长时间驻留而导致内存溢出的发生。
     */
    public void clear() {
        super.setValue(null);
    }
}

