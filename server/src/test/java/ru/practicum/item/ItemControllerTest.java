package ru.practicum.item;

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
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemWithCommentsDto;
import ru.practicum.item.servise.ItemServise;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemServise itemService;

    @InjectMocks
    private ItemController controller;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private MockMvc mvc;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setDescription("Простая дрель");
        itemDto.setAvailable(true);
        itemDto.setOwner(1L);
        itemDto.setRequestId(null);
    }

    @Test
    @DisplayName("Контролер - создание вещи")
    void createItem() throws Exception {
        when(itemService.createItem(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.owner", is((int) itemDto.getOwner()))); // Jackson может привести long к int в JSON
    }

    @Test
    @DisplayName("Контролер - обновление вещи")
    void updateItem() throws Exception {
        itemDto.setName("Новая дрель");
        when(itemService.updateItem(anyLong(), anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Новая дрель")));
    }

    @Test
    @DisplayName("Контролер - получение вещи по id")
    void getItemById() throws Exception {
        CommentDto comment = CommentDto.builder()
                .id(1L)
                .text("Отличная дрель!")
                .authorName("Ivan")
                .build();

        ItemWithCommentsDto dto = ItemWithCommentsDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Мощная")
                .available(true)
                .lastBooking(10L)
                .nextBooking(11L)
                .comments(List.of(comment))
                .build();

        when(itemService.getItemWithCommentsById(anyLong()))
                .thenReturn(dto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.lastBooking", is(10)))
                .andExpect(jsonPath("$.nextBooking", is(11)))
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].text", is("Отличная дрель!")))
                .andExpect(jsonPath("$.comments[0].authorName", is("Ivan")));
    }

    @Test
    @DisplayName("Контролер - получение всех вещей пользователя")
    void getAllItemByUserID() throws Exception {
        when(itemService.getAllItemByUserID(anyLong()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class));
    }

    @Test
    @DisplayName("Контролер - поиск вещи")
    void itemSearch() throws Exception {
        when(itemService.itemSearch(anyString()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].available", is(true)));
    }

    @Test
    @DisplayName("Контролер - создание комментария")
    void createComment() throws Exception {

        CommentDto inputDto = CommentDto.builder()
                .text("Крутая дрель, рекомендую")
                .build();

        CommentDto resultDto = CommentDto.builder()
                .id(1L)
                .text("Крутая дрель, рекомендую")
                .authorName("Ivan")
                .created(LocalDateTime.now())
                .build();

        when(itemService.createComment(anyLong(), anyLong(), any()))
                .thenReturn(resultDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(inputDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(resultDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(resultDto.getText())))
                .andExpect(jsonPath("$.authorName", is(resultDto.getAuthorName())))
                .andExpect(jsonPath("$.created", notNullValue())); // Проверяем наличие даты
    }
}
