package com.lezijie.entity;

import java.io.Serializable;
import java.util.Objects;

public class Dept implements Serializable {
    private int deptNo;
    private String dname;
    private String loc;

    public int getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(int deptNo) {
        this.deptNo = deptNo;
    }

    public String getName() {
        return dname;
    }

    public void setName(String name) {
        this.dname = name;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public Dept() {
    }

    public Dept(int deptNo, String name, String loc) {
        this.deptNo = deptNo;
        this.dname = name;
        this.loc = loc;
    }

    @Override
    public String toString() {
        return "Dept{" +
                "deptNo=" + deptNo +
                ", name='" + dname + '\'' +
                ", loc='" + loc + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dept dept = (Dept) o;
        return deptNo == dept.deptNo && Objects.equals(dname, dept.dname) && Objects.equals(loc, dept.loc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deptNo, dname, loc);
    }
}
