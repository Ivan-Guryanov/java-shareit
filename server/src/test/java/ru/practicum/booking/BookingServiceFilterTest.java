package ru.practicum.booking;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingStatus;
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
    class BookingServiceFilterTest {

        private final BookingService bookingService;
        private final EntityManager em;

        private User owner;
        private User booker;
        private Item item;

        @BeforeEach
        void setUp() {
            owner = User.builder().name("Owner").email("owner@mail.com").build();
            em.persist(owner);

            booker = User.builder().name("Booker").email("booker@mail.com").build();
            em.persist(booker);

            item = Item.builder().name("Предмет").description("Описание").available(true).owner(owner.getId()).build();
            em.persist(item);

            em.flush();
        }

        @Test
        @DisplayName("Сервис(интеграционный тест) - фильтр ALL")
        void findByOwnerAll() {
            Booking b1 = Booking.builder().itemId(item.getId()).booker(booker.getId())
                    .start(LocalDateTime.now().minusDays(1)).end(LocalDateTime.now().plusDays(1))
                    .status(BookingStatus.APPROVED).build();
            Booking b2 = Booking.builder().itemId(item.getId()).booker(booker.getId())
                    .start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(3))
                    .status(BookingStatus.WAITING).build();
            em.persist(b1);
            em.persist(b2);
            em.flush();

            Collection<BookingDtoCreate> result = bookingService.findBookingByOwner(owner.getId(), "ALL");

            assertThat(result, hasSize(2));
        }

        @Test
        @DisplayName("Сервис(интеграционный тест) - фильтр CURRENT")
        void findByOwnerCurrent() {
            Booking current = Booking.builder().itemId(item.getId()).booker(booker.getId())
                    .start(LocalDateTime.now().minusDays(1)).end(LocalDateTime.now().plusDays(1))
                    .status(BookingStatus.APPROVED).build();
            em.persist(current);
            em.flush();

            Collection<BookingDtoCreate> result = bookingService.findBookingByOwner(owner.getId(), "CURRENT");

            assertThat(result, hasSize(1));
        }

        @Test
        @DisplayName("Сервис(интеграционный тест) - фильтр PAST")
        void findByOwnerPast() {
            Booking past = Booking.builder().itemId(item.getId()).booker(booker.getId())
                    .start(LocalDateTime.now().minusDays(5)).end(LocalDateTime.now().minusDays(2))
                    .status(BookingStatus.APPROVED).build();
            em.persist(past);
            em.flush();

            Collection<BookingDtoCreate> result = bookingService.findBookingByOwner(owner.getId(), "PAST");

            assertThat(result, hasSize(1));
        }

        @Test
        @DisplayName("Сервис(интеграционный тест) - фильтр FUTURE")
        void findByOwnerFuture() {
            Booking future = Booking.builder().itemId(item.getId()).booker(booker.getId())
                    .start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2))
                    .status(BookingStatus.APPROVED).build();
            em.persist(future);
            em.flush();

            Collection<BookingDtoCreate> result = bookingService.findBookingByOwner(owner.getId(), "FUTURE");

            assertThat(result, hasSize(1));
        }

        @Test
        @DisplayName("Сервис(интеграционный тест) - фильтр WAITING")
        void findByOwnerWaiting() {
            Booking waiting = Booking.builder().itemId(item.getId()).booker(booker.getId())
                    .start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2))
                    .status(BookingStatus.WAITING).build();
            em.persist(waiting);
            em.flush();

            Collection<BookingDtoCreate> result = bookingService.findBookingByOwner(owner.getId(), "WAITING");

            assertThat(result, hasSize(1));
            assertThat(result.iterator().next().getStatus(), equalTo(BookingStatus.WAITING));
        }

        @Test
        @DisplayName("Сервис(интеграционный тест) - фильтр REJECTED")
        void findByOwnerRejected() {
            Booking rejected = Booking.builder().itemId(item.getId()).booker(booker.getId())
                    .start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2))
                    .status(BookingStatus.REJECTED).build();
            em.persist(rejected);
            em.flush();

            Collection<BookingDtoCreate> result = bookingService.findBookingByOwner(owner.getId(), "REJECTED");

            assertThat(result, hasSize(1));
            assertThat(result.iterator().next().getStatus(), equalTo(BookingStatus.REJECTED));
        }

        @Test
        @DisplayName("Сервис(интеграционный тест) - фильтр ошибка ValidationException при неверном статусе")
        void shouldThrowExceptionOnInvalidState() {
            assertThrows(ValidationException.class, () ->
                    bookingService.findBookingByOwner(owner.getId(), "INVALID_STATE")
            );
        }
    }
