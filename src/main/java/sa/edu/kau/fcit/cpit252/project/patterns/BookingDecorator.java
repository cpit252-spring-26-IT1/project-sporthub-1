package sa.edu.kau.fcit.cpit252.project.patterns;

public abstract class BookingDecorator implements Booking {
    protected Booking decoratedBooking;

    public BookingDecorator(Booking decoratedBooking) {
        this.decoratedBooking = decoratedBooking;
    }

    @Override
    public String getDescription() {
        return decoratedBooking.getDescription();
    }

    @Override
    public double getCost() {
        return decoratedBooking.getCost();
    }

}