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

    public static void messageQueuePut(Message message) throws InterruptedException {
        lock.lock();
        try {
            while ((messageBufferHead + 1) % bufferSize == messageBufferTail) {
                notFull.await();
            }
            messageBuffer[messageBufferHead] = message;
            messageBufferHead = (messageBufferHead + 1) % bufferSize;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public static Message messageQueueTake() throws InterruptedException {
        lock.lock();
        try {
            while (messageBufferHead == messageBufferTail) {
                notEmpty.await();
            }
            Message message = messageBuffer[messageBufferTail];
            messageBufferTail = (messageBufferTail + 1) % bufferSize;
            notFull.signal();
            return message;
        } finally {
            lock.unlock();
        }
    }
}
