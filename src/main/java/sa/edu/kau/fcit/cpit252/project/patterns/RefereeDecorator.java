package sa.edu.kau.fcit.cpit252.project.patterns;

public class RefereeDecorator extends BookingDecorator {
    public RefereeDecorator(Booking newBooking) {
        super(newBooking);
    }

    @Override
    public String getDescription() {
        return decoratedBooking.getDescription() + " + Referee";
    }

    @Override
    public double getCost() {
        return decoratedBooking.getCost() + 50.0; // Assuming a fixed cost for the referee
    }

}