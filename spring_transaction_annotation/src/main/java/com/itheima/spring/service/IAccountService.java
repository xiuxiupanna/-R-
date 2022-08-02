package com.itheima.spring.service;

import com.itheima.spring.dao.IAccountDao;

public interface IAccountService {

    public void transfer(String outName, String inName, Double money);

}
