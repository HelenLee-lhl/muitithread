package com.helen.muitithread;

/**
 * Created by helenlee on 2017/9/16.
 */
public class JackAndMayTwo implements Runnable{
    private BankAccount bankAccount = new BankAccount();
    public static void main(String [] args){
        JackAndMayTwo jackAndMayJob = new JackAndMayTwo();
        Thread jack = new Thread(jackAndMayJob);
        Thread may = new Thread(jackAndMayJob);
        jack.setName("Jack");
        may.setName("May");
        jack.start();
        may.start();
    }
    @Override
    public void run() {
        for (int i = 0; i < 10; i++){
            makeWithdrawal(10);
            if (bankAccount.getBalance() < 0) {
                System.out.println("Overdown");
            }
        }
    }

    private void makeWithdrawal(int i) {
        if (bankAccount.getBalance() > i) {
            System.out.println(Thread.currentThread().getName() + "is about withdraw");
            try {
                System.out.println(Thread.currentThread().getName() + "is going to sleep");
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "is wake up");
            bankAccount.withdraw(i);
            System.out.println(Thread.currentThread().getName() + "completes the withdrawal");
        } else {
            System.out.println("sorry is not enough amount for " + Thread.currentThread().getName());
        }
    }
}
