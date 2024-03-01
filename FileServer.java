import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Ein einfacher FileServer, der Datagramme über UDP empfängt und die Daten in einen Ringpuffer schreibt.
 * Für jeden File im vordefinierten Ordner wird ein FileMonitor angelegt.
 * Der Server startet eine bestimmte Anzahl von Workern, die die Daten aus dem Ringpuffer verarbeiten.
 */
class FileServer {

    // Konstante für den Port, auf dem der Server läuft.
    private static final int PORT = 5999;

    // Socket für die UDP-Kommunikation.
    private final DatagramSocket socket = new DatagramSocket(PORT);

    // Map von Dateinamen zu FileMonitor-Instanzen.
    static final Map<String, FileMonitor> fileMonitors = new HashMap<>();

    // Größe des Ringpuffers.
    private static final int BUFFER_SIZE = 100;

    // Maximale Anzahl von Worker-Threads.
    private static final int MAX_THREADS = 100;

    // Instanz des Ringpuffers.
    private final RingBuffer buffer = new RingBuffer(BUFFER_SIZE);

    /**
     * Konstruktor des FileServers.
     * Initialisiert die FileMonitore und startet die Worker und den Dispatcher.
     */
    public FileServer() throws Exception {
        System.out.println("FileServer startet...");
        System.out.println("Initialisiere FileMonitore...");
        initFileMonitors();
        System.out.println("Initialisierung abgeschlossen!");
        System.out.println("Starte Worker...");
        startWorkers();
        System.out.println("Starte Dispatcher...");
        start();
    }

    /**
     * Initialisiert die FileMonitore für jeden File im festgelegten Ordner.
     */
    private void initFileMonitors() {
        File dir = new File("C:\\Users\\ian-s\\IdeaProjects\\Testat3\\Files\\");
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                fileMonitors.put(file.getName(), new FileMonitor(file));
            }
        }
    }

    /**
     * Erstellt und startet Worker-Threads, die die Daten aus dem Ringpuffer verarbeiten.
     */
    private void startWorkers() {
        for (int i = 0; i < MAX_THREADS; i++) {
            new Thread(new Worker(buffer, socket)).start();
        }
    }

    /**
     * Startet den Dispatcher in einer Endlosschleife, der Datagramme empfängt und in den Ringpuffer schreibt.
     */
    public void start() throws Exception {
        System.out.println("Dispatcher gestartet!");
        do {
            try {
                // Reserviert Speicher für das zu empfangende Datagram.
                byte[] buf = new byte[256];
                // Erstellt ein DatagramPacket, um die Daten zu empfangen.
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                // Empfängt das DatagramPacket vom Socket.
                socket.receive(packet);
                // Schreibt das empfangene DatagramPacket in den Ringpuffer.
                buffer.write(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (true);
    }

    /**
     * Der Einstiegspunkt des Programms,erstellt eine Instanz des FileServers.
     */
    public static void main(String[] args) throws Exception {
        new FileServer();
    }
}
