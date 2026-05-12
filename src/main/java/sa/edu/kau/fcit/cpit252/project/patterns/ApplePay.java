package sa.edu.kau.fcit.cpit252.project.patterns;

public class ApplePay implements Payment {
    public void pay(double amount) {
        //assuming we have some logic to process Apple Pay payment here
        System.out.println("Processing Apple Pay payment of $" + amount);
    }
}
