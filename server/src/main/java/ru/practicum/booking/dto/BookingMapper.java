package ru.practicum.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.booking.Booking;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.dto.UserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDto mapToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItemId())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static BookingDtoCreate mapToDtoCreate(Booking booking) {
        ItemDto item = new ItemDto();
        item.setId(booking.getItemId());
        UserDto user = new UserDto();
        user.setId(booking.getBooker());

        return BookingDtoCreate.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(user)
                .status(booking.getStatus())
                .build();
    }

    public static Booking mapToBooking(BookingDto booking) {
        return Booking.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItemId())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static Booking mapToBookingCreate(BookingDtoCreate booking) {
        return Booking.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .booker(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

}
