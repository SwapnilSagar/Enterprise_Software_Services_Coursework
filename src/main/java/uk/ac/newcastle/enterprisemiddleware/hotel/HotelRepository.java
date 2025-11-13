package uk.ac.newcastle.enterprisemiddleware.hotel;

import uk.ac.newcastle.enterprisemiddleware.repository.GenricRepository;

import javax.enterprise.context.RequestScoped;

/**
 * @author Swapnil Sagar
 * */
@RequestScoped
public class HotelRepository extends GenricRepository<Hotel, Long> {
    public HotelRepository() {
        super(Hotel.class);
    }
}
