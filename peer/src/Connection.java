import java.io.*;
import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Manuel on 2015-11-12.
 */
public class Connection{

    private int send_ACK;
    private int send_SEQ;
    private int recv_ACK;
    private int recv_SEQ;
    private int current_SEQ;

    private boolean connection_ending;
    private boolean connection_established;
    private Integer synNotify;

    private SocketAddress address;
    private ClientSocket client;

    private Map<Integer, DataDatagram > priorityQueue;
    private CongestionWindow congestionWindow;

    private PipedInputStream sendInStream;
    private CoolPipedOutputStream sendOutStream;
    private CoolPipedInputStream receiveInStream;
    private PipedOutputStream receiveOutStream;


    Connection(SocketAddress address, ClientSocket client)
    {
        send_ACK = 0;
        send_SEQ = 0;
        recv_ACK = 0;
        recv_SEQ = 0;
        current_SEQ = 0;

        this.address = address;
        this.client = client;

        priorityQueue  = new HashMap<>();
        connection_established = false;

        try {
            congestionWindow = new CongestionWindow(new PacketDroppedObserver());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sendOutStream = new CoolPipedOutputStream();
        receiveOutStream = new PipedOutputStream();


        try {
            receiveInStream = new CoolPipedInputStream(receiveOutStream, ConstantDefinitions.INITIAL_WINDOW_SIZE);
            sendInStream = new PipedInputStream(sendOutStream, ConstantDefinitions.INITIAL_WINDOW_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   protected boolean enqueue(DatagramPacket packet)
    {
        CoolDatagram coolDatagram = new CoolDatagram(packet);

        switch(coolDatagram.getPacketType())
        {
            case SYN:

                if(!connection_established)
                {
                    SynDatagram synDatagram = new SynDatagram(coolDatagram.getPayload());
                    recv_SEQ = synDatagram.getSeq();

                    ControlPacket handshakeResponse = new ControlPacket(PacketType.SYN_ACK);
                    send_SEQ = getRandom();
                    handshakeResponse.addSequence(send_SEQ);
                    send_SEQ++;
                    send_ACK = recv_SEQ + 1;
                    handshakeResponse.addAcknowledgment(send_ACK);
                    sendPacket(new DatagramPacket(handshakeResponse.getPacket(), handshakeResponse.getPacket().length, address));

                    current_SEQ = send_ACK;
                    connection_established = true;

                }

                break;

            case ACK:

                AckDatagram ackDatagram = new AckDatagram(coolDatagram.getPayload());
                congestionWindow.ackPacket(ackDatagram.getAck());

                if(connection_ending)
                {
                    if(ackDatagram.getAck() == send_SEQ)
                    {
                        connection_established = false;

                        try {

                            sendInStream.close();
                            sendOutStream.close();
                            receiveInStream.close();
                            receiveOutStream.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        client.disconnect(this);

                    }
                }

                break;

            case DATA:

                if(connection_established)
                {
                    DataDatagram dataDatagram = new DataDatagram(coolDatagram.getPayload());
                    recv_SEQ = dataDatagram.getSeq();
                    send_ACK = recv_SEQ + dataDatagram.getLength();

                    ControlPacket ackPacket= new ControlPacket(PacketType.ACK);
                    ackPacket.addAcknowledgment(send_ACK);
                    sendAcknowledgment(new DatagramPacket(ackPacket.getPacket(), ackPacket.getPacket().length, address));

                    priorityQueue.put(recv_SEQ, dataDatagram);
                }

                break;

            case SYN_ACK:

                    SynAckDatagram synackDatagram = new SynAckDatagram(coolDatagram.getPayload());

                    if(synackDatagram.getAck() == send_SEQ)
                    {
                        recv_SEQ = synackDatagram.getSeq();

                        ControlPacket handshakeFinal = new ControlPacket(PacketType.ACK);
                        send_ACK = recv_SEQ + 1;
                        handshakeFinal.addAcknowledgment(send_ACK);
                        sendAcknowledgment(new DatagramPacket(handshakeFinal.getPacket(), handshakeFinal.getPacket().length, address));

                        current_SEQ = send_ACK;
                        connection_established = true;


                        synNotify.notify();
                    }

                break;

            case FIN:
                if(connection_established)
                {
                    FinDatagram finDatagram = new FinDatagram(coolDatagram.getPayload());
                    if(finDatagram.getSeq() == send_ACK)
                    {
                        finDatagram.getSeq();
                        CoolDatagram Datagram = finDatagram;


                        ControlPacket confirmFin = new ControlPacket(PacketType.ACK);
                        send_ACK = recv_SEQ + 1;
                        confirmFin.addAcknowledgment(send_ACK);
                        sendAcknowledgment(new DatagramPacket(confirmFin.getPacket(), confirmFin.getPacket().length, address));

                        ControlPacket finFinal = new ControlPacket(PacketType.FIN);
                        finFinal.addSequence(send_SEQ);
                        send_SEQ++;
                        sendPacket(new DatagramPacket(finFinal.getPacket(), finFinal.getPacket().length, address));

                        connection_ending = true;
                    }
                }
                break;
            case UNKNOWN:
                //IGNORE
                break;
        }

        return false;
    }

    protected boolean connect()
    {
        ControlPacket handshakeStart = new ControlPacket(PacketType.SYN);
        send_SEQ = getRandom();
        handshakeStart.addSequence(send_SEQ);
        send_SEQ++;

        try {
            synNotify.wait();

            if(connection_established)
            {
                return true;
            }
            else
            {
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getRandom()
    {
        return (int) Math.random() * Integer.MAX_VALUE;
    }

    private void sendAcknowledgment(DatagramPacket sendDatagram)
    {
        client.sendingQueue.add(sendDatagram);
    }

    private void sendPacket(DatagramPacket sendDatagram)
    {
        client.sendingQueue.add(sendDatagram);
        CoolDatagram coolDatagram = new CoolDatagram(sendDatagram);
        congestionWindow.putPacket(coolDatagram);
    }

    public InputStream getInputStream()
    {
        return receiveInStream;
    }

    public OutputStream getOutputStream()
    {
        return sendOutStream;
    }

    protected SocketAddress getSocketAddress()
    {
        return address;
    }

    public class CoolPipedInputStream extends PipedInputStream
    {
        CoolPipedInputStream(PipedOutputStream out, int size) throws IOException {
            super(out, size);
        }

        @Override
        public synchronized int read() throws IOException
        {
            byte[] b = new byte[1];
            read(b, 0, 1);
            return b[0];
        }

        @Override
        public int read(byte[] b) throws IOException
        {
            return read(b, 0 ,b.length);
        }

        @Override
        public synchronized int read(byte[] b, int off, int len) throws IOException
        {
            if(current_SEQ != 0)
            {
                while(priorityQueue.containsKey(current_SEQ) && this.available() < len)
                {
                    if(ConstantDefinitions.INITIAL_WINDOW_SIZE - this.available() < priorityQueue.get(current_SEQ).getLength())
                    {
                        break;
                    }

                    current_SEQ = current_SEQ + priorityQueue.get(current_SEQ).getLength();
                    receiveOutStream.write(priorityQueue.remove(current_SEQ).getData().array());
                }

                return super.read(b, off, len);
            }
            else
            {
                return 0;
            }
        }
    }

    public class CoolPipedOutputStream extends PipedOutputStream
    {
        CoolPipedOutputStream()
        {
            super();
        }

        @Override
        public void write(byte[] b) throws IOException {
            super.write(b, 0, b.length);
        }

        @Override
        public void write(int b) throws IOException {
            byte[] barr = new byte[1];
            barr[0] = (byte) b;
            super.write(barr, 0 ,1);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {

            super.write(b, off, len);

            byte[] barr = new byte[len];
            int read = sendInStream.read(barr, 0, len);

            DataPacket dataPacket = new DataPacket(len + ConstantDefinitions.DATA_OVERHEAD);
            dataPacket.addSequence(send_SEQ);
            dataPacket.addData(barr, read);
            sendPacket(new DatagramPacket(dataPacket.getPacket(), dataPacket.getPacket().length, address));

            send_SEQ = send_SEQ + read;
        }
    }

    protected class PacketDroppedObserver implements CongestionWindow.Observer
    {
        @Override
        public void onPacketDropped(CoolDatagram coolDatagram)
        {
            if(coolDatagram.getPacketType() == PacketType.SYN)
            {
                connection_established = false;
                synNotify.notify();
            }
            else
            {
                sendPacket(coolDatagram.getUdpPacket());
            }
        }
    }


}
