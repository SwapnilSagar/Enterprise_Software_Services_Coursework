package uk.ac.newcastle.enterprisemiddleware.hotel;

import uk.ac.newcastle.enterprisemiddleware.hotelbooking.HotelBooking;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Swapnil Sagar
 *
 *
 * */
public class HotelMapper
{

    public static Hotel toHotel(HotelPayload dto){
        Hotel hotel = new Hotel();
        hotel.setId(dto.getId());
        hotel.setName(dto.getName());

        hotel.setPhoneNumber(dto.getPhoneNumber());
        hotel.setPostcode(dto.getPostcode());

        return hotel;
    }



    public static HotelPayload toDTO(Hotel hotel){
        HotelPayload dto = new HotelPayload();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setPhoneNumber(hotel.getPhoneNumber());

        dto.setPostcode(hotel.getPostcode());
        if(hotel.getBookings() != null)
        {
            List<HotelBookMapPayload> bookMapDTOS = hotel.getBookings().stream()
                    .map(HotelMapper::toBookingDTO)
                    .collect(Collectors.toList());
            dto.setBookings(bookMapDTOS);
        }


        return dto;
    }

    private static HotelBookMapPayload toBookingDTO(HotelBooking booking) {
        HotelBookMapPayload dto = new HotelBookMapPayload();
        dto.setId(booking.getId());
        dto.setBookingDate(booking.getBookingDate());

        dto.setStatus(booking.getStatus());
        return dto;
    }
}
