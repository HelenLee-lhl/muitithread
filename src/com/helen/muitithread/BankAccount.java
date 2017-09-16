package com.helen.muitithread;

/**
 * Created by helenlee on 2017/9/16.
 */
public class BankAccount {
    private Integer balance = 100;
    public Integer getBalance(){
        return this.balance;
    }
    public void withdraw(Integer amount){
        this.balance -= amount;
    }

}
