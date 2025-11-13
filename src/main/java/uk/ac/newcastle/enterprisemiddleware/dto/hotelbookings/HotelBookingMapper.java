package uk.ac.newcastle.enterprisemiddleware.dto.hotelbookings;

import uk.ac.newcastle.enterprisemiddleware.hotelbooking.HotelBooking;
import uk.ac.newcastle.enterprisemiddleware.dto.hotel.HotelDTO;

/**
 * @author Swapnil Sagar
 * */
public class HotelBookingMapper {

    public static HotelBookingDTO toDTO(HotelBooking booking){
        HotelBookingDTO dto = new HotelBookingDTO();
        dto.setId(booking.getId());
        dto.setBookingDate(booking.getBookingDate());
        dto.setStatus(booking.getStatus());
        dto.setGlobalBookingId(booking.getGlobalBookingId());

        if(booking.getHotel()!=null){
            HotelDTO hotelDTO = new HotelDTO();
            hotelDTO.setId(booking.getHotel().getId());
            hotelDTO.setName(booking.getHotel().getName());
            hotelDTO.setPhoneNumber(booking.getHotel().getPhoneNumber());
            hotelDTO.setPostcode(booking.getHotel().getPostcode());
            dto.setHotel(hotelDTO);
        }
        return dto;
    }
}
