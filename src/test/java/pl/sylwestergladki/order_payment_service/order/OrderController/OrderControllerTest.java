package pl.sylwestergladki.order_payment_service.order.OrderController;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.sylwestergladki.order_payment_service.order.Order.Order;
import pl.sylwestergladki.order_payment_service.order.OrderService.OrderService;
import pl.sylwestergladki.order_payment_service.order.dto.CreateOrderRequest;

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

        Order order = new Order();
        order.setId(1L);
        order.setAmount(amount);

        when(service.createOrder(amount)).thenReturn(order);

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
        order1.setAmount(BigDecimal.valueOf(100));
        Order order2 = new Order();
        order2.setAmount(BigDecimal.valueOf(200));
        List<Order> orders = List.of(order1, order2);

        when(service.getAll()).thenReturn(orders);

        // when & then
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].amount").value(100))
                .andExpect(jsonPath("$[1].amount").value(200));
    }
}