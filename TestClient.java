import java.net.*;



class TestClient {

    private static final int PORT = 5999;
    private static final String HOST = "localhost";

    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(HOST);

        sendRequest(socket, address, "WRITE,test.txt,1,Hello World!");
        sendRequest(socket, address, "READ,test.txt,1");


        //test if something is overwritten
        sendRequest(socket, address, "WRITE,test.txt,1,Hello World3!");
        sendRequest(socket, address, "READ,test.txt,1");

        //test wrong line number
        sendRequest(socket, address, "WRITE,test.txt,1,Hello World4!");
        sendRequest(socket, address, "READ,test.txt,199");

        //test if file is created
        sendRequest(socket, address, "WRITE,test2.txt,1,Hello World!");
        sendRequest(socket, address, "READ,test2.txt,1");

        //test read a file that does not exist
        sendRequest(socket, address, "READ,test3.txt,1");

        //test server under load
        for (int i = 0; i < 10; i++) {
            if (i%2==0)
                new ThreadRequest(socket, address, "READ,test4.txt,1").start();
            else
                new ThreadRequest(socket, address, "WRITE,test4.txt,1,Hello World!"+i).start();
        }

        Thread.sleep(20000);
        socket.close();
    }

    private static void sendRequest(DatagramSocket socket, InetAddress address, String request) throws Exception {
        byte[] buf = request.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
        socket.send(packet);

        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String response = new String(packet.getData(), 0, packet.getLength());
        System.out.println(response);
    }
    private static class ThreadRequest extends Thread {
        private DatagramSocket socket;
        private InetAddress address;
        private String request;

        public ThreadRequest(DatagramSocket socket, InetAddress address, String request) {
            this.socket = socket;
            this.address = address;
            this.request = request;
        }

        public void run() {
            try {
                sendRequest(socket, address, request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}