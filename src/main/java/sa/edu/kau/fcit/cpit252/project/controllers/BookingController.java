package sa.edu.kau.fcit.cpit252.project.controllers; 

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sa.edu.kau.fcit.cpit252.project.entities.BookingRecord;
import sa.edu.kau.fcit.cpit252.project.patterns.ApplePay;
import sa.edu.kau.fcit.cpit252.project.patterns.AuditManager;
import sa.edu.kau.fcit.cpit252.project.patterns.BasicBooking;
import sa.edu.kau.fcit.cpit252.project.patterns.Booking;
import sa.edu.kau.fcit.cpit252.project.patterns.CreditMada;
import sa.edu.kau.fcit.cpit252.project.patterns.EquipmentDecorator;
import sa.edu.kau.fcit.cpit252.project.patterns.Payment;
import sa.edu.kau.fcit.cpit252.project.patterns.RefereeDecorator;
import sa.edu.kau.fcit.cpit252.project.repositories.BookingRepository;
import sa.edu.kau.fcit.cpit252.project.services.confirmEmailMessage;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private confirmEmailMessage emailService;

    @GetMapping("timebooked")
    public ResponseEntity<?> checkTimeBooked(@RequestParam String fieldName, @RequestParam String playDate) {
        List<BookingRecord> bookings = bookingRepository.findByFieldNameAndPlayDate(fieldName, playDate);
        List<String> bookedTimes = bookings.stream()
                .map(BookingRecord::getPlayTime)
                .collect(Collectors.toList());

        return ResponseEntity.ok(bookedTimes);
    }
    @PostMapping("/make")
    public ResponseEntity<Map<String, Object>> makeBooking(
            @RequestParam String email,
            @RequestParam String fieldName, 
            @RequestParam double basePrice,
            @RequestParam boolean addReferee, 
            @RequestParam boolean addEquipment,
            @RequestParam String paymentMethod,
            @RequestParam String playDate,
            @RequestParam String playTime) {


        Booking myBooking = new BasicBooking(fieldName, basePrice);

        //check if the selected time slot is already booked
        if (bookingRepository.existsByFieldNameAndPlayDateAndPlayTime(fieldName, playDate, playTime)) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "Error",
                "message", "The selected time slot is already booked. Please choose a different time."
            ));
        }

        //Decorate the booking based on user selections
        if (addReferee) {
            myBooking = new RefereeDecorator(myBooking);
        }

        if (addEquipment) {
            myBooking = new EquipmentDecorator(myBooking);
        }

        // Process payment
        Payment paymentProcessor;
        if (paymentMethod.equalsIgnoreCase("APPLE_PAY")) {
            paymentProcessor = new ApplePay();
        } else if (paymentMethod.equalsIgnoreCase("MADA_CREDIT_CARD")) {
            paymentProcessor = new CreditMada();
        } else {
            return ResponseEntity.badRequest().build();
        }
        double totalAmount = myBooking.getCost();
        paymentProcessor.pay(totalAmount);

        BookingRecord newRecord = new BookingRecord(
            email, 
            fieldName, 
            myBooking.getCost(), 
            myBooking.getDescription(),
            paymentMethod,
            playDate,
            playTime
        );
        BookingRecord savedRecord = bookingRepository.save(newRecord);
        emailService.sendConfirmationEmail(email, myBooking.getDescription(), myBooking.getCost());

        Map<String, Object> receipt = Map.of(
            "status", "Success",
            "bookingId", savedRecord.getId(), 
            "finalDescription", savedRecord.getDescription(),
            "totalCost", savedRecord.getTotalCost(),
            "paymentMethod", savedRecord.getPaymentMethod()
        );
        AuditManager.getInstance().logBooking(email, fieldName, playDate, playTime, totalAmount);
        return ResponseEntity.ok(receipt);
    }
}