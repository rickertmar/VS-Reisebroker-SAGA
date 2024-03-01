import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Worker-Thread, der Aufträge aus einem Ringpuffer liest und entsprechend bearbeitet.
 */
class Worker implements Runnable {
    private final RingBuffer buffer; // Ringpuffer, aus dem die Aufträge gelesen werden.
    private final DatagramSocket socket; // Socket zur Kommunikation mit dem Client.
    private static Map<String, FileMonitor> fileMonitors = FileServer.fileMonitors;
    private static int newFileMonitors = 0;

    /**
     * Konstruktor für Worker.
     * Der Ringpuffer, aus dem die Aufträge gelesen werden sollen.
     * Der Socket, der für die Netzwerkkommunikation verwendet wird.
     */
    public Worker(RingBuffer buffer, DatagramSocket socket) {
        this.buffer = buffer;
        this.socket = socket;
    }

    /**
     * Die run-Methode, die die Hauptlogik für den Worker-Thread enthält.
     * Diese Methode wird ausgeführt, wenn der Thread gestartet wird.
     */
    @Override
    public void run(){
        try {
            while (true) { // Unendliche Schleife, um kontinuierlich Aufträge zu verarbeiten.
                DatagramPacket packet = getWork(); // Holen eines Auftrags aus dem Ringpuffer.
                handleRequest(packet); // Bearbeiten des Auftrags.
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt(); // Der Thread wurde unterbrochen, also setzen wir das Unterbrechungsflag.
        }
    }

    /**
     * Entnimmt einen Arbeitsauftrag aus dem Ringpuffer.
     * Ein DatagramPacket, das den Arbeitsauftrag enthält.
     * InterruptedException Wird geworfen, wenn der Thread unterbrochen wird, während er wartet.
     */
    private DatagramPacket getWork() throws InterruptedException {
        return buffer.read(); // Lesen und Rückgabe eines DatagramPackets aus dem Ringpuffer.
    }

    private synchronized void addFileMonitor(String filename) throws Exception{
        System.out.println("Worker: Erstelle FileMonitor für " + filename);
        while (newFileMonitors >=1){
            System.out.println("Worker: Warte auf FileMonitor");
                wait();
        }
        newFileMonitors++;
        fileMonitors.put(filename, new FileMonitor(new File("C:\\Users\\ian-s\\IdeaProjects\\Testat3\\Files\\" + filename)));
        System.out.println("Worker: FileMonitor erstellt");
        newFileMonitors--;
        notify();
    }

    /**
     * Bearbeitet die Anfrage, die in einem DatagramPacket enthalten ist.
     * packet das DatagramPacket, das die Anfrage enthält.
     */
    private void handleRequest(DatagramPacket packet) throws Exception{
        String request = new String(packet.getData(), 0, packet.getLength()); // Umwandeln der Paketdaten in einen String.
        System.out.println("Worker: Bearbeite Anfrage von " + packet.getAddress() + " - Anfrage: " + request);

        // Zerlegen der Anfrage in ihre einzelnen Teile.
        String[] parts = request.split(",");
        String command = parts[0].trim(); // Der Befehl (z.B. READ oder WRITE)
        String filename = parts[1].trim(); // Der Dateiname, auf den sich der Befehl bezieht.
        int lineNo = Integer.parseInt(parts[2].trim()); // Die Zeilennummer, falls benötigt.

        // Überprüfung und Ausführung des entsprechenden Befehls.
        if (command.equalsIgnoreCase("READ")) {
            FileMonitor fileMonitor = fileMonitors.get(filename);
            if (fileMonitor == null) {
                sendResponse("ERROR: Datei nicht gefunden", packet.getAddress(), packet.getPort());
                return;
            }
            try {
                String data = fileMonitor.read(lineNo);
                sendResponse("OK," + data, packet.getAddress(), packet.getPort());
            } catch (Exception e) {
                sendResponse("ERROR: " + e.getMessage(), packet.getAddress(), packet.getPort());
            }
        } else if (command.equalsIgnoreCase("WRITE")) {
            String data = parts[3].trim(); // Die zu schreibenden Daten.
            FileMonitor fileMonitor = fileMonitors.get(filename);
            if (fileMonitor == null) {
                addFileMonitor(filename);
                fileMonitor = fileMonitors.get(filename);
            }
            fileMonitor.write(data, lineNo);
            sendResponse("OK", packet.getAddress(), packet.getPort());
        } else {
            // Unbekannter Befehl wurde empfangen, sende eine Fehlermeldung zurück.
            sendResponse("ERROR: Ungültiger Befehl", packet.getAddress(), packet.getPort());
        }
    }

    /**
     * Sendet eine Antwort an den Client über den DatagramSocket.
     * response Die Antwort, die gesendet werden soll.
     * address Die Adresse, an die die Antwort gesendet werden soll.
     * port Der Port, an den die Antwort gesendet werden soll.
     */
    private void sendResponse(String response, InetAddress address, int port) {
        try {
            byte[] buf = response.getBytes(); // Umwandeln der Antwort in ein Byte-Array.
            DatagramPacket responsePacket = new DatagramPacket(buf, buf.length, address, port); // Erstellung des Antwortpakets.
            socket.send(responsePacket); // Senden des Antwortpakets über den Socket.
        } catch (IOException e) {
            e.printStackTrace(); // Bei einer IO-Ausnahme, drucke die Stack-Trace.
        }
    }
}
