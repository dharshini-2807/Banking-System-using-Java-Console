import java.io.*;
import java.util.ArrayList;

public class FileManager {
    private static final String FILE_NAME = "banking_data.dat";

    public static void saveAccounts(ArrayList<Account> accounts) {
        try (ObjectOutputStream output =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            output.writeObject(accounts);
        } catch (IOException e) {
            System.out.println("Data save error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Account> loadAccounts() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream input =
                     new ObjectInputStream(new FileInputStream(file))) {
            return (ArrayList<Account>) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Data load error: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
