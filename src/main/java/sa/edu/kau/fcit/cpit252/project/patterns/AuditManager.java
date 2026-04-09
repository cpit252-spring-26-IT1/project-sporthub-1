package sa.edu.kau.fcit.cpit252.project.patterns;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditManager {

    private static AuditManager instance;
    private int totalActionsLogged;

    private AuditManager() {
        this.totalActionsLogged = 0;
        System.out.println("AuditManager started in memory.");
    }

    public static synchronized AuditManager getInstance() {
        if (instance == null) {
            instance = new AuditManager();
        }
        return instance;
    }

    public void logAction(String userEmail, String action) {
        this.totalActionsLogged++;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = LocalDateTime.now().format(formatter);
        System.out.println("[" + time + "] (Action #" + this.totalActionsLogged + ") User: " + userEmail + " - " + action);
    }
}