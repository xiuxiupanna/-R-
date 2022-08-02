package com.itheima.spring.service.impl;

import com.itheima.spring.dao.IAccountDao;
import com.itheima.spring.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("accountService")
@Transactional(readOnly = false)
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private IAccountDao accountDao;


    @Override
    @Transactional
    public void transfer(String outName, String inName, Double money) {

        //线转出
        accountDao.out(outName, money);

//        int i =1/0;


        //再转入
        accountDao.in(inName, money);
    }

    @Transactional(readOnly = true) //局部覆盖全局
    public void find() {

    }

}
