package pl.sylwestergladki.order_payment_service.order.OrderController;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sylwestergladki.order_payment_service.order.dto.CreateOrderRequest;
import pl.sylwestergladki.order_payment_service.order.Order.Order;
import pl.sylwestergladki.order_payment_service.order.OrderService.OrderService;
import pl.sylwestergladki.order_payment_service.order.dto.OrderResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest request) {
        OrderResponse orderResponse = service.createOrder(request.amount());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderResponse);
    }

    @GetMapping
    public Page<OrderResponse> getAll(Pageable pageable) {
        return service.getAll(pageable)
                .map(order -> new OrderResponse(order.getId(), order.getStatus(),order.getAmount()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getByIdOrThrow(id));
    }

}
