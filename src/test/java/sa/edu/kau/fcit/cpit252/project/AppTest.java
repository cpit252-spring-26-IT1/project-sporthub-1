package sa.edu.kau.fcit.cpit252.project;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.internet.MimeMessage;
import sa.edu.kau.fcit.cpit252.project.controllers.AuthController;
import sa.edu.kau.fcit.cpit252.project.controllers.BookingController;
import sa.edu.kau.fcit.cpit252.project.controllers.PageController;
import sa.edu.kau.fcit.cpit252.project.entities.BookingRecord;
import sa.edu.kau.fcit.cpit252.project.patterns.ApplePay;
import sa.edu.kau.fcit.cpit252.project.patterns.AuditManager;
import sa.edu.kau.fcit.cpit252.project.patterns.BasicBooking;
import sa.edu.kau.fcit.cpit252.project.patterns.Booking;
import sa.edu.kau.fcit.cpit252.project.patterns.BookingDecorator;
import sa.edu.kau.fcit.cpit252.project.patterns.CreditMada;
import sa.edu.kau.fcit.cpit252.project.patterns.EquipmentDecorator;
import sa.edu.kau.fcit.cpit252.project.patterns.Payment;
import sa.edu.kau.fcit.cpit252.project.patterns.RefereeDecorator;
import sa.edu.kau.fcit.cpit252.project.repositories.BookingRepository;
import sa.edu.kau.fcit.cpit252.project.repositories.UserRepository;
import sa.edu.kau.fcit.cpit252.project.services.confirmEmailMessage;

@ExtendWith(MockitoExtension.class)
public class AppTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private confirmEmailMessage emailService;

    @InjectMocks
    private BookingController bookingController;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthController authController;

    // ========================
    // BasicBooking Tests
    // ========================

    @Test
    public void testBasicBookingCost() {
        Booking booking = new BasicBooking("Football Field", 150.0);
        assertEquals(150.0, booking.getCost(), "The basic field cost should be 150.0");
        assertEquals("Booking for: Football Field", booking.getDescription());
    }

    @Test
    public void testBasicBookingDifferentValues() {
        Booking booking = new BasicBooking("Tennis Court", 200.0);
        assertEquals(200.0, booking.getCost());
        assertEquals("Booking for: Tennis Court", booking.getDescription());
    }

    // ========================
    // Decorator Pattern Tests
    // ========================

    @Test
    public void testRefereeDecoratorOnly() {
        Booking booking = new BasicBooking("Football Field", 150.0);
        booking = new RefereeDecorator(booking);
        assertEquals(200.0, booking.getCost(), "Base 150 + Referee 50 = 200");
        assertEquals("Booking for: Football Field + Referee", booking.getDescription());
    }

    @Test
    public void testEquipmentDecoratorOnly() {
        Booking booking = new BasicBooking("Football Field", 150.0);
        booking = new EquipmentDecorator(booking);
        assertEquals(180.0, booking.getCost(), "Base 150 + Equipment 30 = 180");
        assertEquals("Booking for: Football Field + Equipment Rental", booking.getDescription());
    }

    @Test
    public void testBookingWithAllDecorators() {
        Booking booking = new BasicBooking("Volleyball Field", 150.0);
        booking = new RefereeDecorator(booking);   // +50
        booking = new EquipmentDecorator(booking); // +30
        assertEquals(230.0, booking.getCost());
        assertEquals("Booking for: Volleyball Field + Referee + Equipment Rental", booking.getDescription());
    }

    @Test
    public void testBookingWithAllDecoratorsReversedOrder() {
        Booking booking = new BasicBooking("Padel Court", 100.0);
        booking = new EquipmentDecorator(booking); // +30
        booking = new RefereeDecorator(booking);   // +50
        assertEquals(180.0, booking.getCost());
        assertEquals("Booking for: Padel Court + Equipment Rental + Referee", booking.getDescription());
    }

    // ========================
    // Singleton - AuditManager Tests
    // ========================

    @Test
    public void testAuditManagerSingleton() {
        AuditManager instance1 = AuditManager.getInstance();
        AuditManager instance2 = AuditManager.getInstance();
        assertNotNull(instance1);
        assertSame(instance1, instance2, "Singleton pattern failed!");
    }

    @Test
    public void testAuditManagerLogAction() {
        AuditManager manager = AuditManager.getInstance();
        assertDoesNotThrow(() -> manager.logAction("user@test.com", "test event"));
    }

    @Test
    public void testAuditManagerLogBooking() {
        AuditManager manager = AuditManager.getInstance();
        assertDoesNotThrow(() -> manager.logBooking("user@test.com", "Football Field", "2026-05-20", "19:00", 200.0));
    }

    // ========================
    // Strategy - Payment Tests
    // ========================

    @Test
    public void testApplePayPayment() {
        Payment applePay = new ApplePay();
        assertDoesNotThrow(() -> applePay.pay(100.0));
    }

    @Test
    public void testCreditMadaPayment() {
        Payment creditMada = new CreditMada();
        assertDoesNotThrow(() -> creditMada.pay(200.0));
    }

    // ========================
    // BookingRecord Entity Tests
    // ========================

    @Test
    public void testBookingRecordParameterizedConstructorAndGetters() {
        BookingRecord record = new BookingRecord(
                "test@test.com", "Smash Arena", 100.0,
                "Desc", "APPLE_PAY", "2026-05-20", "19:00"
        );

        assertEquals("test@test.com", record.getUserEmail());
        assertEquals("Smash Arena", record.getFieldName());
        assertEquals(100.0, record.getTotalCost());
        assertEquals("Desc", record.getDescription());
        assertEquals("APPLE_PAY", record.getPaymentMethod());
        assertEquals("2026-05-20", record.getPlayDate());
        assertEquals("19:00", record.getPlayTime());
        assertNotNull(record.getBookingDate(), "bookingDate should be set by constructor");
    }

    @Test
    public void testBookingRecordDefaultConstructor() {
        BookingRecord record = new BookingRecord();
        assertNull(record.getId());
        assertNull(record.getUserEmail());
        assertNull(record.getFieldName());
        assertEquals(0.0, record.getTotalCost());
        assertNull(record.getDescription());
        assertNull(record.getPaymentMethod());
        assertNull(record.getPlayDate());
        assertNull(record.getPlayTime());
        assertNull(record.getBookingDate());
    }

    // ========================
    // User Entity Tests
    // ========================

    @Test
    public void testUserGettersAndSetters() {
        User user = new User();
        user.setId(1L);
        user.setName("Ali");
        user.setEmail("ali@test.com");
        user.setPassword("secret123");

        assertEquals(1L, user.getId());
        assertEquals("Ali", user.getName());
        assertEquals("ali@test.com", user.getEmail());
        assertEquals("secret123", user.getPassword());
    }

    @Test
    public void testUserDefaultValues() {
        User user = new User();
        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
    }

    // ========================
    // BookingController Tests
    // ========================

    @Test
    public void testMakeBooking_SuccessWithApplePay() {
        when(bookingRepository.existsByFieldNameAndPlayDateAndPlayTime(anyString(), anyString(), anyString()))
                .thenReturn(false);

        BookingRecord savedRecordMock = mock(BookingRecord.class);
        when(savedRecordMock.getId()).thenReturn(99L);
        when(savedRecordMock.getDescription()).thenReturn("Booking for: Padel Court + Referee");
        when(savedRecordMock.getTotalCost()).thenReturn(250.0);
        when(savedRecordMock.getPaymentMethod()).thenReturn("APPLE_PAY");

        when(bookingRepository.save(any(BookingRecord.class))).thenReturn(savedRecordMock);

        ResponseEntity<?> response = bookingController.makeBooking(
                "user@test.com", "Padel Court", 200.0, true, false, "APPLE_PAY", "2026-05-20", "17:00"
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("Success", body.get("status"));
        assertEquals(99L, body.get("bookingId"));

        // Verify email service was called with sendConfirmationEmail
        verify(emailService).sendConfirmationEmail(anyString(), anyString(), anyDouble());
    }

    @Test
    public void testMakeBooking_SuccessWithMadaCreditCard() {
        when(bookingRepository.existsByFieldNameAndPlayDateAndPlayTime(anyString(), anyString(), anyString()))
                .thenReturn(false);

        BookingRecord savedRecordMock = mock(BookingRecord.class);
        when(savedRecordMock.getId()).thenReturn(42L);
        when(savedRecordMock.getDescription()).thenReturn("Booking for: Tennis Court");
        when(savedRecordMock.getTotalCost()).thenReturn(100.0);
        when(savedRecordMock.getPaymentMethod()).thenReturn("MADA_CREDIT_CARD");

        when(bookingRepository.save(any(BookingRecord.class))).thenReturn(savedRecordMock);

        ResponseEntity<?> response = bookingController.makeBooking(
                "user@test.com", "Tennis Court", 100.0, false, false, "MADA_CREDIT_CARD", "2026-06-01", "10:00"
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("Success", body.get("status"));
        assertEquals(42L, body.get("bookingId"));
    }

    @Test
    public void testMakeBooking_WithBothDecorators() {
        when(bookingRepository.existsByFieldNameAndPlayDateAndPlayTime(anyString(), anyString(), anyString()))
                .thenReturn(false);

        BookingRecord savedRecordMock = mock(BookingRecord.class);
        when(savedRecordMock.getId()).thenReturn(77L);
        when(savedRecordMock.getDescription()).thenReturn("Booking for: Football Field + Referee + Equipment Rental");
        when(savedRecordMock.getTotalCost()).thenReturn(230.0);
        when(savedRecordMock.getPaymentMethod()).thenReturn("APPLE_PAY");

        when(bookingRepository.save(any(BookingRecord.class))).thenReturn(savedRecordMock);

        ResponseEntity<?> response = bookingController.makeBooking(
                "user@test.com", "Football Field", 150.0, true, true, "APPLE_PAY", "2026-05-20", "18:00"
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("Success", body.get("status"));
        assertEquals(230.0, body.get("totalCost"));
    }

    @Test
    public void testMakeBooking_DoubleBooking_Fails() {
        when(bookingRepository.existsByFieldNameAndPlayDateAndPlayTime(anyString(), anyString(), anyString()))
                .thenReturn(true);

        ResponseEntity<?> response = bookingController.makeBooking(
                "user@test.com", "Padel Court", 200.0, true, false, "APPLE_PAY", "2026-05-20", "17:00"
        );

        assertTrue(response.getStatusCode().is4xxClientError());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("Error", body.get("status"));
    }

    @Test
    public void testMakeBooking_InvalidPaymentMethod() {
        when(bookingRepository.existsByFieldNameAndPlayDateAndPlayTime(anyString(), anyString(), anyString()))
                .thenReturn(false);

        ResponseEntity<?> response = bookingController.makeBooking(
                "user@test.com", "Football Field", 150.0, false, false, "BITCOIN", "2026-05-20", "17:00"
        );

        assertTrue(response.getStatusCode().is4xxClientError());
        assertNull(response.getBody(), "Invalid payment method should return empty body");
    }

    @Test
    public void testCheckTimeBooked_ReturnsBookedTimes() {
        BookingRecord record1 = new BookingRecord("a@test.com", "Field A", 100.0, "desc", "APPLE_PAY", "2026-05-20", "10:00");
        BookingRecord record2 = new BookingRecord("b@test.com", "Field A", 100.0, "desc", "APPLE_PAY", "2026-05-20", "12:00");

        when(bookingRepository.findByFieldNameAndPlayDate("Field A", "2026-05-20"))
                .thenReturn(List.of(record1, record2));

        ResponseEntity<?> response = bookingController.checkTimeBooked("Field A", "2026-05-20");

        assertTrue(response.getStatusCode().is2xxSuccessful());

        @SuppressWarnings("unchecked")
        List<String> bookedTimes = (List<String>) response.getBody();
        assertNotNull(bookedTimes);
        assertEquals(2, bookedTimes.size());
        assertTrue(bookedTimes.contains("10:00"));
        assertTrue(bookedTimes.contains("12:00"));
    }

    @Test
    public void testCheckTimeBooked_ReturnsEmptyList() {
        when(bookingRepository.findByFieldNameAndPlayDate("Field B", "2026-06-01"))
                .thenReturn(Collections.emptyList());

        ResponseEntity<?> response = bookingController.checkTimeBooked("Field B", "2026-06-01");

        assertTrue(response.getStatusCode().is2xxSuccessful());

        @SuppressWarnings("unchecked")
        List<String> bookedTimes = (List<String>) response.getBody();
        assertNotNull(bookedTimes);
        assertTrue(bookedTimes.isEmpty());
    }

    // ========================
    // AuthController Tests
    // ========================

    @Test
    public void testRegister_Success() {
        User newUser = new User();
        newUser.setName("Ali");
        newUser.setEmail("ali@test.com");
        newUser.setPassword("pass123");

        when(userRepository.findByEmail("ali@test.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.register(newUser);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(userRepository).save(newUser);
    }

    @Test
    public void testRegister_DuplicateEmail_Fails() {
        User existingUser = new User();
        existingUser.setEmail("ali@test.com");

        when(userRepository.findByEmail("ali@test.com")).thenReturn(Optional.of(existingUser));

        User newUser = new User();
        newUser.setEmail("ali@test.com");

        ResponseEntity<?> response = authController.register(newUser);

        assertTrue(response.getStatusCode().is4xxClientError());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testLogin_Success() {
        User storedUser = new User();
        storedUser.setName("Ali");
        storedUser.setEmail("ali@test.com");
        storedUser.setPassword("pass123");

        when(userRepository.findByEmail("ali@test.com")).thenReturn(Optional.of(storedUser));

        User loginRequest = new User();
        loginRequest.setEmail("ali@test.com");
        loginRequest.setPassword("pass123");

        ResponseEntity<?> response = authController.login(loginRequest);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        String body = (String) response.getBody();
        assertNotNull(body);
        assertTrue(body.contains("success"));
        assertTrue(body.contains("Ali"));
    }

    @Test
    public void testLogin_WrongPassword_Fails() {
        User storedUser = new User();
        storedUser.setName("Ali");
        storedUser.setEmail("ali@test.com");
        storedUser.setPassword("pass123");

        when(userRepository.findByEmail("ali@test.com")).thenReturn(Optional.of(storedUser));

        User loginRequest = new User();
        loginRequest.setEmail("ali@test.com");
        loginRequest.setPassword("wrongpass");

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    public void testLogin_UserNotFound_Fails() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        User loginRequest = new User();
        loginRequest.setEmail("unknown@test.com");
        loginRequest.setPassword("anypass");

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(401, response.getStatusCode().value());
    }

    // ========================
    // PageController Tests
    // ========================

    @Test
    public void testShowHomePage() {
        PageController pageController = new PageController();
        assertEquals("index", pageController.showHomePage());
    }

    @Test
    public void testShowLoginPage() {
        PageController pageController = new PageController();
        assertEquals("login", pageController.showLoginPage());
    }

    // ========================
    // confirmEmailMessage Service Test
    // ========================

    @Test
    public void testConfirmEmailMessageSendConfirmation() {
        // The actual sendConfirmationEmail uses @Autowired JavaMailSender which
        // will be null in a unit test context, so the catch block will handle it.
        confirmEmailMessage emailMsg = new confirmEmailMessage();
        assertDoesNotThrow(() -> emailMsg.sendConfirmationEmail("test@example.com", "Test Booking", 150.0));
    }

    // ========================
    // Additional edge-case tests for better coverage
    // ========================

    @Test
    public void testMakeBooking_WithEquipmentOnly() {
        when(bookingRepository.existsByFieldNameAndPlayDateAndPlayTime(anyString(), anyString(), anyString()))
                .thenReturn(false);

        BookingRecord savedRecordMock = mock(BookingRecord.class);
        when(savedRecordMock.getId()).thenReturn(55L);
        when(savedRecordMock.getDescription()).thenReturn("Booking for: Court + Equipment Rental");
        when(savedRecordMock.getTotalCost()).thenReturn(180.0);
        when(savedRecordMock.getPaymentMethod()).thenReturn("APPLE_PAY");

        when(bookingRepository.save(any(BookingRecord.class))).thenReturn(savedRecordMock);

        ResponseEntity<?> response = bookingController.makeBooking(
                "user@test.com", "Court", 150.0, false, true, "APPLE_PAY", "2026-07-01", "09:00"
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testMakeBooking_NoDecoratorsWithMada() {
        when(bookingRepository.existsByFieldNameAndPlayDateAndPlayTime(anyString(), anyString(), anyString()))
                .thenReturn(false);

        BookingRecord savedRecordMock = mock(BookingRecord.class);
        when(savedRecordMock.getId()).thenReturn(88L);
        when(savedRecordMock.getDescription()).thenReturn("Booking for: Swimming Pool");
        when(savedRecordMock.getTotalCost()).thenReturn(120.0);
        when(savedRecordMock.getPaymentMethod()).thenReturn("MADA_CREDIT_CARD");

        when(bookingRepository.save(any(BookingRecord.class))).thenReturn(savedRecordMock);

        ResponseEntity<?> response = bookingController.makeBooking(
                "user@test.com", "Swimming Pool", 120.0, false, false, "MADA_CREDIT_CARD", "2026-08-15", "14:00"
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("MADA_CREDIT_CARD", body.get("paymentMethod"));
    }

    @Test
    public void testBookingRecordGetId() {
        BookingRecord record = new BookingRecord();
        assertNull(record.getId(), "ID should be null for unsaved record");
    }

    // ========================
    // BookingDecorator base class Tests
    // ========================

    @Test
    public void testBookingDecoratorBaseGetDescription() {
        // Create a concrete anonymous subclass that does NOT override getDescription
        // to test the base BookingDecorator delegation
        Booking base = new BasicBooking("Test Field", 100.0);
        BookingDecorator decorator = new BookingDecorator(base) {
            // Don't override getDescription or getCost — use base implementations
        };
        // The base BookingDecorator.getDescription() should delegate to the wrapped booking
        assertEquals("Booking for: Test Field", decorator.getDescription());
        assertEquals(100.0, decorator.getCost());
    }

    @Test
    public void testBookingDecoratorBaseGetCost() {
        Booking base = new BasicBooking("Court", 250.0);
        BookingDecorator decorator = new BookingDecorator(base) {};
        assertEquals(250.0, decorator.getCost());
    }

    // ========================
    // confirmEmailMessage with mocked JavaMailSender Tests
    // ========================

    @Test
    public void testSendConfirmationEmail_Success() throws Exception {
        // Use reflection to inject a mocked JavaMailSender into confirmEmailMessage
        confirmEmailMessage emailMsg = new confirmEmailMessage();
        JavaMailSender mockMailSender = mock(JavaMailSender.class);
        MimeMessage mockMimeMessage = mock(MimeMessage.class);

        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);

        // Inject mock via reflection since @Autowired field is private
        java.lang.reflect.Field mailSenderField = confirmEmailMessage.class.getDeclaredField("mailSender");
        mailSenderField.setAccessible(true);
        mailSenderField.set(emailMsg, mockMailSender);

        assertDoesNotThrow(() -> emailMsg.sendConfirmationEmail("test@example.com", "Test Booking", 150.0));

        // Verify the email was sent
        verify(mockMailSender).send(any(MimeMessage.class));
    }

    @Test
    public void testSendConfirmationEmail_ExceptionHandled() {
        // Without mailSender injected, the catch block should handle the exception gracefully
        confirmEmailMessage emailMsg = new confirmEmailMessage();
        assertDoesNotThrow(() -> emailMsg.sendConfirmationEmail("test@example.com", "Test Booking", 150.0));
    }
}
