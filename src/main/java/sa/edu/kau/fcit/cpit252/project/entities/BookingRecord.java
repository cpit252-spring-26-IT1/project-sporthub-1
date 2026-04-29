package sa.edu.kau.fcit.cpit252.project.entities; 

import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    public BookingRecord() {}

    public BookingRecord(String userEmail, String fieldName, double totalCost, String description) {
        this.userEmail = userEmail;
        this.fieldName = fieldName;
        this.totalCost = totalCost;
        this.description = description;
        this.bookingDate = LocalDateTime.now();
    }

   
    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public String getFieldName() { return fieldName; }
    public double getTotalCost() { return totalCost; }
    public String getDescription() { return description; }
    public LocalDateTime getBookingDate() { return bookingDate; }
}