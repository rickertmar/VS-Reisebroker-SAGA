import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import HotelService.HotelService;

public class Gernerator {
    public static void main(String[] args) {
        fillHotels();
        fillFlights();
    }

    public static void fillHotels() {
        try (InputStream inputStream = Gernerator.class.getResourceAsStream("/hotels.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);

            properties.forEach((key, value) -> {
                String hotelName = key.toString();
                int capacity = Integer.parseInt(value.toString());
                // Hier können Sie Ihre Hotelobjekte erstellen und weiterverarbeiten
                HotelService hotel = new HotelService(hotelName, capacity);
                // Zum Beispiel können Sie die Hotelobjekte in einer Liste speichern oder direkt verwenden
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fillFlights() {
        try (InputStream inputStream = Gernerator.class.getResourceAsStream("/flights.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);

            properties.forEach((key, value) -> {
                String flightName = key.toString();
                int capacity = Integer.parseInt(value.toString());
                // Hier können Sie Ihre Flugobjekte erstellen und weiterverarbeiten
                Flight flight = new Flight(flightName, capacity);
                // Zum Beispiel können Sie die Flugobjekte in einer Liste speichern oder direkt verwenden
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
