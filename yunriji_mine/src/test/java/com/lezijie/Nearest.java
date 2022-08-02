package com.lezijie;

public class Nearest {

    public static void main(String [] args) {
        A a = new A();
        B b = new B();
        a.fun();
        b.fun();


    }

}

class A {
    private void print() {
        System.out.println("A");

    }

    public void fun() {
        this.print();
    }


}

class B extends  A{
    public void print() {

        System.out.println("B");
    }


}


