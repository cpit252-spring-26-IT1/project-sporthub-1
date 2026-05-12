package sa.edu.kau.fcit.cpit252.project.controllers; 

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sa.edu.kau.fcit.cpit252.project.entities.BookingRecord;
import sa.edu.kau.fcit.cpit252.project.patterns.ApplePay;
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

    @PostMapping("/make")
    public ResponseEntity<Map<String, Object>> makeBooking(
            @RequestParam String email,
            @RequestParam String fieldName, 
            @RequestParam double basePrice,
            @RequestParam boolean addReferee, 
            @RequestParam boolean addEquipment,
            @RequestParam String paymentMethod) {


        Booking myBooking = new BasicBooking(fieldName, basePrice);


        if (addReferee) {
            myBooking = new RefereeDecorator(myBooking);
        }

        if (addEquipment) {
            myBooking = new EquipmentDecorator(myBooking);
        }

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
            paymentMethod
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

        return ResponseEntity.ok(receipt);
    }
}