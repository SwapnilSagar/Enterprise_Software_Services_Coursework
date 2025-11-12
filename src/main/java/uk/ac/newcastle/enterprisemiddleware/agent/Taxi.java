package uk.ac.newcastle.enterprisemiddleware.agent;


public class Taxi {
    private Long id;

    private String registration;

    private Integer noOfSeats;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public Integer getNoOfSeats() {
        return noOfSeats;
    }

    public void setNoOfSeats(Integer noOfSeats) {
        this.noOfSeats = noOfSeats;
    }

    @Override
    public String toString() {
        return "Taxi{" +
                "id=" + id +
                ", registration='" + registration + '\'' +
                ", noOfSeats=" + noOfSeats +
                '}';
    }
}
