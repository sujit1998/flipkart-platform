package com.flipkart.order.model;

public class Order {

    private long orderId;
    private String orderName;

    Order(long orderId, String orderName){
        this.orderId = orderId;
        this.orderName = orderName;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

}
