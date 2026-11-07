import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class Bank {
    private final ArrayList<Account> accounts;
    private static final BigDecimal SAVINGS_MINIMUM = new BigDecimal("500.00");
    private static final BigDecimal DAILY_WITHDRAWAL_LIMIT = new BigDecimal("10000.00");
    private static final BigDecimal MONTHLY_INTEREST_RATE = new BigDecimal("0.03");

    public Bank() {
        accounts = FileManager.loadAccounts();
    }

    public ArrayList<Account> getAllAccounts() {
        return accounts;
    }

    public BigDecimal getMinimumBalance(String accountType) {
        return accountType.equalsIgnoreCase("SAVINGS")
                ? SAVINGS_MINIMUM : BigDecimal.ZERO;
    }

    public BigDecimal getDailyWithdrawalLimit() {
        return DAILY_WITHDRAWAL_LIMIT;
    }

    private void validatePositiveAmount(BigDecimal amount) throws CustomException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException("Please enter a valid amount greater than zero.");
        }
    }

    public void createAccount(String number, String name, String dob,
                              String address, String phone, String email,
                              String type, BigDecimal openingBalance, String pin)
            throws CustomException {
        if (number == null || !number.matches("[0-9]{6,18}"))
            throw new CustomException("Account number must contain 6 to 18 digits.");
        if (findAccountQuietly(number) != null)
            throw new CustomException("Account Already Exists!");
        if (name == null || name.trim().isEmpty())
            throw new CustomException("Customer name cannot be empty.");
        if (phone == null || !phone.matches("[0-9]{10}"))
            throw new CustomException("Mobile number must contain 10 digits.");
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
            throw new CustomException("Please enter a valid email address.");
        if (!type.equalsIgnoreCase("SAVINGS") && !type.equalsIgnoreCase("CURRENT"))
            throw new CustomException("Account type must be SAVINGS or CURRENT.");
        if (pin == null || !pin.matches("[0-9]{4}"))
            throw new CustomException("PIN must contain exactly 4 digits.");
        validatePositiveAmount(openingBalance);

        BigDecimal minimum = getMinimumBalance(type);
        if (openingBalance.compareTo(minimum) < 0)
            throw new CustomException("Minimum opening balance is Rs. " + minimum);

        Account account = new Account(number, name.trim(), dob, address, phone,
                email, type.toUpperCase(), openingBalance.setScale(2, RoundingMode.HALF_UP), pin);
        account.addTransaction(new Transaction(makeTransactionId(), number,
                "OPENING", openingBalance, "Opening balance"));
        accounts.add(account);
    }

    private Account findAccountQuietly(String number) {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(number)) return account;
        }
        return null;
    }

    public Account findAccount(String number) throws CustomException {
        Account account = findAccountQuietly(number);
        if (account == null || !account.isActive())
            throw new CustomException("Account Not Available!");
        return account;
    }

    public boolean verifyPin(String number, String pin) throws CustomException {
        Account account = findAccount(number);
        if (account.isLocked())
            throw new CustomException("This account is locked. Contact the bank administrator.");
        if (!account.getPin().equals(pin))
            throw new CustomException("Invalid PIN!");
        return true;
    }

    public void updateCustomerDetails(String number, String name, String address,
                                      String phone, String email) throws CustomException {
        Account account = findAccount(number);
        if (name == null || name.trim().isEmpty())
            throw new CustomException("Name cannot be empty.");
        if (phone == null || !phone.matches("[0-9]{10}"))
            throw new CustomException("Mobile number must contain 10 digits.");
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
            throw new CustomException("Invalid email address.");
        account.setName(name.trim());
        account.setAddress(address);
        account.setPhone(phone);
        account.setEmail(email);
    }

    public void closeAccount(String number) throws CustomException {
        Account account = findAccount(number);
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0)
            throw new CustomException("Please withdraw or transfer the remaining balance before closing.");
        account.setActive(false);
    }

    public void deposit(String number, BigDecimal amount) throws CustomException {
        validatePositiveAmount(amount);
        Account account = findAccount(number);
        account.setBalance(account.getBalance().add(amount));
        addTransaction(account, "DEPOSIT", amount, "Cash deposit");
    }

    public void withdraw(String number, BigDecimal amount) throws CustomException {
        validatePositiveAmount(amount);
        Account account = findAccount(number);
        BigDecimal newBalance = account.getBalance().subtract(amount);
        BigDecimal minimum = getMinimumBalance(account.getAccountType());
        if (newBalance.compareTo(minimum) < 0)
            throw new CustomException("Insufficient balance or minimum balance requirement would be violated.");

        BigDecimal todayTotal = BigDecimal.ZERO;
        for (Transaction transaction : account.getTransactions()) {
            if (transaction.getType().equals("WITHDRAW")
                    && transaction.getDateTime().toLocalDate().equals(LocalDate.now())) {
                todayTotal = todayTotal.add(transaction.getAmount());
            }
        }
        if (todayTotal.add(amount).compareTo(DAILY_WITHDRAWAL_LIMIT) > 0)
            throw new CustomException("Daily withdrawal limit of Rs. "
                    + DAILY_WITHDRAWAL_LIMIT + " exceeded.");

        account.setBalance(newBalance);
        addTransaction(account, "WITHDRAW", amount, "Cash withdrawal");
    }

    public void transfer(String fromNumber, String toNumber, BigDecimal amount)
            throws CustomException {
        validatePositiveAmount(amount);
        if (fromNumber.equals(toNumber))
            throw new CustomException("Source and destination accounts must be different.");

        Account from = findAccount(fromNumber);
        Account to = findAccount(toNumber);

        BigDecimal newBalance = from.getBalance().subtract(amount);
        if (newBalance.compareTo(getMinimumBalance(from.getAccountType())) < 0)
            throw new CustomException("Insufficient balance for this transfer.");

        from.setBalance(newBalance);
        to.setBalance(to.getBalance().add(amount));
        addTransaction(from, "TRANSFER OUT", amount, "Transferred to " + toNumber);
        addTransaction(to, "TRANSFER IN", amount, "Received from " + fromNumber);
    }

    public void changePin(String number, String newPin) throws CustomException {
        Account account = findAccount(number);
        if (newPin == null || !newPin.matches("[0-9]{4}"))
            throw new CustomException("New PIN must contain exactly 4 digits.");
        account.setPin(newPin);
    }

    public void setAccountLocked(String number, boolean locked) throws CustomException {
        findAccount(number).setLocked(locked);
    }

    public BigDecimal addMonthlyInterest(String number) throws CustomException {
        Account account = findAccount(number);
        if (!account.getAccountType().equals("SAVINGS"))
            throw new CustomException("Interest calculation is available for Savings accounts only.");
        BigDecimal interest = account.getBalance()
                .multiply(MONTHLY_INTEREST_RATE)
                .divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
        account.setBalance(account.getBalance().add(interest));
        addTransaction(account, "INTEREST", interest, "Monthly interest at 3% annual rate");
        return interest;
    }

    private void addTransaction(Account account, String type,
                                BigDecimal amount, String description) {
        account.addTransaction(new Transaction(makeTransactionId(),
                account.getAccountNumber(), type, amount, description));
    }

    private String makeTransactionId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public void displayAccount(Account account) {
        System.out.println("\n----- ACCOUNT DETAILS -----");
        System.out.println("Account Number : " + account.getAccountNumber());
        System.out.println("Customer Name  : " + account.getName());
        System.out.println("Date of Birth  : " + account.getDob());
        System.out.println("Address        : " + account.getAddress());
        System.out.println("Mobile         : " + account.getPhone());
        System.out.println("Email          : " + account.getEmail());
        System.out.println("Account Type   : " + account.getAccountType());
        System.out.println("Balance        : Rs. " + account.getBalance());
        System.out.println("Status         : " + (account.isActive() ? "ACTIVE" : "CLOSED"));
        System.out.println("Access         : " + (account.isLocked() ? "LOCKED" : "UNLOCKED"));
    }

    public void displayAllAccounts() {
        boolean found = false;
        for (Account account : accounts) {
            if (account.isActive()) {
                displayAccount(account);
                found = true;
            }
        }
        if (!found) System.out.println("No active accounts available.");
    }

    public void displayTransactions(Account account, int limit) {
        ArrayList<Transaction> list = account.getTransactions();
        if (list.isEmpty()) {
            System.out.println("No transactions available.");
            return;
        }
        int start = Math.max(0, list.size() - limit);
        for (int i = start; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }

    public Transaction getLastTransaction(Account account) {
        ArrayList<Transaction> list = account.getTransactions();
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }
}
