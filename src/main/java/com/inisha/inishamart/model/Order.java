package com.inisha.inishamart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private long id;
    private long buyerId;
    private String status; // PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private List<OrderItem> items;

    public Order() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getBuyerId() { return buyerId; }
    public void setBuyerId(long buyerId) { this.buyerId = buyerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
