package ro.iss.service;

import ro.iss.domain.Order;
import ro.iss.domain.OrderStatus;
import ro.iss.domain.User;
import ro.iss.repository.OrderRepository;

import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository = new OrderRepository();

    public void placeOrder(int quantity, User user) {
        Order order = new Order();
        order.setQuantity(quantity);
        order.setStatus(OrderStatus.PENDING);
        order.setUser(user);

        orderRepository.save(order);
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public void honorOrder(Order order) {
        order.setStatus(OrderStatus.HONORED);
        orderRepository.update(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(int id) {
        return orderRepository.findById(id);
    }
}
