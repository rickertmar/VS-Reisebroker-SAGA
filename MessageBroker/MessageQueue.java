package MessageBroker;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MessageQueue {
    private static Message[] messageBuffer;
    private static int messageBufferHead = 0;
    private static int messageBufferTail = 0;
    private static int bufferSize;
    private static final Lock lock = new ReentrantLock();
    private static final Condition notFull = lock.newCondition();
    private static final Condition notEmpty = lock.newCondition();

    public MessageQueue(int size) {
        messageBuffer = new Message[size];
        bufferSize = size;
    }

    /**
     * Adds a message to the queue. If the queue is full, waits until there is space.
     * This method is thread-safe and uses a lock to manage access to the queue.
     *
     * @param message The message to add to the queue.
     * @throws InterruptedException if the current thread is interrupted while waiting.
     */
    public static void messageQueuePut(Message message) throws InterruptedException {
        lock.lock();
        try {
            // Wait until there is space in the queue
            while ((messageBufferHead + 1) % bufferSize == messageBufferTail) {
                notFull.await();
            }
            // Add the message to the queue and update the head pointer
            messageBuffer[messageBufferHead] = message;
            messageBufferHead = (messageBufferHead + 1) % bufferSize;
            // Signal any waiting threads that the queue is not empty
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes and returns a message from the queue. If the queue is empty, waits until a message is available.
     * This method is thread-safe and uses a lock to manage access to the queue.
     *
     * @return The message removed from the queue.
     * @throws InterruptedException if the current thread is interrupted while waiting.
     */
    public static Message messageQueueTake() throws InterruptedException {
        lock.lock();
        try {
            // Wait until there is a message in the queue
            while (messageBufferHead == messageBufferTail) {
                notEmpty.await();
            }
            // Remove the message from the queue and update the tail pointer
            Message message = messageBuffer[messageBufferTail];
            messageBufferTail = (messageBufferTail + 1) % bufferSize;
            // Signal any waiting threads that the queue is not full
            notFull.signal();
            return message;
        } finally {
            lock.unlock();
        }
    }
}