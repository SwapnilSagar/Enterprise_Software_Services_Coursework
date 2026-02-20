package uk.ac.newcastle.enterprisemiddleware.hotel;

import uk.ac.newcastle.enterprisemiddleware.repository.CommonRepository;

import javax.enterprise.context.RequestScoped;

/**
 * @author Swapnil Sagar
 * */
@RequestScoped
public class HotelRepository extends CommonRepository<Hotel, Long> {
    public HotelRepository() {
        super(Hotel.class);
    }
}
