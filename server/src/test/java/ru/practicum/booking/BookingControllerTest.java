package ru.practicum.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.booking.BookingStatus;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingDtoCreate;
import ru.practicum.booking.service.BookingService;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController controller;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mvc;
    private BookingDto bookingDto;
    private BookingDtoCreate bookingDtoCreate;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        ItemDto itemDto = ItemDto.builder().id(1L).name("Дрель").build();
        UserDto userDto = UserDto.builder().id(1L).name("Ivan").build();

        bookingDtoCreate = BookingDtoCreate.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(itemDto)
                .booker(userDto)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    @DisplayName("Контролер - создание бронирования")
    void createBooking() throws Exception {
        when(bookingService.createBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingDtoCreate);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoCreate.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoCreate.getStatus().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDtoCreate.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoCreate.getBooker().getId()), Long.class));
    }

    @Test
    @DisplayName("Контролер - подтверждение бронирования")
    void bookingApproval() throws Exception {
        bookingDtoCreate.setStatus(BookingStatus.APPROVED);
        when(bookingService.bookingApproval(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDtoCreate);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    @DisplayName("Контролер - получение бронирования по id")
    void findBookingById() throws Exception {
        when(bookingService.findBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDtoCreate);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.name", is("Дрель")));
    }

    @Test
    @DisplayName("Контролер - получение бронирований пользователя")
    void findBookingByUser() throws Exception {
        when(bookingService.findBookingByUser(anyLong(), anyString()))
                .thenReturn(List.of(bookingDtoCreate));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDtoCreate.getId()), Long.class));
    }

    @Test
    @DisplayName("Контролер - получение бронирований для вещей пользователя")
    void findBookingByOwner() throws Exception {
        Long userId = 1L;
        Long bookingId1 = 101L;
        Long bookingId2 = 102L;

        BookingDtoCreate approvedBooking1 = BookingDtoCreate.builder()
                .id(bookingId1)
                .status(BookingStatus.APPROVED)
                .item(ItemDto.builder().id(1L).name("Дрель").build())
                .booker(UserDto.builder().id(2L).name("Ivan").build())
                .build();

        BookingDtoCreate approvedBooking2 = BookingDtoCreate.builder()
                .id(bookingId2)
                .status(BookingStatus.APPROVED)
                .item(ItemDto.builder().id(2L).name("Пила").build())
                .booker(UserDto.builder().id(2L).name("Ivan").build())
                .build();

        when(bookingService.bookingApproval(eq(userId), anyLong(), eq(true)))
                .thenReturn(approvedBooking1);

        when(bookingService.findBookingByOwner(eq(userId), eq("ALL")))
                .thenReturn(List.of(approvedBooking1, approvedBooking2));

        mvc.perform(patch("/bookings/{bookingId}", bookingId1)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.id", is(bookingId1), Long.class));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // Проверяем, что в списке 2 бронирования
                .andExpect(jsonPath("$[0].status", is("APPROVED")))
                .andExpect(jsonPath("$[1].status", is("APPROVED")))
                .andExpect(jsonPath("$[0].id", is(bookingId1), Long.class))
                .andExpect(jsonPath("$[1].id", is(bookingId2), Long.class));
    }

}
