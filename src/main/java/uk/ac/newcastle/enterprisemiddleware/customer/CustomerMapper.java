package uk.ac.newcastle.enterprisemiddleware.customer;

import uk.ac.newcastle.enterprisemiddleware.DTO.CustomerBookMapDTO;
import uk.ac.newcastle.enterprisemiddleware.travelagent.BookingEntity;
import uk.ac.newcastle.enterprisemiddleware.travelagent.GlobalBooking;
import uk.ac.newcastle.enterprisemiddleware.travelagent.client.response.HotelBookingResponse;
import uk.ac.newcastle.enterprisemiddleware.travelagent.client.response.Taxi2BookingResponse;
import uk.ac.newcastle.enterprisemiddleware.travelagent.client.response.TaxiBookingResponse;
import uk.ac.newcastle.enterprisemiddleware.util.JsonUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Swapnil Sagar
 * */

public class CustomerMapper
{

    public static Customer toCustomer(CustomerPayload dto){
        Customer customer = new Customer();
        customer.setId(dto.getId());
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhoneNumber(dto.getPhoneNumber());
        return customer;
    }

    public static CustomerPayload toDTO(Customer customer){
        CustomerPayload dto = new CustomerPayload();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setPhoneNumber(customer.getPhoneNumber());
        if (customer.getBookings() != null)
        {
            List<CustomerBookMapDTO> bookingDTOs = customer.getBookings().stream()
                    .map(CustomerMapper::toBookingDTO)
                    .collect(Collectors.toList());
            dto.setBookings(bookingDTOs);
        }
        return dto;
    }

    private static CustomerBookMapDTO toBookingDTO(GlobalBooking booking)
    {
        CustomerBookMapDTO dto = new CustomerBookMapDTO();
        dto.setId(booking.getId());
        dto.setBookingDate(booking.getBookingDate());

        if (booking.getBookingEntity() != null){
            BookingEntity bookingEntity = new BookingEntity();

            if (booking.getBookingEntity().getTaxi() != null)
            {

                TaxiBookingResponse bTaxi = JsonUtils.fromJson(JsonUtils.toJson(booking.getBookingEntity().getTaxi()), TaxiBookingResponse.class);
                bookingEntity.setTaxi(bTaxi);

            }
            if (booking.getBookingEntity().getTaxi2() != null) {
                Taxi2BookingResponse bTaxi2 = JsonUtils.fromJson(JsonUtils.toJson(booking.getBookingEntity().getTaxi2()), Taxi2BookingResponse.class);
                bookingEntity.setTaxi2(bTaxi2);
            }
            if (booking.getBookingEntity().getHotel() != null)
            {
                HotelBookingResponse bHotel = JsonUtils.fromJson(JsonUtils.toJson(booking.getBookingEntity().getHotel()), HotelBookingResponse.class);
                bookingEntity.setHotel(bHotel);

            }
            dto.setBookingEntity(bookingEntity);
        }

        return dto;
    }
}
