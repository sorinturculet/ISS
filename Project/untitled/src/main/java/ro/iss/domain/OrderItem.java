package ro.iss.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "orderitems")
public class OrderItem extends Identifiable<Integer> {

    private int drugid;
    private int orderid;
    private int quantity;
    private String drugName;

    @ManyToOne
    @JoinColumn(name = "drugid", insertable = false, updatable = false)
    private Drug drug;

    @ManyToOne
    @JoinColumn(name = "orderid", insertable = false, updatable = false)
    private Order order;

    // Getters & Setters
    public int getDrugid() { return drugid; }
    public void setDrugid(int drugid) { this.drugid = drugid; }

    public int getOrderid() { return orderid; }
    public void setOrderid(int orderid) { this.orderid = orderid; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getDrugName() { return drugName; }
    public void setDrugName(String drugName) { this.drugName = drugName; }

    public Drug getDrug() { return drug; }
    public void setDrug(Drug drug) { this.drug = drug; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
}