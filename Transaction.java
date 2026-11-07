import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private String transactionId;
    private String accountNumber;
    private String type;
    private BigDecimal amount;
    private LocalDateTime dateTime;
    private String description;

    public Transaction(String transactionId, String accountNumber,
                       String type, BigDecimal amount, String description) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.dateTime = LocalDateTime.now();
    }

    public String getTransactionId() { return transactionId; }
    public String getAccountNumber() { return accountNumber; }
    public String getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return transactionId + " | " + type + " | Rs. " + amount
                + " | " + dateTime.format(format) + " | " + description;
    }
}
