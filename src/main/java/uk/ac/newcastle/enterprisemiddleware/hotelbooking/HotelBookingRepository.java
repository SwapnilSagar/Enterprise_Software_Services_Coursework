package uk.ac.newcastle.enterprisemiddleware.hotelbooking;

import uk.ac.newcastle.enterprisemiddleware.repository.CommonRepository;

import javax.enterprise.context.RequestScoped;

/**
 * @author Swapnil Sagar
 * */
@RequestScoped
public class HotelBookingRepository extends CommonRepository<HotelBooking, Long> {
    public HotelBookingRepository() {
        super(HotelBooking.class);
    }
}
