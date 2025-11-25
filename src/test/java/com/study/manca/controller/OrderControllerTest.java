package com.study.manca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.manca.dto.OrderRequest;
import com.study.manca.dto.OrderResponse;
import com.study.manca.entity.Member;
import com.study.manca.entity.Menu;
import com.study.manca.entity.Order;
import com.study.manca.entity.Seat;
import com.study.manca.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ContextConfiguration(classes = {OrderController.class})
@DisplayName("OrderController 테스트")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private OrderResponse createOrderResponse(Long id, Long memberId, String memberName,
                                               Long seatId, String seatNumber,
                                               Long menuId, String menuName, BigDecimal unitPrice,
                                               Integer quantity, Order.OrderStatus status, String remarks) {
        Member member = Member.builder().name(memberName).email("test@test.com").phone("010-0000-0000").build();
        Menu menu = Menu.builder().name(menuName).category(Menu.MenuCategory.BEVERAGE).price(unitPrice).isAvailable(true).build();
        Seat seat = Seat.builder().seatNumber(seatNumber).type(Seat.SeatType.REGULAR).status(Seat.SeatStatus.AVAILABLE).build();

        Order order = Order.builder()
                .member(member)
                .menu(menu)
                .seat(seat)
                .quantity(quantity)
                .totalPrice(unitPrice.multiply(BigDecimal.valueOf(quantity)))
                .orderDateTime(LocalDateTime.now())
                .status(status)
                .remarks(remarks)
                .build();

        try {
            java.lang.reflect.Field orderIdField = Order.class.getDeclaredField("id");
            orderIdField.setAccessible(true);
            orderIdField.set(order, id);

            java.lang.reflect.Field memberIdField = Member.class.getDeclaredField("id");
            memberIdField.setAccessible(true);
            memberIdField.set(member, memberId);

            java.lang.reflect.Field menuIdField = Menu.class.getDeclaredField("id");
            menuIdField.setAccessible(true);
            menuIdField.set(menu, menuId);

            java.lang.reflect.Field seatIdField = Seat.class.getDeclaredField("id");
            seatIdField.setAccessible(true);
            seatIdField.set(seat, seatId);

            java.lang.reflect.Field createdAtField = Order.class.getSuperclass().getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(order, LocalDateTime.now());

            java.lang.reflect.Field updatedAtField = Order.class.getSuperclass().getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(order, LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return OrderResponse.from(order);
    }

    @Test
    @DisplayName("GET /api/orders - 전체 주문 조회 성공")
    void getAllOrders_Success() throws Exception {
        // given
        List<OrderResponse> orders = Arrays.asList(
                createOrderResponse(1L, 1L, "홍길동", 1L, "A-01", 1L, "아메리카노",
                        new BigDecimal("3000"), 2, Order.OrderStatus.PENDING, "얼음 적게"),
                createOrderResponse(2L, 2L, "김철수", 2L, "A-02", 2L, "카페라떼",
                        new BigDecimal("4000"), 1, Order.OrderStatus.COMPLETED, null)
        );
        given(orderService.findAll()).willReturn(orders);

        // when & then
        mockMvc.perform(get("/api/orders"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].memberName").value("홍길동"))
                .andExpect(jsonPath("$[1].memberName").value("김철수"));
    }

    @Test
    @DisplayName("GET /api/orders/{id} - 주문 상세 조회 성공")
    void getOrderById_Success() throws Exception {
        // given
        OrderResponse order = createOrderResponse(1L, 1L, "홍길동", 1L, "A-01", 1L, "아메리카노",
                new BigDecimal("3000"), 2, Order.OrderStatus.PENDING, "얼음 적게");
        given(orderService.findById(1L)).willReturn(order);

        // when & then
        mockMvc.perform(get("/api/orders/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.menuName").value("아메리카노"))
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    @DisplayName("GET /api/orders/member/{memberId} - 회원별 주문 조회 성공")
    void getOrdersByMemberId_Success() throws Exception {
        // given
        List<OrderResponse> orders = Arrays.asList(
                createOrderResponse(1L, 1L, "홍길동", 1L, "A-01", 1L, "아메리카노",
                        new BigDecimal("3000"), 2, Order.OrderStatus.PENDING, null),
                createOrderResponse(2L, 1L, "홍길동", 1L, "A-01", 2L, "카페라떼",
                        new BigDecimal("4000"), 1, Order.OrderStatus.COMPLETED, null)
        );
        given(orderService.findByMemberId(1L)).willReturn(orders);

        // when & then
        mockMvc.perform(get("/api/orders/member/{memberId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].memberId").value(1))
                .andExpect(jsonPath("$[1].memberId").value(1));
    }

    @Test
    @DisplayName("GET /api/orders/status/{status} - 상태별 주문 조회 성공")
    void getOrdersByStatus_Success() throws Exception {
        // given
        List<OrderResponse> orders = Arrays.asList(
                createOrderResponse(1L, 1L, "홍길동", 1L, "A-01", 1L, "아메리카노",
                        new BigDecimal("3000"), 2, Order.OrderStatus.PENDING, null)
        );
        given(orderService.findByStatus(Order.OrderStatus.PENDING)).willReturn(orders);

        // when & then
        mockMvc.perform(get("/api/orders/status/{status}", "PENDING"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /api/orders - 주문 등록 성공")
    void createOrder_Success() throws Exception {
        // given
        OrderRequest request = new OrderRequest(1L, 1L, 1L, 2, "얼음 적게");
        OrderResponse response = createOrderResponse(1L, 1L, "홍길동", 1L, "A-01", 1L, "아메리카노",
                new BigDecimal("3000"), 2, Order.OrderStatus.PENDING, "얼음 적게");
        given(orderService.create(any(OrderRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.menuName").value("아메리카노"))
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    @DisplayName("POST /api/orders/{id}/status - 주문 상태 변경 성공")
    void updateOrderStatus_Success() throws Exception {
        // given
        OrderResponse response = createOrderResponse(1L, 1L, "홍길동", 1L, "A-01", 1L, "아메리카노",
                new BigDecimal("3000"), 2, Order.OrderStatus.COMPLETED, "얼음 적게");
        given(orderService.updateStatus(eq(1L), eq(Order.OrderStatus.COMPLETED))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/orders/{id}/status", 1L)
                        .param("status", "COMPLETED"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("POST /api/orders/{id}/delete - 주문 삭제 성공")
    void deleteOrder_Success() throws Exception {
        // given
        doNothing().when(orderService).delete(1L);

        // when & then
        mockMvc.perform(post("/api/orders/{id}/delete", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
