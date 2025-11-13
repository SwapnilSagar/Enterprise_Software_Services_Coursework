package uk.ac.newcastle.enterprisemiddleware.dto.customer;

import uk.ac.newcastle.enterprisemiddleware.agent.BookingEntity;
import uk.ac.newcastle.enterprisemiddleware.agent.Flight;
import uk.ac.newcastle.enterprisemiddleware.agent.GlobalBooking;
import uk.ac.newcastle.enterprisemiddleware.agent.Taxi;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.hotel.Hotel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Swapnil Sagar
 * */
public class CustomerMapper {

    public static Customer toCustomer(CustomerDTO dto){
        Customer customer = new Customer();
        customer.setId(dto.getId());
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhoneNumber(dto.getPhoneNumber());
        return customer;
    }

    public static CustomerDTO toDTO(Customer customer){
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setPhoneNumber(customer.getPhoneNumber());
        if (customer.getBookings() != null) {
            List<CustomerBookMapDTO> bookingDTOs = customer.getBookings().stream()
                    .map(CustomerMapper::toBookingDTO)
                    .collect(Collectors.toList());
            dto.setBookings(bookingDTOs);
        }
        return dto;
    }

    private static CustomerBookMapDTO toBookingDTO(GlobalBooking booking) {
        CustomerBookMapDTO dto = new CustomerBookMapDTO();
        dto.setId(booking.getId());
        dto.setBookingDate(booking.getBookingDate());

        if (booking.getBookingEntity() != null){
            BookingEntity bookingEntity = new BookingEntity();

            if (booking.getBookingEntity().getHotel() != null) {
                Hotel bHotel = booking.getBookingEntity().getHotel();
                Hotel hotel = new Hotel();
                hotel.setId(bHotel.getId());
                hotel.setName(bHotel.getName());
                hotel.setPhoneNumber(bHotel.getPhoneNumber());
                hotel.setPostcode(bHotel.getPostcode());
                bookingEntity.setHotel(bHotel);
            }
            if (booking.getBookingEntity().getFlight() != null) {
                Flight bFlight = booking.getBookingEntity().getFlight();
                Flight flight = new Flight();
                flight.setId(bFlight.getId());
                flight.setFlightNumber(bFlight.getFlightNumber());
                flight.setDeparture(bFlight.getDeparture());
                flight.setDestination(bFlight.getDestination());
                bookingEntity.setFlight(flight);
            }
            if (booking.getBookingEntity().getTaxi() != null) {
                Taxi bTaxi = booking.getBookingEntity().getTaxi();
                Taxi taxi = new Taxi();
                taxi.setId(bTaxi.getId());
                taxi.setRegistration(bTaxi.getRegistration());
                taxi.setNoOfSeats(bTaxi.getNoOfSeats());
                bookingEntity.setTaxi(taxi);
            }
            dto.setBookingEntity(bookingEntity);
        }

        return dto;
    }
}
