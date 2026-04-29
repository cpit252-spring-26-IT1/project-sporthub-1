package sa.edu.kau.fcit.cpit252.project.controllers; 

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;
import sa.edu.kau.fcit.cpit252.project.patterns.*;

import sa.edu.kau.fcit.cpit252.project.entities.BookingRecord;
import sa.edu.kau.fcit.cpit252.project.patterns.BasicBooking;
import sa.edu.kau.fcit.cpit252.project.patterns.Booking;
import sa.edu.kau.fcit.cpit252.project.patterns.EquipmentDecorator;
import sa.edu.kau.fcit.cpit252.project.patterns.RefereeDecorator;
import sa.edu.kau.fcit.cpit252.project.repositories.BookingRepository;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;


    @PostMapping("/make")
    public ResponseEntity<Map<String, Object>> makeBooking(
            @RequestParam String email,
            @RequestParam String fieldName, 
            @RequestParam double basePrice,
            @RequestParam boolean addReferee, 
            @RequestParam boolean addEquipment) {


        Booking myBooking = new BasicBooking(fieldName, basePrice);


        if (addReferee) {
            myBooking = new RefereeDecorator(myBooking);
        }

        if (addEquipment) {
            myBooking = new EquipmentDecorator(myBooking);
        }


        BookingRecord newRecord = new BookingRecord(
            email, 
            fieldName, 
            myBooking.getCost(), 
            myBooking.getDescription()
        );
        BookingRecord savedRecord = bookingRepository.save(newRecord);

        Map<String, Object> receipt = Map.of(
            "status", "Success",
            "bookingId", savedRecord.getId(), 
            "finalDescription", savedRecord.getDescription(),
            "totalCost", savedRecord.getTotalCost()
        );

        return ResponseEntity.ok(receipt);
    }
}