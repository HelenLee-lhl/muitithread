# muitithread
## 多线程问题样例
### 示例1 多线程之更新丢失问题
```Java
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
            System.out.println("balance is " + balance + " now");
        }
    }

    /**
     * 对账户进行加 1 操作
     */
    private void increament() {
        int i = balance;
        //由于上下两步操作非原子性，两个线程可能同时拿到了相同的balance
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
```
#### 示例背景：
  同时启动两个线程A B 对balance共享数据进行增量➕1操作50次，预期的执行完成之后balance应该为100，然而多次运行结果却并非我们预期<br>
    balance is 1 now<br>
    balance is 2 now<br>
    balance is 3 now<br>
    balance is 4 now<br>
    balance is 5 now<br>
    balance is 6 now<br>
    balance is 4 now//此处覆盖了之前的更新数据，之前的更新丢失<br>
    balance is 5 now<br>
    balance is 6 now<br>
#### 产生问题原因：
  A 线程获取的 i = balance = 3，然后 A 线程挂起，B 线程 继续执行3 次累加操作，balance=6；A 线程醒来后继续执行 balance = (i + 1)即：balance = 3 + 1 = 4；<br>
#### 解决方案：
  同步化increament()方法使其操作具备原子性的特征即increament()中的两步操作同一时间只能由一个线程操作。我们可以在方法前添加同步化标记 synchronized 
### 实例 2 同步化影响的范围
```Java
package com.helen.muitithread;

/**
 * Created by helenlee on 2017/9/17.
 */
public class SynchronizedDemo {
    private static int balance = 100;

    public void updataA(){
        int i = balance;
        balance = i + 1;
    }
    public synchronized void updataB(){
        int i = balance;
        balance = i + 1;
    }
    public synchronized void updataC(){
        int i = balance;
        balance = i + 1;
    }
    public static synchronized void updataD(){
        int i = balance;
        balance = i + 1;
    }
    public static synchronized void updataE(){
        int i = balance;
        balance = i + 1;
    }
}
```
`说明：假设SynchronizedDemo 为一层楼，其中的每一个方法对应每一个房间，如果方法标记为synchronized则对应的房间是锁上的，其他的房间默认没有上锁；相同作用域的同步方法共享唯一的一把钥匙（即每一个作用域只有一把钥匙而且可以打开此作用域下所有的房间）`
* updataA()方法没有同步化，线程 a b 可以同时进入<br>
* 非静态的同步方法默认作用域为this即当前对象，也就是说你要对非静态的同步方法操作必须获得当前对象🔒的🔑；<br>
  1）updataB()和updataC()如果线程 a 拿到对象锁的钥匙开始操作updataC()方法，那么线程 b 此时不能操作 updataB()和updataC() 方法，因为对象锁唯一的钥匙在 线程 a 手上。<br>
* 静态的同步话方法作用域是CLASS范围,也就是说你要对静态的同步方法操作必须获得当前CLASS🔒的🔑；
  1）updataD()和updataE()效果同updataB()和updataC()<br>
  2）那么updataC()和updataD()同步互斥吗？当然不是，两个方法同步的作用域不同，即两个线程可以拿到不同作用域的钥匙进行操纵。<br>
  
### 示例 3 死锁问题
```Java
package com.helen.muitithread;

import java.util.Date;

/**
 * Created by helenlee on 2017/9/17.
 */
public class DeadLockDemo implements Runnable{
    private ObjectA objectA = new ObjectA();
    private ObjectB objectB = new ObjectB();

    @Override
    public void run() {
        for (int i = 0 ; i < 50 ; i ++){
            if ("a".equals(Thread.currentThread().getName())){
                objectA.methodA();
            }else {
                objectB.methodB();
            }
        }
    }
    class ObjectA{
        public synchronized void methodA(){
            try {
                Thread.sleep(1000);
                System.out.println("线程" + Thread.currentThread().getName() + "进入methodA()方法,马上开始执行methodB()方法" + new Date());
                objectB.methodB();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    class ObjectB{
        public synchronized void methodB(){
            try {
                System.out.println("线程" + Thread.currentThread().getName() + "进入methodB()方法,马上开始执行methodA()方法" + new Date());
                objectA.methodA();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String [] agrs){
        DeadLockDemo deadLockDemo = new DeadLockDemo();
        Thread a = new Thread(deadLockDemo);
        Thread b = new Thread(deadLockDemo);
        a.setName("a");
        b.setName("b");
        a.start();
        b.start();
    }
}
```
打印结果：
线程b进入methodB()方法,马上开始执行methodA()方法Sun Sep 17 17:09:29 CST 2017
线程a进入methodA()方法,马上开始执行methodB()方法Sun Sep 17 17:09:30 CST 2017
无尽的等待中..........
　`产生原因：线程 a 拿到objectA对象锁钥匙把持住不放并尝试对ObjectB进行调用发现ObjectB其他线程锁住于是进入等待状态，而线程 b拿到了ObjectB对象锁钥匙，并尝试对objectA进行调用发现objectA其他线程锁住于是进入等待状态。线程a／b 各自把持对象锁不放，于是死锁产生`
  
