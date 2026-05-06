package pl.sylwestergladki.order_payment_service.order.OrderController;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.sylwestergladki.order_payment_service.order.Order.Order;
import pl.sylwestergladki.order_payment_service.order.Order.OrderStatus;
import pl.sylwestergladki.order_payment_service.order.OrderService.OrderService;
import pl.sylwestergladki.order_payment_service.order.dto.CreateOrderRequest;
import pl.sylwestergladki.order_payment_service.order.dto.OrderResponse;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateOrder() throws Exception {
        // given
        BigDecimal amount = BigDecimal.valueOf(100);
        CreateOrderRequest request = new CreateOrderRequest(amount);

        OrderResponse orderResponse =
                new OrderResponse(1L, OrderStatus.NEW, amount);


        when(service.createOrder(amount)).thenReturn(orderResponse);

        // when & then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100));

        verify(service).createOrder(amount);
    }

    @Test
    void shouldGetAllOrders() throws Exception {
        // given
        Order order1 = new Order();
        order1.setId(1L);
        order1.setAmount(BigDecimal.valueOf(100));

        Order order2 = new Order();
        order2.setId(2L);
        order2.setAmount(BigDecimal.valueOf(200));

        Page<Order> page = new PageImpl<>(List.of(order1, order2));

        when(service.getAll(any(Pageable.class))).thenReturn(page);

        // when & then
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].amount").value(100))
                .andExpect(jsonPath("$.content[1].amount").value(200));
    }
}