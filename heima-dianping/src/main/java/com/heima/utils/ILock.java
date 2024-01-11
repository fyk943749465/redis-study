package com.heima.utils;

public interface ILock {
    boolean tryLock(long timeoutSec);
    void unlock();
}
