package ro.iss.service;

import ro.iss.domain.Drug;
import ro.iss.domain.Order;
import ro.iss.domain.OrderItem;
import ro.iss.repository.OrderItemRepository;

import java.util.List;

public class OrderItemService {
    private final OrderItemRepository orderItemRepository = new OrderItemRepository();

    public void addOrderItem(Order order, Drug drug, int quantity) {
        OrderItem item = new OrderItem();
        item.setOrderid(order.getId());
        item.setDrugid(drug.getId());
        item.setQuantity(quantity);
        item.setDrugName(drug.getName());
        item.setOrder(order);
        item.setDrug(drug);

        orderItemRepository.save(item);
    }

    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }
}
