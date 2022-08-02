package com.itheima.spring.dao;

public interface IAccountDao {

    public void out(String outName, Double money);

    public void in(String inName, Double money);

}
