package ro.iss.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order extends Identifiable<Integer> {

    @Column(name = "quantity")
    private int quantity;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "id", insertable = true, updatable = true, nullable = false)
    private User user;
    
    @Column(name = "userid", insertable = false, updatable = false)
    private Integer userId;

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    // For backward compatibility with code using string status
    public void setStatus(String statusStr) {
        try {
            this.status = OrderStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Default to PENDING if invalid status string
            this.status = OrderStatus.PENDING;
        }
    }

    public User getUser() { return user; }
    public void setUser(User user) { 
        this.user = user;
        // If user is not null, also set userId for consistency
        if (user != null) {
            this.userId = user.getId();
        }
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}