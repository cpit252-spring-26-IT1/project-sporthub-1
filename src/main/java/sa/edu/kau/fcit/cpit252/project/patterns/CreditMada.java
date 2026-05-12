package sa.edu.kau.fcit.cpit252.project.patterns;


public class CreditMada implements Payment {
    @Override
    public void pay(double amount) {
        //assuming we have some logic to process Mada/CreditCard payment here
        System.out.println("Processing Mada/CreditCard payment of $" + amount);
    }
}