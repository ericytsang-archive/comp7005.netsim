package ProtocolPeer;

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

    private boolean connection_established;
    private boolean connection_active;

    private boolean started_fin;
    private boolean response_fin;

    private boolean readComplete;

    private Object synNotify;
    private Object seqNotify;

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
        connection_active = true;

        started_fin = false;
        response_fin = false;

        readComplete = false;

        synNotify = new Object();
        seqNotify = new Object();

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
                    SynDatagram synDatagram = new SynDatagram(coolDatagram);
                    recv_SEQ = synDatagram.getSeq();

                    System.out.println("RECEIVED SYN: " + recv_SEQ);

                    ControlPacket handshakeResponse = new ControlPacket(PacketTypesProtocol.SYN_ACK);
                    send_SEQ = getRandom();
                    handshakeResponse.addSequence(send_SEQ);

                    send_ACK = recv_SEQ + ConstantDefinitions.SYN_SIZE;

                    System.out.println("SENT SYNACK: " + send_SEQ + ", " + send_ACK);
                    send_SEQ = send_SEQ + ConstantDefinitions.SYNACK_SIZE;
                    handshakeResponse.addAcknowledgment(send_ACK);
                    sendPacket(new DatagramPacket(handshakeResponse.getPacket(), handshakeResponse.getPacket().length, address));

                    current_SEQ = send_ACK;
                    connection_established = true;
                    System.out.println("CONNECTION SUCCESSFULLY RECEIVED !!");

                }

                break;

            case ACK:

                AckDatagram ackDatagram = new AckDatagram(coolDatagram);
                congestionWindow.ackPacket(ackDatagram.getAck());

                if(response_fin)
                {

                    if(ackDatagram.getAck() == send_SEQ)
                    {
                        connection_established = false;
                        connection_active = false;

                        try {

                            sendInStream.close();
                            sendOutStream.close();
                            receiveInStream.close();
                            receiveOutStream.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        client.disconnect(this);
                        System.out.println("CONNECTION DISCONNECTED");


                    }
                }

                break;

            case DATA:

                if(connection_established)
                {
                    DataDatagram dataDatagram = new DataDatagram(coolDatagram);
                    recv_SEQ = dataDatagram.getSeq();
                    send_ACK = recv_SEQ + dataDatagram.getLength();

                    System.out.println("Received SEQ: " + recv_SEQ);
                    System.out.println("Sent ACK: " + send_ACK);

                   /* StringBuffer sb = new StringBuffer();
                    for( byte b : dataDatagram.getData().array() )
                        sb.append(Integer.toHexString( b ));
                    System.out.println(sb.toString());*/

                    ControlPacket ackPacket= new ControlPacket(PacketTypesProtocol.ACK);
                    ackPacket.addAcknowledgment(send_ACK);
                    sendAcknowledgment(new DatagramPacket(ackPacket.getPacket(), ackPacket.getPacket().length, address));

                    priorityQueue.put(recv_SEQ, dataDatagram);
                }

                break;

            case SYN_ACK:

                SynAckDatagram synackDatagram = new SynAckDatagram(coolDatagram);

                if(synackDatagram.getAck() == send_SEQ)
                {
                    recv_SEQ = synackDatagram.getSeq();

                    System.out.println("RECEIVED SYNACK: " + recv_SEQ + ", " + synackDatagram.getAck());

                    congestionWindow.ackPacket(synackDatagram.getAck());

                    ControlPacket handshakeFinal = new ControlPacket(PacketTypesProtocol.ACK);
                    send_ACK = recv_SEQ + ConstantDefinitions.SYNACK_SIZE;
                    handshakeFinal.addAcknowledgment(send_ACK);
                    sendAcknowledgment(new DatagramPacket(handshakeFinal.getPacket(), handshakeFinal.getPacket().length, address));

                    System.out.println("SENT ACK: " + send_ACK);

                    current_SEQ = send_ACK;
                    connection_established = true;

                    synchronized (synNotify) {
                        synNotify.notify();
                    }
                    synchronized (seqNotify) {
                        seqNotify.notify();
                    }
                }

                break;

            case FIN:
                if(connection_established)
                {
                    FinDatagram finDatagram = new FinDatagram(coolDatagram);

                    System.out.println("Received FIN SEQ: " + finDatagram.getSeq());
                    System.out.println("CURRENT Sent ACK: " + send_ACK);

                    if(finDatagram.getSeq() == send_ACK)
                    {
                        ControlPacket confirmFin = new ControlPacket(PacketTypesProtocol.ACK);
                        send_ACK += ConstantDefinitions.FIN_SIZE;
                        confirmFin.addAcknowledgment(send_ACK);
                        sendAcknowledgment(new DatagramPacket(confirmFin.getPacket(), confirmFin.getPacket().length, address));

                        if(started_fin && !response_fin)
                        {
                            connection_established = false;
                            connection_active = false;

                            try {

                                sendInStream.close();
                                sendOutStream.close();
                                receiveInStream.close();
                                receiveOutStream.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            client.disconnect(this);
                            System.out.println("CONNECTION DISCONNECTED");
                        }

                        if(!started_fin)
                        {
                            ControlPacket finFinal = new ControlPacket(PacketTypesProtocol.FIN);
                            finFinal.addSequence(send_SEQ);
                            send_SEQ = send_SEQ + ConstantDefinitions.FIN_SIZE;
                            sendPacket(new DatagramPacket(finFinal.getPacket(), finFinal.getPacket().length, address));

                            response_fin = true;
                            started_fin = true;
                        }
                    }
                }
                break;
            case UNKNOWN:
                System.out.println("what is going on");
                break;
        }

        return false;
    }

    public void disconnect()
    {
        ControlPacket starFin = new ControlPacket(PacketTypesProtocol.FIN);
        starFin.addSequence(send_SEQ);
        send_SEQ = send_SEQ + ConstantDefinitions.FIN_SIZE;
        sendPacket(new DatagramPacket(starFin.getPacket(), starFin.getPacket().length, address));

        started_fin = true;
        System.out.println("SENT DC REQUEST");
    }

    protected boolean connect()
    {
        ControlPacket handshakeStart = new ControlPacket(PacketTypesProtocol.SYN);
        send_SEQ = getRandom();
        handshakeStart.addSequence(send_SEQ);
        send_SEQ += ConstantDefinitions.SYN_SIZE;
        sendPacket(new DatagramPacket(handshakeStart.getPacket(), handshakeStart.getPacket().length, address));


        try {
            synchronized (synNotify) {
                synNotify.wait();
            }

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
        return (int) ((Math.random() * Integer.MAX_VALUE) / ConstantDefinitions.RANDOM_FACTOR);
    }

    private void sendAcknowledgment(DatagramPacket sendDatagram)
    {
        client.sendingQueue.add(sendDatagram);
    }

    private void sendPacket(DatagramPacket sendDatagram)
    {
        client.sendingQueue.add(sendDatagram);
        CoolDatagram coolDatagram = new CoolDatagram(sendDatagram);
        switch (coolDatagram.getPacketType()) {
            case SYN:
                coolDatagram = new SynDatagram(coolDatagram);
                break;
            case DATA:
                coolDatagram = new DataDatagram(coolDatagram);
                break;
            case SYN_ACK:
                coolDatagram = new SynAckDatagram(coolDatagram);
                break;
            case FIN:
                coolDatagram = new FinDatagram(coolDatagram);
                break;
            default:
                coolDatagram = null;
                System.err.println("unknown Packet Type");
                break;
        }

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

    public boolean isActive()
    {
        return connection_active;
    }
    public class CoolPipedInputStream extends PipedInputStream
    {
        CoolPipedInputStream(PipedOutputStream out, int size) throws IOException {
            super(out, size);
        }

        @Override
        public synchronized int read() throws IOException
        {
            coolEnqueueing(1);
            return super.read();
        }

        @Override
        public int read(byte[] b) throws IOException
        {
            coolEnqueueing(b.length);
            return super.read(b);
        }

        @Override
        public synchronized int read(byte[] b, int off, int len) throws IOException
        {
            coolEnqueueing(len);
            return super.read(b, off, len);
        }

        private void coolEnqueueing(int len) throws IOException {
            if(current_SEQ == 0)
            {
                synchronized (seqNotify) {
                    try {
                        seqNotify.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            while(true) {
                while (priorityQueue.containsKey(current_SEQ) && this.available() < len) {
                    if (ConstantDefinitions.INITIAL_WINDOW_SIZE - this.available() < priorityQueue.get(current_SEQ).getLength()) {
                        break;
                    }

                    readComplete = true;
                    receiveOutStream.write(priorityQueue.get(current_SEQ).getData().array());
                    current_SEQ = current_SEQ + priorityQueue.remove(current_SEQ).getLength();
                }

                if(readComplete)
                {
                    readComplete = false;
                    break;
                }
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
            write(b, 0, b.length);
        }

        @Override
        public void write(int b) throws IOException {
            byte[] barr = new byte[1];
            barr[0] = (byte) b;
            write(barr, 0 ,1);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {

            super.write(b, off, len);
            int max_len = 0;

            if(len > ConstantDefinitions.MAX_PACKETSIZE - ConstantDefinitions.DATA_OVERHEAD)
            {
                while(len > 0)
                {
                    max_len = len;

                    if(max_len >= ConstantDefinitions.MAX_PACKETSIZE - ConstantDefinitions.DATA_OVERHEAD)
                    {
                        max_len = ConstantDefinitions.MAX_PACKETSIZE - ConstantDefinitions.DATA_OVERHEAD;
                    }

                    byte[] barr = new byte[max_len];

                    int read = sendInStream.read(barr, 0, max_len);

                    DataPacket dataPacket = new DataPacket(max_len + ConstantDefinitions.DATA_OVERHEAD);
                    dataPacket.addSequence(send_SEQ);
                    dataPacket.addData(barr, read);

                    System.out.println(dataPacket.getPacket().toString());

                    sendPacket(new DatagramPacket(dataPacket.getPacket(), dataPacket.getPacket().length, address));

                    System.out.println("SEND SEQ: " + send_SEQ);

                    send_SEQ = send_SEQ + read + ConstantDefinitions.DATA_OVERHEAD;

                    len = len - max_len;
                }
            }
            else
            {
                byte[] barr = new byte[len];

                int read = sendInStream.read(barr, 0, len);

                DataPacket dataPacket = new DataPacket(len + ConstantDefinitions.DATA_OVERHEAD);
                dataPacket.addSequence(send_SEQ);
                dataPacket.addData(barr, read);

                System.out.println(dataPacket.getPacket().toString());

                sendPacket(new DatagramPacket(dataPacket.getPacket(), dataPacket.getPacket().length, address));

                System.out.println("SEND SEQ: " + send_SEQ);

                send_SEQ = send_SEQ + read + ConstantDefinitions.DATA_OVERHEAD;
            }
        }
    }

    protected class PacketDroppedObserver implements CongestionWindow.Observer
    {
        @Override
        public void onPacketDropped(CoolDatagram coolDatagram)
        {
            System.out.println("PACKET DROPPED");

            if(coolDatagram.getPacketType() == PacketTypesProtocol.SYN)
            {
                connection_established = false;
                synchronized (synNotify) {
                    synNotify.notify();
                }
            }
            else
            {
                sendPacket(coolDatagram.getUdpPacket());
            }
        }
    }


}
