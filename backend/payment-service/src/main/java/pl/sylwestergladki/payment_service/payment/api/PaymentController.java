package pl.sylwestergladki.payment_service.payment.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sylwestergladki.payment_service.payment.application.PaymentService;
import pl.sylwestergladki.payment_service.payment.application.dto.PaymentResponse;


@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(paymentService.getByIdOrThrow(id));
    }

    @GetMapping
    public Page<PaymentResponse> getAll(Pageable pageable) {
        return paymentService.getAll(pageable)
                .map(payment -> new PaymentResponse(payment.getId(), payment.getOrderId(), payment.getStatus()));
    }

}
