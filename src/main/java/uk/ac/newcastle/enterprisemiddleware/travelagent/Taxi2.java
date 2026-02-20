package uk.ac.newcastle.enterprisemiddleware.travelagent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 *
 * @author Swapnil Sagar
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Taxi2 {
    private long taxiId;
    private String registrationNumber;


    private int seatsCount;

    public Long getTaxiId() {
        return taxiId;
    }



    public void setTaxiId(Long taxiId) {
        this.taxiId = taxiId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public int getSeatsCount() {
        return seatsCount;
    }



    public void setSeatsCount(int seatsCount) {
        this.seatsCount = seatsCount;
    }

    @Override
    public String toString() {
        return "Taxi2{" +
                "id=" + taxiId +
                ", registration='" + registrationNumber + '\'' +
                ", noOfSeats=" + seatsCount +
                '}';
    }
}
