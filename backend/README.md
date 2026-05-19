# order-payment-service
## ARCHITECTURE
Client -> Controller -> Service -> Domain -> Repository -> Database
Simple monolithic Spring Boot application with layered architecture and domain-driven business logic
## MAIN FLOWS
### CREATE ORDER
1. Client
2. OrderController
3. OrderService
4. Order.create(amount)   ← logika domenowa
5. OrderRepository.save()
6. Database
7. return Order (NEW)
### PAYMENT
1. Client
2. PaymentController
3. PaymentService
4. OrderService.getOrder(orderId)
5. Order.markAsPaid() / markAsFailed()
6. OrderRepository.save()
7. PaymentRepository.save()
8. Database
### GET ORDER
1. Client
2. OrderController
3. OrderService
4. OrderRepository.findById()
5. Database
6. return Order
### GET ALL ORDERS
1. Client
2. OrderController
3. OrderService
4. OrderRepositor.findAll()
5. Database
6. Return List<Order>
### PAYMENT FAILURE
1. Client
2. PaymentController
3. PaymentService
4. OrderService.getOrder()
5. Order.markAsFailed()
6. OrderRepository.save()
7. PaymentRepository.save()
### INVALID ORDER
1. Client
2. OrderController
3. OrderService
4. Order.create(amount)
5. InvalidOrderAmountException
6. ControllerAdvice (jeśli masz)
7. HTTP 400

## DOMAIN RULES
- Order cannot have negative amount
- Order can be NEW -> PAID or FAILED
- Paid order cannot be failed
