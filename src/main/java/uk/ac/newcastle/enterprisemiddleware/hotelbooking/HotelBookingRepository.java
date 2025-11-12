package uk.ac.newcastle.enterprisemiddleware.hotelbooking;

import uk.ac.newcastle.enterprisemiddleware.repository.GenricRepository;

import javax.enterprise.context.RequestScoped;

/**
 * @author Mayank Kunwar
 * */
@RequestScoped
public class HotelBookingRepository extends GenricRepository<HotelBooking, Long> {
    public HotelBookingRepository() {
        super(HotelBooking.class);
    }
}
