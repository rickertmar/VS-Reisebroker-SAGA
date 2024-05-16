package DataFiller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class FillProperties {
    public static void main(String[] args) {
        int numItems = 5; // Anzahl der zu generierenden Hotels und Flüge

        Properties data = generateData(numItems);
        saveDataToFile(data);
    }

    static Properties FailChances = new Properties();

    static {
        FailChances.setProperty("FailChance_noAnswer", "10");
        FailChances.setProperty("FailChance_noAnswer", "10");
    }

    private static Properties generateData(int numItems) {
        Properties properties = new Properties();

        // Hotels generieren und zur Properties-Datei hinzufügen
        for (int i = 1; i <= numItems; i++) {
            String hotelName = "Hotel " + i;
            properties.setProperty(hotelName, generateHotelData()); // Hier können Sie Ihre Hotel-Daten generieren
        }

        // Flüge generieren und zur Properties-Datei hinzufügen
        for (int i = 1; i <= numItems; i++) {
            String flightName = "Flight " + i;
            properties.setProperty(flightName, generateFlightData()); // Hier können Sie Ihre Flug-Daten generieren
        }


        properties.setProperty("NotSendMessage", Integer.toString(getChanceToNotSendMessage()));
        properties.setProperty("NotDoAnything", Integer.toString(getChanceToNotDoAnything()));
        properties.setProperty("NoClients", Integer.toString((int) (Math.random() * 50 + 50)));

        return properties;
    }

    private static String generateHotelData() {
        // Hier können Sie den generierten Hotel-Datensatz erstellen (z.B. Anzahl der Betten)
        // Rückgabeformat kann entsprechend der Anforderungen angepasst werden
        return "Number of beds: " + (int) (Math.random() * 100); // Beispiel: Zufällige Anzahl von Betten zwischen 0 und 100
    }

    private static String generateFlightData() {
        // Hier können Sie den generierten Flug-Datensatz erstellen (z.B. Anzahl der Sitze)
        // Rückgabeformat kann entsprechend der Anforderungen angepasst werden
        return "Number of seats: " + (int) (Math.random() * 200); // Beispiel: Zufällige Anzahl von Sitzen zwischen 0 und 200
    }

    // Samuel Anfang

    public static int getChanceToNotSendMessage() {
        return (int) (Math.random() * 100);
    }

    public static int getChanceToNotDoAnything() {
        return (int) (Math.random() * 100);

    }

    // Samuel Ende

    private static void saveDataToFile(Properties data) {
        try (FileOutputStream fileOutputStream = new FileOutputStream("data.properties")) {
            data.store(fileOutputStream, "Generated Hotel and Flight Data");
            System.out.println("Generated data saved to generated_data.properties file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static int getNoClients() {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("data.properties")) {
            properties.load(fileInputStream);
            return Integer.parseInt(properties.getProperty("NoClients"));
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
