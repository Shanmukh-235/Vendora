package com.vendora.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vendora.model.CartItem;
import com.vendora.model.DeliveryAgent;
import com.vendora.model.Order;
import com.vendora.model.OrderItem;
import com.vendora.model.Product;
import com.vendora.model.User;
import com.vendora.repository.DeliveryAgentRepository;
import com.vendora.repository.OrderItemRepository;
import com.vendora.repository.OrderRepository;
import com.vendora.repository.ProductRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    @Autowired
    private ProductRepository productRepository;

    
    @Autowired
    private ProductService productService;

    /**
     * 🛒 Place an order for a user and update stock automatically
     */
    @Transactional
    public Order placeOrder(User user, List<CartItem> cartItems) {
        if (user == null || cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot place order: user or cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setStatus("PLACED");
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (product == null)
                continue;

            int currentStock = product.getStock();
            int quantity = cartItem.getQuantity();

            // ✅ Stock validation
            if (currentStock < quantity) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            // ✅ Reduce stock and save
            product.setStock(currentStock - quantity);
            productRepository.save(product);

            // ✅ Create Order Item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(product.getPrice());
            orderItemRepository.save(orderItem);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        order.setTotalAmount(total);
        return orderRepository.save(order);
    }

    /** 🔍 Fetch all orders (Admin view) */
    public List<Order> getAllOrders() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    /** 🔍 Fetch orders for a specific user */
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    /** 🔍 Get single order by ID */
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    /** 📊 Count total orders placed by a user */
    public long countOrdersByUser(User user) {
        return orderRepository.findByUser(user).size();
    }

    /** 🔍 Fetch orders assigned to a delivery agent */
    public List<Order> getOrdersAssignedToAgent(DeliveryAgent agent) {
        if (agent == null)
            return List.of();
        return orderRepository.findByDeliveryAgent(agent);
    }

    /** 🚚 Assign delivery agent */
    public void assignDeliveryAgent(Long orderId, Long agentId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        DeliveryAgent agent = deliveryAgentRepository.findById(agentId).orElse(null);

        if (order != null && agent != null) {
            order.setDeliveryAgent(agent);
            orderRepository.save(order);
        }
    }

    /** 🔄 Toggle order status */
    public void toggleOrderStatus(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            if ("DELIVERED".equalsIgnoreCase(order.getStatus())) {
                order.setStatus("PLACED");
            } else {
                order.setStatus("DELIVERED");
            }
            orderRepository.save(order);
        }
    }

    /** 💰 Calculate total revenue from delivered orders */
    public BigDecimal getTotalRevenue() {
        return orderRepository.findAll().stream()
                .filter(order -> "DELIVERED".equalsIgnoreCase(order.getStatus()))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public boolean cancelOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUser().equals(user)) {
            return false;
        }

        if (!"PLACED".equalsIgnoreCase(order.getStatus())) {
            return false; // Only pending orders can be canceled
        }

        // Restore product stock
        List<OrderItem> items = orderItemRepository.findByOrder(order);
        for (OrderItem item : items) {
            Product product = item.getProduct();
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
            }
        }

        order.setStatus("CANCELED");
        orderRepository.save(order);
        return true;
    }

    public List<OrderItem> getOrderItemsByOrder(Order order) {
        return orderItemRepository.findByOrder(order);
    }

    @Transactional
    public void cancelOrderAndRestoreStock(Order order) {
        if (order == null)
            return;

        // restore stock for each product in the order
        List<OrderItem> items = orderItemRepository.findByOrder(order);
        for (OrderItem item : items) {
            Product product = item.getProduct();
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productService.saveProduct(product);
            }
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }


}
