package sa.edu.kau.fcit.cpit252.project.patterns;

public class EquipmentDecorator extends BookingDecorator {
    public EquipmentDecorator(Booking newBooking) {
        super(newBooking);
    }

    @Override
    public String getDescription() {
        return decoratedBooking.getDescription() + " + Equipment Rental";
    }

    @Override
    public double getCost() {
        return decoratedBooking.getCost() + 30.0; // Assuming a fixed cost for equipment rental
    }

}