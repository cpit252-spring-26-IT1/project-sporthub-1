package sa.edu.kau.fcit.cpit252.project.entities; 

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bookings") 
public class BookingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;
    private String fieldName;
    private double totalCost;
    private String description;
    private LocalDateTime bookingDate;
    private String paymentMethod;

    public BookingRecord() {}

    public BookingRecord(String userEmail, String fieldName, double totalCost, String description, String paymentMethod) {
        this.userEmail = userEmail;
        this.fieldName = fieldName;
        this.totalCost = totalCost;
        this.description = description;
        this.bookingDate = LocalDateTime.now();
        this.paymentMethod = paymentMethod;
    }

   
    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public String getFieldName() { return fieldName; }
    public double getTotalCost() { return totalCost; }
    public String getDescription() { return description; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public String getPaymentMethod() { return paymentMethod; }
}