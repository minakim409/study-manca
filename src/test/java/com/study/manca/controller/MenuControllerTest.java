//package com.study.manca.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.study.manca.dto.MenuRequest;
//import com.study.manca.dto.MenuResponse;
//import com.study.manca.entity.Menu;
//import com.study.manca.service.MenuService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.doNothing;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest
//@ContextConfiguration(classes = {MenuController.class})
//@DisplayName("MenuController 테스트")
//class MenuControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private MenuService menuService;
//
//    private MenuResponse createMenuResponse(Long id, String name, Menu.MenuCategory category,
//                                            BigDecimal price, String description, Boolean isAvailable) {
//        Menu menu = Menu.builder()
//                .name(name)
//                .category(category)
//                .price(price)
//                .description(description)
//                .isAvailable(isAvailable)
//                .build();
//        try {
//            java.lang.reflect.Field idField = Menu.class.getDeclaredField("id");
//            idField.setAccessible(true);
//            idField.set(menu, id);
//
//            java.lang.reflect.Field createdAtField = Menu.class.getSuperclass().getDeclaredField("createdAt");
//            createdAtField.setAccessible(true);
//            createdAtField.set(menu, LocalDateTime.now());
//
//            java.lang.reflect.Field updatedAtField = Menu.class.getSuperclass().getDeclaredField("updatedAt");
//            updatedAtField.setAccessible(true);
//            updatedAtField.set(menu, LocalDateTime.now());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        return MenuResponse.from(menu);
//    }
//
//    @Test
//    @DisplayName("GET /api/menus - 전체 메뉴 조회 성공")
//    void getAllMenus_Success() throws Exception {
//        // given
//        List<MenuResponse> menus = Arrays.asList(
//                createMenuResponse(1L, "아메리카노", Menu.MenuCategory.BEVERAGE,
//                        new BigDecimal("3000"), "깊고 진한 에스프레소", true),
//                createMenuResponse(2L, "치즈볼", Menu.MenuCategory.SNACK,
//                        new BigDecimal("4000"), "바삭한 치즈볼", true)
//        );
//        given(menuService.findAll()).willReturn(menus);
//
//        // when & then
//        mockMvc.perform(get("/api/menus"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(2))
//                .andExpect(jsonPath("$[0].name").value("아메리카노"))
//                .andExpect(jsonPath("$[1].name").value("치즈볼"));
//    }
//
//    @Test
//    @DisplayName("GET /api/menus/available - 판매 가능 메뉴 조회 성공")
//    void getAvailableMenus_Success() throws Exception {
//        // given
//        List<MenuResponse> menus = Arrays.asList(
//                createMenuResponse(1L, "아메리카노", Menu.MenuCategory.BEVERAGE,
//                        new BigDecimal("3000"), "깊고 진한 에스프레소", true)
//        );
//        given(menuService.findAvailable()).willReturn(menus);
//
//        // when & then
//        mockMvc.perform(get("/api/menus/available"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(1))
//                .andExpect(jsonPath("$[0].isAvailable").value(true));
//    }
//
//    @Test
//    @DisplayName("GET /api/menus/category/{category} - 카테고리별 메뉴 조회 성공")
//    void getMenusByCategory_Success() throws Exception {
//        // given
//        List<MenuResponse> menus = Arrays.asList(
//                createMenuResponse(1L, "아메리카노", Menu.MenuCategory.BEVERAGE,
//                        new BigDecimal("3000"), "깊고 진한 에스프레소", true),
//                createMenuResponse(2L, "카페라떼", Menu.MenuCategory.BEVERAGE,
//                        new BigDecimal("4000"), "부드러운 라떼", true)
//        );
//        given(menuService.findByCategory(Menu.MenuCategory.BEVERAGE)).willReturn(menus);
//
//        // when & then
//        mockMvc.perform(get("/api/menus/category/{category}", "BEVERAGE"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(2))
//                .andExpect(jsonPath("$[0].category").value("BEVERAGE"));
//    }
//
//    @Test
//    @DisplayName("GET /api/menus/{id} - 메뉴 상세 조회 성공")
//    void getMenuById_Success() throws Exception {
//        // given
//        MenuResponse menu = createMenuResponse(1L, "아메리카노", Menu.MenuCategory.BEVERAGE,
//                new BigDecimal("3000"), "깊고 진한 에스프레소", true);
//        given(menuService.findById(1L)).willReturn(menu);
//
//        // when & then
//        mockMvc.perform(get("/api/menus/{id}", 1L))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.name").value("아메리카노"))
//                .andExpect(jsonPath("$.price").value(3000));
//    }
//
//    @Test
//    @DisplayName("POST /api/menus - 메뉴 등록 성공")
//    void createMenu_Success() throws Exception {
//        // given
//        MenuRequest request = new MenuRequest("아메리카노", Menu.MenuCategory.BEVERAGE,
//                new BigDecimal("3000"), "깊고 진한 에스프레소", true);
//        MenuResponse response = createMenuResponse(1L, "아메리카노", Menu.MenuCategory.BEVERAGE,
//                new BigDecimal("3000"), "깊고 진한 에스프레소", true);
//        given(menuService.create(any(MenuRequest.class))).willReturn(response);
//
//        // when & then
//        mockMvc.perform(post("/api/menus")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andDo(print())
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.name").value("아메리카노"));
//    }
//
//    @Test
//    @DisplayName("POST /api/menus/{id}/update - 메뉴 정보 전체 수정 성공")
//    void updateMenu_Success() throws Exception {
//        // given
//        MenuRequest request = new MenuRequest("아이스 아메리카노", Menu.MenuCategory.BEVERAGE,
//                new BigDecimal("3500"), "시원한 아메리카노", true);
//        MenuResponse response = createMenuResponse(1L, "아이스 아메리카노", Menu.MenuCategory.BEVERAGE,
//                new BigDecimal("3500"), "시원한 아메리카노", true);
//        given(menuService.update(eq(1L), any(MenuRequest.class))).willReturn(response);
//
//        // when & then
//        mockMvc.perform(post("/api/menus/{id}/update", 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("아이스 아메리카노"))
//                .andExpect(jsonPath("$.price").value(3500));
//    }
//
//    @Test
//    @DisplayName("POST /api/menus/{id}/update-partial - 메뉴 정보 부분 수정 성공")
//    void updateMenuPartial_Success() throws Exception {
//        // given
//        MenuRequest request = new MenuRequest(null, null, new BigDecimal("3500"), null, null);
//        MenuResponse response = createMenuResponse(1L, "아메리카노", Menu.MenuCategory.BEVERAGE,
//                new BigDecimal("3500"), "깊고 진한 에스프레소", true);
//        given(menuService.updatePartial(eq(1L), any(MenuRequest.class))).willReturn(response);
//
//        // when & then
//        mockMvc.perform(post("/api/menus/{id}/update-partial", 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.price").value(3500));
//    }
//
//    @Test
//    @DisplayName("POST /api/menus/{id}/delete - 메뉴 삭제 성공")
//    void deleteMenu_Success() throws Exception {
//        // given
//        doNothing().when(menuService).delete(1L);
//
//        // when & then
//        mockMvc.perform(post("/api/menus/{id}/delete", 1L))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//}
