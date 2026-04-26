package sa.edu.kau.fcit.cpit252.project.patterns;

public class BasicBooking implements Booking {
    private String fieldName;
    private double basePrice;

    public BasicBooking(String fieldName, double basePrice) {
        this.fieldName = fieldName;
        this.basePrice = basePrice;
    }

    @Override
    public String getDescription() {
        return "Booking for: " + fieldName;
    }

    @Override
    public double getCost() {
        return basePrice;
    }
}