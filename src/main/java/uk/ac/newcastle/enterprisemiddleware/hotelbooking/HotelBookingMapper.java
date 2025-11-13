package uk.ac.newcastle.enterprisemiddleware.hotelbooking;

import uk.ac.newcastle.enterprisemiddleware.hotel.HotelPayload;

/**
 * @author Swapnil Sagar
 * */
public class HotelBookingMapper {

    public static HotelBookingPayload toDTO(HotelBooking booking){
        HotelBookingPayload dto = new HotelBookingPayload();
        dto.setId(booking.getId());
        dto.setBookingDate(booking.getBookingDate());
        dto.setStatus(booking.getStatus());
        dto.setGlobalBookingId(booking.getGlobalBookingId());

        if(booking.getHotel()!=null){
            HotelPayload hotelPayload = new HotelPayload();
            hotelPayload.setId(booking.getHotel().getId());
            hotelPayload.setName(booking.getHotel().getName());
            hotelPayload.setPhoneNumber(booking.getHotel().getPhoneNumber());
            hotelPayload.setPostcode(booking.getHotel().getPostcode());
            dto.setHotel(hotelPayload);
        }
        return dto;
    }
}
