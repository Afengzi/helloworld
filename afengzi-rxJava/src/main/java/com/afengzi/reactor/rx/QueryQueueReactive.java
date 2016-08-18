package com.afengzi.reactor.rx;

import com.afengzi.delayqueue.DelayQueueWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.concurrent.Executors;

/**
 * Created by winged fish on 2016/8/11.
 */
public abstract class QueryQueueReactive {
    private final static Logger log = Logger.getLogger(QueryQueueReactive.class);

    public void queryPayResult(DelayQueueWrapper current, DelayQueueWrapper next) {
        Scheduler observableS = Schedulers.from(Executors.newFixedThreadPool(2));
        Scheduler subscribeS = Schedulers.from(Executors.newCachedThreadPool());
        if (isSwitchQuery()) {
            while (current.getQueueSize() > 0) {
                String value = current.pollQueueValue();
                System.out.println(Thread.currentThread().getName() + " -->currentSize " + current.getQueueSize());
                Observable<String> result = Observable.just(value);

                //���˵�orderIdΪ�յ�����
                System.out.println(Thread.currentThread().getName() + " --> before filter1 value : " + value);
                result=result.filter((orderId) -> StringUtils.isNotBlank(orderId));

                printLog("before observableOn ");
                result = result.observeOn(observableS);
                printLog("after observableOn");

                //���˵�����Ҫ��ѯ��orderId,�磺��ʱ����ɶ��˲�����
                System.out.println(Thread.currentThread().getName() + " --> before filter2 value : " + value);
                result=result.filter((orderId) -> isNeedQuery(orderId));

                printLog("before observableOn ");
                result = result.observeOn(observableS);
                printLog("after observableOn");

                //���˵�����ɹ���orderId
                System.out.println(Thread.currentThread().getName() + " --> before filter3 value : " + value);
                result=result.filter((orderId) -> doQueryAndExecute(orderId));
                if (next != null) {
                    //�Ѷ����ŷ��뵽��һ������
                    System.out.println(Thread.currentThread().getName() + " --> before subscribe1 value : " + value);
                    result.subscribe((orderId) -> next.addQueue(orderId));
                    System.out.println(Thread.currentThread().getName() + " -->current = [" + current.getQueueSize() + "], next = [" + next.getQueueSize() + "]");
                }

            }

        }
    }

    private void printLog(String s){
        System.out.println(Thread.currentThread().getName() + " --> s: "+s);
    }


    public abstract boolean isSwitchQuery();

    public abstract boolean isNeedQuery(String orderId);

    public abstract boolean doQueryAndExecute(String orderId);

}
