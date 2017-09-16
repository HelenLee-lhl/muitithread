package com.helen.muitithread;

/**
 * Created by helenlee on 2017/9/16.
 */
public class UpdateLoseDemo implements Runnable {
    private int balance;

    /**
     * 循环50次对balance进行加 1 操作
     */
    @Override
    public void run() {
        for (int i = 0; i < 50; i++) {
            //对账户增量加1
            increament();
            System.out.println(Thread.currentThread().getName() + " add complete and balance is " + balance + " now");
        }
    }

    /**
     * 对账户进行加 1 操作
     */
    private synchronized void increament() {
        int i = balance;
        if (balance == 4) {
            try {
                Thread.sleep(1);
                balance = i + 1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        balance = i + 1;
    }

    /**
     * 新建两个线程 A B 对balance进行增量操作
     *
     * @param args
     */
    public static void main(String[] args) {
        UpdateLoseDemo updateLoseDemo = new UpdateLoseDemo();
        Thread threaA = new Thread(updateLoseDemo);
        Thread threaB = new Thread(updateLoseDemo);
        threaA.setName("A");
        threaB.setName("B");
        threaA.start();
        threaB.start();
    }
}
