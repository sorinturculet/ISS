package ro.iss.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ro.iss.domain.Drug;
import ro.iss.domain.Order;
import ro.iss.domain.OrderItem;
import ro.iss.domain.OrderStatus;
import ro.iss.domain.User;
import ro.iss.service.DrugService;
import ro.iss.service.OrderItemService;
import ro.iss.service.OrderService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HospitalStaffController {

    @FXML private ComboBox<String> drugComboBox;
    @FXML private TextField quantityField;
    @FXML private Button addItemButton;
    @FXML private TextArea orderStatusArea;
    @FXML private Button logoutButton;
    
    @FXML private ListView<String> orderDraftListView;
    @FXML private Button submitOrderButton;
    @FXML private Button refreshButton;
    
    @FXML private ListView<String> pendingOrdersListView;
    @FXML private ListView<String> completedOrdersListView;

    private final DrugService drugService = new DrugService();
    private final OrderService orderService = new OrderService();
    private final OrderItemService orderItemService = new OrderItemService();

    private User currentUser;
    private final List<OrderItemDraft> currentOrderItems = new ArrayList<>();
    private final ObservableList<String> orderDraftDisplayList = FXCollections.observableArrayList();
    private final ObservableList<String> pendingOrdersDisplayList = FXCollections.observableArrayList();
    private final ObservableList<String> completedOrdersDisplayList = FXCollections.observableArrayList();

    private static class OrderItemDraft {
        Drug drug;
        int quantity;

        OrderItemDraft(Drug drug, int quantity) {
            this.drug = drug;
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return drug.getName() + " x" + quantity;
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        orderStatusArea.appendText("Logged in as: " + currentUser.getUsername() + " (Section)\n");
        loadOrders();
    }

    @FXML
    public void initialize() {
        drugService.getAllDrugs().forEach(d -> drugComboBox.getItems().add(d.getName()));

        orderDraftListView.setItems(orderDraftDisplayList);
        pendingOrdersListView.setItems(pendingOrdersDisplayList);
        completedOrdersListView.setItems(completedOrdersDisplayList);

        addItemButton.setOnAction(this::handleAddItem);
        submitOrderButton.setOnAction(this::handlePlaceFinalOrder);
        refreshButton.setOnAction(this::handleRefreshOrders);
        
        pendingOrdersListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> showOrderDetails(newVal, "PENDING"));
        completedOrdersListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> showOrderDetails(newVal, "HONORED"));
    }

    @FXML
    public void handleRefreshOrders(ActionEvent event) {
        loadOrders();
        orderStatusArea.appendText("Orders refreshed.\n");
    }

    private void loadOrders() {
        if (currentUser == null) return;
        
        pendingOrdersDisplayList.clear();
        completedOrdersDisplayList.clear();
        
        orderService.getAllOrders().stream()
            .filter(o -> o.getUser() != null && o.getUser().getId().equals(currentUser.getId()))
            .forEach(o -> {
                String orderInfo = "Order #" + o.getId() + " - " + formatDate(o);
                
                if (o.getStatus() == OrderStatus.PENDING) {
                    pendingOrdersDisplayList.add(orderInfo);
                } else if (o.getStatus() == OrderStatus.HONORED) {
                    completedOrdersDisplayList.add(orderInfo);
                }
            });
    }
    
    private void showOrderDetails(String selectedOrder, String statusStr) {
        if (selectedOrder == null) return;
        
        try {
            int orderId = Integer.parseInt(selectedOrder.split("#")[1].split(" ")[0]);
            Order order = orderService.getOrderById(orderId);
            
            if (order != null) {
                orderStatusArea.clear();
                orderStatusArea.appendText("Order #" + orderId + " (" + order.getStatus() + "):\n");
                
                List<OrderItem> items = orderItemService.getAllOrderItems().stream()
                    .filter(oi -> oi.getOrderid() == orderId)
                    .toList();
                
                if (items.isEmpty()) {
                    orderStatusArea.appendText("No items found for this order.\n");
                } else {
                    for (OrderItem item : items) {
                        orderStatusArea.appendText("- " + item.getDrugName() + " x" + item.getQuantity() + "\n");
                    }
                }
            }
        } catch (Exception e) {
            orderStatusArea.appendText("Error showing order details: " + e.getMessage() + "\n");
        }
    }
    
    private String formatDate(Order order) {
        return "";
    }

    @FXML
    private void handleAddItem(ActionEvent event) {
        try {
            String selectedDrugName = drugComboBox.getValue();
            if (selectedDrugName == null || selectedDrugName.isEmpty()) {
                 orderStatusArea.appendText("Please select a drug.\n");
                 return;
            }
            
            int quantity = Integer.parseInt(quantityField.getText());
            if (quantity <= 0) {
                orderStatusArea.appendText("Quantity must be positive.\n");
                return;
            }

            Optional<Drug> drugOpt = drugService.getAllDrugs().stream()
                        .filter(d -> d.getName().equals(selectedDrugName))
                    .findFirst();

            if (drugOpt.isPresent()) {
                OrderItemDraft draftItem = new OrderItemDraft(drugOpt.get(), quantity);
                currentOrderItems.add(draftItem);
                orderDraftDisplayList.add(draftItem.toString());
                
                orderStatusArea.appendText("Added to draft: " + draftItem + "\n");
                quantityField.clear();
                drugComboBox.getSelectionModel().clearSelection();
            } else {
                 orderStatusArea.appendText("Selected drug not found.\n");
            }

        } catch (NumberFormatException ex) {
            orderStatusArea.appendText("Invalid quantity input.\n");
        } catch (Exception e) {
            orderStatusArea.appendText("Error adding item: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePlaceFinalOrder(ActionEvent event) {
        if (currentOrderItems.isEmpty()) {
            orderStatusArea.appendText("Order draft is empty. Add items first.\n");
            return;
        }
        
        if (currentUser == null) {
            orderStatusArea.appendText("Error: No logged-in user found.\n");
            return;
        }

        try {
            Order newOrder = new Order();
            int totalQuantity = currentOrderItems.stream().mapToInt(item -> item.quantity).sum();
            newOrder.setQuantity(totalQuantity);
            newOrder.setStatus(OrderStatus.PENDING);
            newOrder.setUser(currentUser);
            
            orderService.saveOrder(newOrder);

            if (newOrder.getId() == null) {
                 orderStatusArea.appendText("Error: Failed to save order header.\n");
                 return;
            }
            
            orderStatusArea.appendText("Created Order #" + newOrder.getId() + "\n");

            for (OrderItemDraft draft : currentOrderItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(newOrder);
                orderItem.setDrug(draft.drug);
                orderItem.setQuantity(draft.quantity);
                orderItem.setDrugName(draft.drug.getName());
                orderItem.setOrderid(newOrder.getId());
                orderItem.setDrugid(draft.drug.getId());

                orderItemService.addOrderItem(newOrder, draft.drug, draft.quantity);
                orderStatusArea.appendText("  - Added item: " + draft + "\n");
            }

            currentOrderItems.clear();
            orderDraftDisplayList.clear();
            orderStatusArea.appendText("Order #" + newOrder.getId() + " placed successfully!\n");
            
            loadOrders();

        } catch (Exception e) {
            orderStatusArea.appendText("Error placing final order: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        Stage stage = (Stage) addItemButton.getScene().getWindow();
        stage.close();
    }
}
