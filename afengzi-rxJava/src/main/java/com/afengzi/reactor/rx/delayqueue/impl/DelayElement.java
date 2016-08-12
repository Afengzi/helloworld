package com.afengzi.reactor.rx.delayqueue.impl;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by lixiuhai on 2016/8/12.
 */
public class DelayElement implements Delayed {

    private final long trigger ;
    private final String value ;

    public DelayElement(long delaySecTime,String value) {
        this.trigger = System.currentTimeMillis()+ TimeUnit.SECONDS.toMillis(delaySecTime);
        this.value = value;
    }

    public long getDelay(TimeUnit unit) {
        System.out.println(Thread.currentThread().getName() + " --> trigger : "+trigger);
        return unit.convert(trigger-System.currentTimeMillis(),TimeUnit.MILLISECONDS);
    }

    public int compareTo(Delayed o) {
        if (o == null) {
            throw new IllegalArgumentException("parameter must not null !");
        }
        if (!(o instanceof DelayElement)) {
            throw new ClassCastException("parameter is not class DelayedTask !");
        }
        DelayElement that = (DelayElement) o;
        if (this.trigger == that.trigger) {
            return 0;
        }
        return this.trigger > that.trigger ? 1 : -1;
    }

    public String getValue() {
        return value;
    }
}
