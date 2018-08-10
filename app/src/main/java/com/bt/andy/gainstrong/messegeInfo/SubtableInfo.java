package com.bt.andy.gainstrong.messegeInfo;

/**
 * @创建者 AndyYan
 * @创建时间 2018/5/25 16:08
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class SubtableInfo {
    /**
     * goodsName : 熊猫TV
     * goodsid : 123456
     * xs_unit_price : 2000.00
     * number : 10
     * zh_unit_price : 1900.00
     * sum_pric : 20000.00
     * type : 自提
     * remark : 备注
     * address : 海门市
     */

    private String goodsName;
    private String goodsid;
    private String unitid;//单位id
    private double xs_unit_price;//销售单价
    private int    number;        //下单台数
    private double zh_unit_price;//折后单价
    private double sum_pric;    //合计费用
    private String remark;      //备注

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(String goodsid) {
        this.goodsid = goodsid;
    }

    public String getUnitid() {
        return unitid;
    }

    public void setUnitid(String unitid) {
        this.unitid = unitid;
    }

    public double getXs_unit_price() {
        return xs_unit_price;
    }

    public void setXs_unit_price(double xs_unit_price) {
        this.xs_unit_price = xs_unit_price;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public double getZh_unit_price() {
        return zh_unit_price;
    }

    public void setZh_unit_price(double zh_unit_price) {
        this.zh_unit_price = zh_unit_price;
    }

    public double getSum_pric() {
        return sum_pric;
    }

    public void setSum_pric(double sum_pric) {
        this.sum_pric = sum_pric;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
