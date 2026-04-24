package pl.sylwestergladki.order_payment_service.order.OrderController;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import pl.sylwestergladki.order_payment_service.order.dto.CreateOrderRequest;
import pl.sylwestergladki.order_payment_service.order.Order.Order;
import pl.sylwestergladki.order_payment_service.order.OrderService.OrderService;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public Order create(@RequestBody @Valid CreateOrderRequest request) {
        return service.createOrder(request.amount());
    }

    @GetMapping
    public List<Order> getAll() {
        return service.getAll();
    }
}
