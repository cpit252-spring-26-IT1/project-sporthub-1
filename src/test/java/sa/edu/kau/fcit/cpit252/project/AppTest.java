package sa.edu.kau.fcit.cpit252.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import sa.edu.kau.fcit.cpit252.project.patterns.ApplePay;
import sa.edu.kau.fcit.cpit252.project.patterns.AuditManager;
import sa.edu.kau.fcit.cpit252.project.patterns.BasicBooking;
import sa.edu.kau.fcit.cpit252.project.patterns.Booking;
import sa.edu.kau.fcit.cpit252.project.patterns.CreditMada;
import sa.edu.kau.fcit.cpit252.project.patterns.EquipmentDecorator;
import sa.edu.kau.fcit.cpit252.project.patterns.Payment;
import sa.edu.kau.fcit.cpit252.project.patterns.RefereeDecorator;

/**
 * Unit test for simple App.
 */
public class AppTest {

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    //----- Decorator Pattern Tests -----
    @Test
    public void testBasicBookingCost() {
        Booking booking = new BasicBooking("Football Field", 150.0);
        assertEquals(150.0, booking.getCost(), "The basic field cost should be 150.0");
        assertEquals("Booking for: Football Field", booking.getDescription());
    }

    @Test
    public void testBookingWithAllDecorators() {
        Booking booking = new BasicBooking("Volleyball Field", 150.0);
        booking = new RefereeDecorator(booking); // +50
        booking = new EquipmentDecorator(booking); // +30
        
        assertEquals(230.0, booking.getCost(), "The total cost with referee and equipment should be 230.0");
    }

    // -------- Singleton & Strategy Pattern Tests --------
    @Test
    public void testAuditManagerSingleton() {
        // Request two instances of AuditManager simultaneously
        AuditManager instance1 = AuditManager.getInstance();
        AuditManager instance2 = AuditManager.getInstance();

        // Verify that they are not null
        assertNotNull(instance1, "The first instance should not be null");
        
        // The key here: assertSame ensures both refer to the exact same object in memory
        assertSame(instance1, instance2, "Singleton pattern failed! The system must always return the exact same instance");
    }
    
    @Test
    public void testApplePayStrategyInstantiation() {
        // Verify that Apple Pay strategy can be instantiated as a payment processor
        Payment payment = new ApplePay();
        assertNotNull(payment, "Apple Pay strategy object should be instantiated successfully");
    }

    @Test
    public void testCreditMadaStrategyInstantiation() {
        // Verify that Credit/Mada strategy can be instantiated as a payment processor
        Payment payment = new CreditMada();
        assertNotNull(payment, "CreditMada strategy object should be instantiated successfully");
    }
}
