package uk.ac.newcastle.enterprisemiddleware.hotel;

import uk.ac.newcastle.enterprisemiddleware.DTO.HotelBookMapDTO;
import uk.ac.newcastle.enterprisemiddleware.DTO.HotelDTO;
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

    public static Hotel toHotel(HotelDTO dto){
        Hotel hotel = new Hotel();
        hotel.setId(dto.getId());
        hotel.setName(dto.getName());

        hotel.setPhoneNumber(dto.getPhoneNumber());
        hotel.setPostcode(dto.getPostcode());

        return hotel;
    }



    public static HotelDTO toDTO(Hotel hotel){
        HotelDTO dto = new HotelDTO();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setPhoneNumber(hotel.getPhoneNumber());

        dto.setPostcode(hotel.getPostcode());
        if(hotel.getBookings() != null)
        {
            List<HotelBookMapDTO> bookMapDTOS = hotel.getBookings().stream()
                    .map(HotelMapper::toBookingDTO)
                    .collect(Collectors.toList());
            dto.setBookings(bookMapDTOS);
        }


        return dto;
    }

    private static HotelBookMapDTO toBookingDTO(HotelBooking booking) {
        HotelBookMapDTO dto = new HotelBookMapDTO();
        dto.setId(booking.getId());
        dto.setBookingDate(booking.getBookingDate());

        dto.setStatus(booking.getStatus());
        return dto;
    }
}
