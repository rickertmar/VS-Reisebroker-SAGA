package DataFiller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * The FillProperties class is designed to generate and manage properties data for a simulation.
 * It generates data for hotels and flights, including random capacities and seats, and manages
 * probabilities for certain actions not to be taken. It also provides functionality to save this
 * data to a properties file and retrieve specific values from it.
 */
public class FillProperties {
    public static void main(String[] args) {
        int numItems = 100; // Number of hotels and flights to generate

        Properties data = generateData(numItems);
        saveDataToFile(data);
    }

    // Static block to initialize failure chances
    static Properties FailChances = new Properties();

    static {
        FailChances.setProperty("FailChance_noAnswer", "10");
        FailChances.setProperty("FailChance_noAnswer", "10");
    }

    /**
     * Generates properties data for hotels and flights, including random capacities and seats.
     * It also generates random probabilities for actions not being taken.
     * @param numItems The number of hotels and flights to generate.
     * @return A Properties object containing the generated data.
     */
    private static Properties generateData(int numItems) {
        Properties properties = new Properties();

        // Generate hotel data and add to properties
        for (int i = 1; i <= numItems; i++) {
            String hotelName = "Hotel " + i;
            properties.setProperty(hotelName, generateHotelData());
        }

        // Generate flight data and add to properties
        for (int i = 1; i <= numItems; i++) {
            String flightName = "Flight " + i;
            properties.setProperty(flightName, generateFlightData());
        }

        // Generate probabilities for actions not being taken
        properties.setProperty("NotSendMessage", Integer.toString(generateChanceToNotSendMessage()));
        properties.setProperty("NotDoAnything", Integer.toString(generateChanceToNotDoAnything()));
        properties.setProperty("NoClients", Integer.toString((int) (Math.random() * 50 + 50)));

        return properties;
    }

    /**
     * Generates a random number of beds for a hotel.
     * @return A string representing the number of beds in the format "Number of beds: <random number>".
     */
    private static String generateHotelData() {
        return "Number of beds: " + (int) (Math.random() * 100);
    }

    /**
     * Generates a random number of seats for a flight.
     * @return A string representing the number of seats in the format "Number of seats: <random number>".
     */
    private static String generateFlightData() {
        return "Number of seats: " + (int) (Math.random() * 200);
    }

    /**
     * Generates a random chance for a message not to be sent.
     * @return An integer representing the probability.
     */
    public static int generateChanceToNotSendMessage() {
        return (int) (Math.random() * 100);
    }

    /**
     * Generates a random chance for no action to be taken.
     * @return An integer representing the probability.
     */
    public static int generateChanceToNotDoAnything() {
        return (int) (Math.random() * 100);
    }

    /**
     * Saves the generated properties data to a file.
     * @param data The Properties object containing the data to be saved.
     */
    private static void saveDataToFile(Properties data) {
        try (FileOutputStream fileOutputStream = new FileOutputStream("data.properties")) {
            data.store(fileOutputStream, "Generated Hotel and Flight Data");
            System.out.println("Generated data saved to generated_data.properties file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the probability of a message not being sent from the properties file.
     * @return The probability as an integer.
     */
    public static int getChanceToNotSendMessage() {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("data.properties")) {
            properties.load(fileInputStream);
            return Integer.parseInt(properties.getProperty("NotSendMessage"));
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Retrieves the probability of no action being taken from the properties file.
     * @return The probability as an integer.
     */
    public static int getChanceToNotDoAnything() {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("data.properties")) {
            properties.load(fileInputStream);
            return Integer.parseInt(properties.getProperty("NotDoAnything"));
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Retrieves the number of clients from the properties file.
     * @return The number of clients as an integer.
     */
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