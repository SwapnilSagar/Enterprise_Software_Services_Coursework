package uk.ac.newcastle.enterprisemiddleware.agent;

import uk.ac.newcastle.enterprisemiddleware.repository.GenricRepository;

import javax.enterprise.context.RequestScoped;

/**
 * @author Mayank Kunwar
 * */
@RequestScoped
public class GlobalBookingRepository  extends GenricRepository<GlobalBooking, String> {
    public GlobalBookingRepository() {
        super(GlobalBooking.class);
    }
}
