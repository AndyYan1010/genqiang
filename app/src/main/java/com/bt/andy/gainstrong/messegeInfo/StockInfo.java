package com.bt.andy.gainstrong.messegeInfo;

/**
 * @创建者 AndyYan
 * @创建时间 2018/7/27 13:33
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class StockInfo {

    /**
     * fname : 冰箱
     * fqty : 99.0
     * address : 黄古山仓
     */

    private String fname;
    private String fqty;
    private String address;

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFqty() {
        return fqty;
    }

    public void setFqty(String fqty) {
        this.fqty = fqty;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
