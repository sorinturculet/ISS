package ro.iss.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ro.iss.domain.Order;
import ro.iss.domain.OrderItem;
import ro.iss.domain.User;
import ro.iss.service.OrderItemService;
import ro.iss.service.OrderService;

import java.util.Comparator;

public class PharmacyViewController {

    // UI components
    @FXML private ListView<String> pendingOrdersListView;
    @FXML private ListView<String> completedOrdersListView;
    @FXML private Button honorButton;
    @FXML private Button refreshButton;
    @FXML private TextArea orderDetailsArea;
    @FXML private Button logoutButton;

    // Services
    private final OrderService orderService = new OrderService();
    private final OrderItemService orderItemService = new OrderItemService();

    // Observable lists for UI binding
    private final ObservableList<String> pendingOrdersDisplayList = FXCollections.observableArrayList();
    private final ObservableList<String> completedOrdersDisplayList = FXCollections.observableArrayList();
    
    // Keep track of loaded orders to map between UI display and domain objects
    private Order selectedPendingOrder;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        orderDetailsArea.appendText("Logged in as: " + currentUser.getUsername() + " (Pharmacy)\n");
        loadOrders();
    }

    @FXML
    public void initialize() {
        // Initialize lists and bind to UI
        pendingOrdersListView.setItems(pendingOrdersDisplayList);
        completedOrdersListView.setItems(completedOrdersDisplayList);
        
        // Set up event handlers
        pendingOrdersListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> showPendingOrderDetails(newVal));
        
        completedOrdersListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> showCompletedOrderDetails(newVal));
        
        honorButton.setOnAction(e -> honorSelectedOrder());
        refreshButton.setOnAction(this::handleRefreshOrders);
    }

    @FXML
    public void handleRefreshOrders(ActionEvent event) {
        loadOrders();
        orderDetailsArea.appendText("Orders refreshed.\n");
    }

    private void loadOrders() {
        pendingOrdersDisplayList.clear();
        completedOrdersDisplayList.clear();
        
        // Load pending orders, sorted by ID (proxy for timestamp) to prioritize by arrival time
        orderService.getAllOrders().stream()
                .filter(o -> "PENDING".equals(o.getStatus()))
                .sorted(Comparator.comparing(Order::getId)) // Sort by ID (chronological)
                .forEach(o -> {
                    String userInfo = "";
                    if (o.getUser() != null) {
                        userInfo = " (User #" + o.getUser().getId() + ")";
                    } else if (o.getUserId() != null) {
                        userInfo = " (User ID: " + o.getUserId() + ")";
                    } else {
                        userInfo = " (Unknown User)";
                    }
                    pendingOrdersDisplayList.add("Order #" + o.getId() + userInfo);
                });
        
        // Load completed orders
        orderService.getAllOrders().stream()
                .filter(o -> "HONORED".equals(o.getStatus()))
                .sorted(Comparator.comparing(Order::getId).reversed()) // Newest first
                .forEach(o -> {
                    String userInfo = "";
                    if (o.getUser() != null) {
                        userInfo = " (User #" + o.getUser().getId() + ")";
                    } else if (o.getUserId() != null) {
                        userInfo = " (User ID: " + o.getUserId() + ")";
                    } else {
                        userInfo = " (Unknown User)";
                    }
                    completedOrdersDisplayList.add("Order #" + o.getId() + userInfo);
                });
    }
    
    private void showPendingOrderDetails(String selected) {
        if (selected == null) {
            selectedPendingOrder = null;
            return;
        }
        
        try {
            int orderId = Integer.parseInt(selected.split("#")[1].split(" ")[0]);
            selectedPendingOrder = orderService.getOrderById(orderId);
            
            displayOrderDetails(selectedPendingOrder, "PENDING");
        } catch (Exception e) {
            orderDetailsArea.setText("Error loading order details: " + e.getMessage());
        }
    }
    
    private void showCompletedOrderDetails(String selected) {
        if (selected == null) return;
        
        try {
            int orderId = Integer.parseInt(selected.split("#")[1].split(" ")[0]);
            Order order = orderService.getOrderById(orderId);
            
            // Clear any pending order selection
            pendingOrdersListView.getSelectionModel().clearSelection();
            selectedPendingOrder = null;
            
            displayOrderDetails(order, "HONORED");
        } catch (Exception e) {
            orderDetailsArea.setText("Error loading order details: " + e.getMessage());
        }
    }
    
    private void displayOrderDetails(Order order, String status) {
        if (order == null) return;
        
        orderDetailsArea.clear();
        orderDetailsArea.appendText("Order #" + order.getId() + " (" + status + ")\n");
        
        if (order.getUser() != null) {
            orderDetailsArea.appendText("From: " + order.getUser().getUsername() + "\n");
        }
        
        orderDetailsArea.appendText("\nItems:\n");
        orderItemService.getAllOrderItems().stream()
                .filter(oi -> oi.getOrderid() == order.getId())
                .forEach(oi -> orderDetailsArea.appendText("- " + oi.getDrugName() + " x" + oi.getQuantity() + "\n"));
    }

    @FXML
    public void honorSelectedOrder() {
        if (selectedPendingOrder == null) {
            orderDetailsArea.appendText("No order selected to honor.\n");
            return;
        }
        
        try {
            orderService.honorOrder(selectedPendingOrder);
            orderDetailsArea.appendText("Order #" + selectedPendingOrder.getId() + " has been honored.\n");
            
            // Refresh the lists
            loadOrders();
            
            // Clear selection
            selectedPendingOrder = null;
        } catch (Exception e) {
            orderDetailsArea.appendText("Error honoring order: " + e.getMessage() + "\n");
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        Stage stage = (Stage) honorButton.getScene().getWindow();
        stage.close();
    }
}
