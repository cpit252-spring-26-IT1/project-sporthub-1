package sa.edu.kau.fcit.cpit252.project.controllers; 

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sa.edu.kau.fcit.cpit252.project.patterns.BasicBooking;
import sa.edu.kau.fcit.cpit252.project.patterns.Booking;
import sa.edu.kau.fcit.cpit252.project.patterns.EquipmentDecorator;
import sa.edu.kau.fcit.cpit252.project.patterns.RefereeDecorator;

@RestController
@RequestMapping("/api/booking")
public class BookingController {


    @PostMapping("/make")
    public ResponseEntity<Map<String, Object>> makeBooking(
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


        Map<String, Object> receipt = Map.of(
            "status", "Success",
            "finalDescription", myBooking.getDescription(),
            "totalCost", myBooking.getCost()
        );

        return ResponseEntity.ok(receipt);
    }
}