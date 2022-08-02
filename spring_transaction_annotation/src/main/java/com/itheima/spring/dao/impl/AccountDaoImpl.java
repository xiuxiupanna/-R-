package com.itheima.spring.dao.impl;

import com.itheima.spring.dao.IAccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository("accountDao")
public class AccountDaoImpl extends JdbcDaoSupport implements IAccountDao {

//    @Autowired
//    private DataSource dataSource;

    @Autowired
    public void setSuperDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);

    }



    @Override
    public void out(String outName, Double money) {
        getJdbcTemplate().update("update t_account set money = money - ? where name = ?", money, outName);

    }

    @Override
    public void in(String inName, Double money) {
        getJdbcTemplate().update("update t_account set money = money + ? where = ?", money, inName);


    }
}
