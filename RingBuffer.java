import java.net.DatagramPacket;

/**
 * Eine RingBuffer-Klasse zur Speicherung von DatagramPaketen.
 * Dieser Puffer arbeitet nach dem First-In-First-Out (FIFO) Prinzip und ist thread-sicher.
 */
class RingBuffer {
    private final int capacity; // Maximale Kapazität des Ringpuffers.
    private final DatagramPacket[] buffer; // Das Array, das als Ringpuffer dient.
    private int readPointer = 0; // Zeiger, der auf die Stelle im Puffer zeigt, wo gelesen werden soll.
    private int writePointer = 0; // Zeiger, der auf die Stelle im Puffer zeigt, wo geschrieben werden soll.
    private int size = 0; // Aktuelle Anzahl der Elemente im Puffer.

    /**
     * Konstruktor für den Ringpuffer.
     * Initialisiert den Puffer mit der gegebenen Kapazität.
     */
    public RingBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new DatagramPacket[capacity];
    }

    /**
     * Fügt ein DatagramPacket zum Ringpuffer hinzu.
     * Falls der Puffer voll ist, wartet diese Methode, bis Platz verfügbar ist.
     */
    public synchronized void write(DatagramPacket data) throws InterruptedException {
        while (size == capacity) {
            wait(); // Warten, bis Platz im Puffer verfügbar ist.
        }
        buffer[writePointer] = data; // Einfügen des DatagramPackets in den Puffer.
        writePointer = (writePointer + 1) % capacity; // Aktualisieren des Schreibzeigers.
        size++; // Erhöhen der Puffergröße.
        notifyAll(); // Benachrichtigen anderer wartender Threads.
    }

    /**
     * Liest und entfernt ein DatagramPacket aus dem Ringpuffer.
     * Falls der Puffer leer ist, wartet diese Methode, bis ein Element verfügbar ist.
     */
    public synchronized DatagramPacket read() throws InterruptedException {
        while (size == 0) {
            wait(); // Warten, bis ein Element zum Lesen verfügbar ist.
        }
        DatagramPacket data = buffer[readPointer]; // Auslesen des DatagramPackets aus dem Puffer.
        readPointer = (readPointer + 1) % capacity; // Aktualisieren des Lesezeigers.
        size--; // Verringern der Puffergröße.
        notifyAll(); // Benachrichtigen anderer wartender Threads.
        return data; // Zurückgeben des gelesenen DatagramPackets.
    }
}
