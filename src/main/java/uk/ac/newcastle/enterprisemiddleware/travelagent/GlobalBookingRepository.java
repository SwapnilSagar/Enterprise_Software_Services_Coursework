package uk.ac.newcastle.enterprisemiddleware.travelagent;

import uk.ac.newcastle.enterprisemiddleware.repository.CommonRepository;

import javax.enterprise.context.RequestScoped;

/**
 * @author Swapnil Sagar
 * */
@RequestScoped
public class GlobalBookingRepository  extends CommonRepository<GlobalBooking, String> {
    public GlobalBookingRepository() {
        super(GlobalBooking.class);
    }
}
