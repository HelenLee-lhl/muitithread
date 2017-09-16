package com.helen.muitithread;

/**
 * Created by helenlee on 2017/9/16.
 */
public class JackAndMayOne implements Runnable{
    private BankAccount bankAccount = new BankAccount();
    public static void main(String [] args){
        JackAndMayOne jackAndMayOne = new JackAndMayOne();
        Thread jack = new Thread(jackAndMayOne);
        Thread may = new Thread(jackAndMayOne);
        jack.setName("Jack");
        may.setName("may");
        jack.start();
        may.start();
    }
    @Override
    public void run() {
        makewithdrawal(99);
    }

    private void makewithdrawal(int account) {
        if (bankAccount.getBalance() > account){
            System.out.println(Thread.currentThread().getName() + "is coming and sleep now!");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "wake up and withdraw");
            if (bankAccount.getBalance() > account){
                bankAccount.withdraw(account);
                System.out.println(Thread.currentThread().getName() + "  withdraw");
            }else {
                System.out.println("sorry is not enough for " + Thread.currentThread().getName());
            }
        }
    }
}
