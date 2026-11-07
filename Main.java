import java.math.BigDecimal;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final Bank bank = new Bank();

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            showMenu();
            int choice = readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1: createAccount(); break;
                    case 2: viewAccount(); break;
                    case 3: updateAccount(); break;
                    case 4: closeAccount(); break;
                    case 5: searchAccount(); break;
                    case 6: deposit(); break;
                    case 7: withdraw(); break;
                    case 8: balanceEnquiry(); break;
                    case 9: transfer(); break;
                    case 10: miniStatement(); break;
                    case 11: transactionHistory(); break;
                    case 12: verifyPin(); break;
                    case 13: changePin(); break;
                    case 14: lockAccount(); break;
                    case 15: unlockAccount(); break;
                    case 16: minimumBalanceInfo(); break;
                    case 17: dailyLimitInfo(); break;
                    case 18: calculateInterest(); break;
                    case 19: accountTypeInfo(); break;
                    case 20: printLastReceipt(); break;
                    case 21: bank.displayAllAccounts(); break;
                    case 22:
                        save();
                        System.out.println("Logged out. Thank you for using our Banking System!");
                        running = false;
                        break;
                    default:
                        System.out.println("Please enter a valid choice (1-22).");
                }
            } catch (CustomException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected input/error. Please try again.");
            }
        }
        sc.close();
    }

    private static void showMenu() {
        System.out.println("\n========== BANKING MANAGEMENT SYSTEM ==========");
        System.out.println("1.  Create Account");
        System.out.println("2.  View Account Details");
        System.out.println("3.  Update Customer Details");
        System.out.println("4.  Close Account");
        System.out.println("5.  Search Account");
        System.out.println("6.  Deposit Money");
        System.out.println("7.  Withdraw Money");
        System.out.println("8.  Balance Enquiry");
        System.out.println("9.  Money Transfer");
        System.out.println("10. Mini Statement");
        System.out.println("11. Transaction History");
        System.out.println("12. Verify PIN / Login Check");
        System.out.println("13. Change PIN");
        System.out.println("14. Lock Account (Admin Demo)");
        System.out.println("15. Unlock Account (Admin Demo)");
        System.out.println("16. Minimum Balance Information");
        System.out.println("17. Daily Withdrawal Limit");
        System.out.println("18. Calculate Monthly Interest");
        System.out.println("19. Account Type Information");
        System.out.println("20. Print Last Transaction Receipt");
        System.out.println("21. Display All Accounts (Admin Demo)");
        System.out.println("22. Logout / Exit");
        System.out.println("================================================");
    }

    private static void createAccount() throws CustomException {
        System.out.println("\n--- CREATE ACCOUNT ---");
        String number = readLine("Account Number (6-18 digits): ");
        String name = readLine("Customer Name: ");
        String dob = readLine("Date of Birth (DD-MM-YYYY): ");
        String address = readLine("Address: ");
        String phone = readLine("Mobile Number (10 digits): ");
        String email = readLine("Email: ");
        String type = readLine("Account Type (SAVINGS/CURRENT): ").toUpperCase();
        BigDecimal opening = readMoney("Opening Balance: ");
        String pin = readLine("Create 4-digit PIN: ");

        bank.createAccount(number, name, dob, address, phone, email,
                type, opening, pin);
        save();
        System.out.println("Account Created Successfully!");
    }

    private static void viewAccount() throws CustomException {
        Account account = getAccountWithPin();
        bank.displayAccount(account);
    }

    private static void updateAccount() throws CustomException {
        Account account = getAccountWithPin();
        System.out.println("Enter updated details:");
        String name = readLine("Customer Name: ");
        String address = readLine("Address: ");
        String phone = readLine("Mobile Number: ");
        String email = readLine("Email: ");
        bank.updateCustomerDetails(account.getAccountNumber(), name, address, phone, email);
        save();
        System.out.println("Customer details updated successfully.");
    }

    private static void closeAccount() throws CustomException {
        Account account = getAccountWithPin();
        bank.closeAccount(account.getAccountNumber());
        save();
        System.out.println("Account closed successfully.");
    }

    private static void searchAccount() throws CustomException {
        String number = readLine("Enter Account Number: ");
        Account account = bank.findAccount(number);
        bank.displayAccount(account);
    }

    private static void deposit() throws CustomException {
        Account account = getAccountWithPin();
        BigDecimal amount = readMoney("Enter deposit amount: ");
        bank.deposit(account.getAccountNumber(), amount);
        save();
        System.out.println("Amount Deposited Successfully!");
        printBalance(account);
    }

    private static void withdraw() throws CustomException {
        Account account = getAccountWithPin();
        BigDecimal amount = readMoney("Enter withdrawal amount: ");
        bank.withdraw(account.getAccountNumber(), amount);
        save();
        System.out.println("Withdrawal Successful!");
        printBalance(account);
    }

    private static void balanceEnquiry() throws CustomException {
        Account account = getAccountWithPin();
        printBalance(account);
    }

    private static void transfer() throws CustomException {
        Account source = getAccountWithPin();
        String destination = readLine("Beneficiary Account Number: ");
        BigDecimal amount = readMoney("Transfer Amount: ");
        bank.transfer(source.getAccountNumber(), destination, amount);
        save();
        System.out.println("Money Transferred Successfully!");
        printBalance(source);
    }

    private static void miniStatement() throws CustomException {
        Account account = getAccountWithPin();
        System.out.println("\n--- MINI STATEMENT (LAST 5) ---");
        bank.displayTransactions(account, 5);
    }

    private static void transactionHistory() throws CustomException {
        Account account = getAccountWithPin();
        System.out.println("\n--- COMPLETE TRANSACTION HISTORY ---");
        bank.displayTransactions(account, Integer.MAX_VALUE);
    }

    private static void verifyPin() throws CustomException {
        String number = readLine("Account Number: ");
        String pin = readLine("PIN: ");
        bank.verifyPin(number, pin);
        System.out.println("PIN Verified Successfully. Login Check Passed!");
    }

    private static void changePin() throws CustomException {
        Account account = getAccountWithPin();
        String newPin = readLine("Enter New 4-digit PIN: ");
        bank.changePin(account.getAccountNumber(), newPin);
        save();
        System.out.println("PIN Changed Successfully!");
    }

    private static void lockAccount() throws CustomException {
        String number = readLine("Account Number: ");
        bank.setAccountLocked(number, true);
        save();
        System.out.println("Account Locked.");
    }

    private static void unlockAccount() throws CustomException {
        String number = readLine("Account Number: ");
        bank.setAccountLocked(number, false);
        save();
        System.out.println("Account Unlocked.");
    }

    private static void minimumBalanceInfo() {
        System.out.println("Savings minimum balance: Rs. 500.00");
        System.out.println("Current account minimum balance: Rs. 0.00");
        System.out.println("Note: These are sample rules for this college project.");
    }

    private static void dailyLimitInfo() {
        System.out.println("Daily withdrawal limit: Rs. " + bank.getDailyWithdrawalLimit());
    }

    private static void calculateInterest() throws CustomException {
        Account account = getAccountWithPin();
        BigDecimal interest = bank.addMonthlyInterest(account.getAccountNumber());
        save();
        System.out.println("Monthly interest added: Rs. " + interest);
        printBalance(account);
    }

    private static void accountTypeInfo() {
        System.out.println("SAVINGS: Sample savings account; minimum balance Rs. 500.");
        System.out.println("CURRENT: Sample current account; minimum balance Rs. 0.");
    }

    private static void printLastReceipt() throws CustomException {
        Account account = getAccountWithPin();
        Transaction transaction = bank.getLastTransaction(account);
        if (transaction == null) {
            System.out.println("No transaction receipt available.");
        } else {
            System.out.println("\n========== TRANSACTION RECEIPT ==========");
            System.out.println(transaction);
            System.out.println("Current Balance: Rs. " + account.getBalance());
            System.out.println("=========================================");
        }
    }

    private static Account getAccountWithPin() throws CustomException {
        String number = readLine("Account Number: ");
        String pin = readLine("PIN: ");
        bank.verifyPin(number, pin);
        return bank.findAccount(number);
    }

    private static void printBalance(Account account) {
        System.out.println("Available Balance: Rs. " + account.getBalance());
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter numbers only.");
            }
        }
    }

    private static BigDecimal readMoney(String prompt) throws CustomException {
        try {
            System.out.print(prompt);
            BigDecimal amount = new BigDecimal(sc.nextLine().trim());
            if (amount.scale() > 2)
                throw new CustomException("Amount can have at most 2 decimal places.");
            return amount;
        } catch (NumberFormatException e) {
            throw new CustomException("Invalid amount. Enter a number.");
        }
    }

    private static void save() {
        FileManager.saveAccounts(bank.getAllAccounts());
    }
}
