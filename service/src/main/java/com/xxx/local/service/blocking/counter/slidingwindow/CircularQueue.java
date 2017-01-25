package com.xxx.local.service.blocking.counter.slidingwindow;

import com.google.common.base.Preconditions;

/**
 * 环形队列(非通用)
 *
 * @author xiaobaoqiu  Date: 17-1-24 Time: 下午4:10
 */
class CircularQueue {

    /**
     * 元素
     */
    private long[] data;

    /**
     * 队头(先入元素)
     */
    private int head = 0;

    /**
     * 队尾(后入元素),代表下一个插入的位置
     */
    private int tail = 0;

    @SuppressWarnings("unchecked")
    public CircularQueue(int capacity) {
        Preconditions.checkArgument(capacity > 0);

        data = new long[capacity + 1];  //一个空闲元素便于判断
        head = 0;
        tail = 0;
    }

    public boolean isFull() {
        return (head != 0 && head - tail == 1) ||
                (head == 0 && tail == data.length - 1);
    }

    /**
     * 插入队尾一个元素
     *
     * @return 表示插入是否成功, 比如队列已满则
     */
    public boolean enqueue(long item, LifeCyclePredicate predicate) {
        if (isFull()) {
            if (!predicate.apply(head())) {
                System.out.println("队列已满,插入失败,item=" + item + "," + toString());
                return false;// 满了 且 队首元素未过期
            }

            // 队首元素过期,则直接丢弃队首元素
            System.out.println("丢弃过期队首元素,head=" + head + ",tail=" + tail + ", data[head]=" + data[head] + ",item=" + item);
            data[head] = 0;
            head = (head + 1) % data.length;
        }

        //没有满
        System.out.println("元素入队列,item=" + item + "," + toString());
        enqueue(item);
        return true;
    }

    private void enqueue(long item) {
        data[tail] = item;
        tail = (tail + 1) % data.length;
    }

    /**
     * 队首元素
     */
    public long head() {
        return data[head];
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(CircularQueue.class.getSimpleName());
        builder = builder.append("capacity=" + data.length)
                .append(",head=" + head)
                .append(",tail=" + tail)
                .append(",data=");
        for (long d : data) {
            builder = builder.append(d).append("-->");
        }
        return builder.toString();
    }

    /**
     * 测试代码
     */
    public static void main(String[] args) throws Exception {
        CircularQueue queue = new CircularQueue(5);
        LifeCyclePredicate predicate = new LifeCyclePredicate(1000L);

        queue.enqueue(1, predicate);
        queue.enqueue(100, predicate);
        queue.enqueue(500, predicate);


        System.out.println(queue.toString());
        Thread.sleep(1000);

        queue.enqueue(1001, predicate);
        queue.enqueue(1101, predicate);
        queue.enqueue(1201, predicate); //触发队列满
        System.out.println(queue.toString());
    }
}
