import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private String accountNumber;
    private String name;
    private String dob;
    private String address;
    private String phone;
    private String email;
    private String accountType;
    private BigDecimal balance;
    private String pin;
    private boolean active;
    private boolean locked;
    private ArrayList<Transaction> transactions;

    public Account(String accountNumber, String name, String dob,
                   String address, String phone, String email,
                   String accountType, BigDecimal balance, String pin) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.dob = dob;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.accountType = accountType;
        this.balance = balance;
        this.pin = pin;
        this.active = true;
        this.locked = false;
        this.transactions = new ArrayList<>();
    }

    public String getAccountNumber() { return accountNumber; }
    public String getName() { return name; }
    public String getDob() { return dob; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAccountType() { return accountType; }
    public BigDecimal getBalance() { return balance; }
    public String getPin() { return pin; }
    public boolean isActive() { return active; }
    public boolean isLocked() { return locked; }
    public ArrayList<Transaction> getTransactions() { return transactions; }

    public void setName(String name) { this.name = name; }
    public void setDob(String dob) { this.dob = dob; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setPin(String pin) { this.pin = pin; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setActive(boolean active) { this.active = active; }
    public void setLocked(boolean locked) { this.locked = locked; }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }
}
