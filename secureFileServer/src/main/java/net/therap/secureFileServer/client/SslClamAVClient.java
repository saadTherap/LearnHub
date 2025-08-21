package net.therap.secureFileServer.client;

import fi.solita.clamav.ClamAVClient;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author tanvirhassan
 * @since 21/8/25
 */
public class SslClamAVClient extends ClamAVClient {

    private final String host;
    private final int port;

    public SslClamAVClient(String host, int port) {
        super(host, port); // still call parent constructor
        this.host = host;
        this.port = port;
    }

    @Override
    public byte[] scan(InputStream is) throws IOException {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port)) {
            socket.startHandshake();

            OutputStream outs = socket.getOutputStream();
            InputStream ins = socket.getInputStream();

            // Send zINSTREAM command
            outs.write("zINSTREAM\0".getBytes("ASCII"));
            outs.flush();

            byte[] chunk = new byte[2048];
            int read;
            while ((read = is.read(chunk)) >= 0) {
                outs.write(intToByteArray(read));
                outs.write(chunk, 0, read);
            }
            // Terminate with zero length
            outs.write(new byte[]{0,0,0,0});
            outs.flush();

            return readAll(ins);
        }
    }

    private static byte[] intToByteArray(int val) {
        return new byte[]{
                (byte) (val >>> 24),
                (byte) (val >>> 16),
                (byte) (val >>> 8),
                (byte) val
        };
    }

    private static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buf = new byte[2048];
        int read;
        while ((read = in.read(buf)) != -1) {
            bout.write(buf, 0, read);
            if (bout.size() >= 4 &&
                    bout.toByteArray()[bout.size() - 1] == 0) {
                break; // stop if null terminator found
            }
        }
        return bout.toByteArray();
    }
}
