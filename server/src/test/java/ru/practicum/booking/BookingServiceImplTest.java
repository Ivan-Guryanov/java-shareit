package ru.practicum.booking;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingDtoCreate;
import ru.practicum.booking.service.BookingService;
import ru.practicum.exception.ValidationException;
import ru.practicum.item.Item;
import ru.practicum.user.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:shareit;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.sql.init.mode=never",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class BookingServiceImplTest {

    private final BookingService bookingService;
    private final EntityManager em;

    @Test
    @DisplayName("Сервис(интеграционный тест) - создание бронирования")
    void createBooking() {
        // given
        User owner = makeUser("owner@mail.com", "Owner");
        User booker = makeUser("booker@mail.com", "Booker");
        em.persist(owner);
        em.persist(booker);

        Item item = makeItem("Дрель", owner.getId());
        em.persist(item);
        em.flush();

        BookingDto bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        // when
        BookingDtoCreate result = bookingService.createBooking(booker.getId(), bookingDto);

        // then
        assertThat(result.getId(), notNullValue());
        assertThat(result.getItem().getName(), equalTo("Дрель"));
        assertThat(result.getBooker().getName(), equalTo("Booker"));
        assertThat(result.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - подтверждение бронирования")
    void bookingApproval() {
        // given
        User owner = makeUser("owner@mail.com", "Owner");
        User booker = makeUser("booker@mail.com", "Booker");
        em.persist(owner);
        em.persist(booker);

        Item item = makeItem("Пила", owner.getId());
        em.persist(item);

        Booking booking = Booking.builder()
                .itemId(item.getId())
                .booker(booker.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        em.persist(booking);
        em.flush();

        // when
        BookingDtoCreate result = bookingService.bookingApproval(owner.getId(), booking.getId(), true);

        // then
        assertThat(result.getStatus(), equalTo(BookingStatus.APPROVED));

        Booking updatedBooking = em.find(Booking.class, booking.getId());
        assertThat(updatedBooking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - получение бронирования по id")
    void findBookingById() {
        // given
        User owner = makeUser("owner@test.ru", "Owner");
        User booker = makeUser("booker@test.ru", "Booker");
        em.persist(owner);
        em.persist(booker);

        Item item = makeItem("Инструмент", owner.getId());
        em.persist(item);

        Booking booking = Booking.builder()
                .itemId(item.getId())
                .booker(booker.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        em.persist(booking);
        em.flush();

        // when
        BookingDtoCreate result = bookingService.findBookingById(owner.getId(), booking.getId());

        // then
        assertThat(result.getId(), equalTo(booking.getId()));
        assertThat(result.getItem().getName(), equalTo("Инструмент"));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - создание бронирования: ошибка, если начало бронирования null")
    void createWithStartNull() {
        // given
        User owner = makeUser("owner@mail.com", "Owner");
        User booker = makeUser("booker@mail.com", "Booker");
        em.persist(owner);
        em.persist(booker);

        Item item = makeItem("Дрель", owner.getId());
        em.persist(item);
        em.flush();

        BookingDto dto = BookingDto.builder()
                .itemId(item.getId())
                .start(null)
                .end(LocalDateTime.now().plusDays(1))
                .build();

        // when & then
        assertThrows(ValidationException.class, () -> bookingService.createBooking(booker.getId(), dto));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - создание бронирования: ошибка, если конец null")
    void createWithEndNull() {
        // given
        User owner = makeUser("owner@mail.com", "Owner");
        User booker = makeUser("booker@mail.com", "Booker");
        em.persist(owner);
        em.persist(booker);

        Item item = makeItem("Дрель", owner.getId());
        em.persist(item);
        em.flush();

        BookingDto dto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(null)
                .build();

        // when & then
        assertThrows(ValidationException.class, () -> bookingService.createBooking(booker.getId(), dto));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - создание бронирования: ошибка, если начало в прошлом")
    void createWithPastStart() {
        // given
        User owner = makeUser("owner@mail.com", "Owner");
        User booker = makeUser("booker@mail.com", "Booker");
        em.persist(owner);
        em.persist(booker);

        Item item = makeItem("Дрель", owner.getId());
        em.persist(item);
        em.flush();

        BookingDto dto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        // when & then
        assertThrows(ValidationException.class, () -> bookingService.createBooking(booker.getId(), dto));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - создание бронирования: ошибка, если конец раньше старта")
    void createWithEndBeforeStart() {
        // given
        User owner = makeUser("owner@mail.com", "Owner");
        User booker = makeUser("booker@mail.com", "Booker");
        em.persist(owner);
        em.persist(booker);

        Item item = makeItem("Дрель", owner.getId());
        em.persist(item);
        em.flush();

        BookingDto dto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        // when & then
        assertThrows(ValidationException.class, () -> bookingService.createBooking(booker.getId(), dto));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - создание бронирования: ошибка, если старт и конец совпадают")
    void createWithSameDates() {
        // given
        User owner = makeUser("owner@mail.com", "Owner");
        User booker = makeUser("booker@mail.com", "Booker");
        em.persist(owner);
        em.persist(booker);

        Item item = makeItem("Дрель", owner.getId());
        em.persist(item);
        em.flush();

        LocalDateTime sameDate = LocalDateTime.now().plusDays(1);
        BookingDto dto = BookingDto.builder()
                .itemId(item.getId())
                .start(sameDate)
                .end(sameDate)
                .build();

        // when & then
        assertThrows(ValidationException.class, () -> bookingService.createBooking(booker.getId(), dto));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - создание бронирования: ошибка, если вещь недоступна")
    void createWithNotAvailableItem() {
        // given
        User owner = makeUser("owner@mail.com", "Owner");
        User booker = makeUser("booker@mail.com", "Booker");
        em.persist(owner);
        em.persist(booker);

        Item item = Item.builder()
                .name("Сломанная дрель").description("Desc").available(false).owner(owner.getId())
                .build();
        em.persist(item);
        em.flush();

        BookingDto dto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        // when & then
        assertThrows(ValidationException.class, () -> bookingService.createBooking(booker.getId(), dto));
    }


    @Test
    @DisplayName("Сервис(интеграционный тест) - получение бронирований пользователя по условию")
    void findBookingByUser() {
        // given — Создаем пользователя и две его брони (прошлую и будущую)
        User booker = makeUser("booker@test.ru", "Booker");
        User owner = makeUser("owner@test.ru", "Owner");
        em.persist(booker);
        em.persist(owner);

        Item item = makeItem("Вещь", owner.getId());
        em.persist(item);

        // Прошлое бронирование
        Booking past = Booking.builder()
                .itemId(item.getId()).booker(booker.getId())
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(2))
                .status(BookingStatus.APPROVED).build();

        // Будущее бронирование
        Booking future = Booking.builder()
                .itemId(item.getId()).booker(booker.getId())
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(10))
                .status(BookingStatus.APPROVED).build();

        em.persist(past);
        em.persist(future);
        em.flush();

        // when
        Collection<BookingDtoCreate> result = bookingService.findBookingByUser(booker.getId(), "PAST");

        // then
        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getId(), equalTo(past.getId()));
    }


    @Test
    @DisplayName("Сервис(интеграционный тест) - получение бронирования для вещей пользователя")
    void findBookingByOwner() {
        // given
        User owner = makeUser("owner@mail.com", "Owner");
        User booker = makeUser("booker@mail.com", "Booker");
        em.persist(owner);
        em.persist(booker);

        Item item = makeItem("Вещь", owner.getId());
        em.persist(item);

        Booking booking = Booking.builder()
                .itemId(item.getId())
                .booker(booker.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        em.persist(booking);
        em.flush();

        // when
        Collection<BookingDtoCreate> result = bookingService.findBookingByOwner(owner.getId(), "WAITING");

        // then
        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getStatus(), equalTo(BookingStatus.WAITING));
    }

    // Вспомогательные методы
    private User makeUser(String email, String name) {
        return User.builder().name(name).email(email).build();
    }

    private Item makeItem(String name, Long ownerId) {
        return Item.builder().name(name).description("Desc").available(true).owner(ownerId).build();
    }
}
