package fiveavian.proxvc.vc;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.openal.ALC10.alcOpenDevice;

public class AudioInputDevice implements AutoCloseable {
    private static final int NUM_DEVICE_BUFFERS = 8;

    private final ByteBuffer samples = BufferUtils.createByteBuffer(VCProtocol.BUFFER_SIZE);
    private final IntBuffer ints = BufferUtils.createIntBuffer(1);
    private long device = alcOpenDevice((ByteBuffer)null);

    public static String[] getSpecifiers() {
        String result = null;
        result = ALC10.alcGetString(-1, ALC11.ALC_CAPTURE_DEVICE_SPECIFIER);
        return result == null ? new String[0] : result.split("\0");
    }

    public synchronized void open(String deviceName) {
        close();
        if (Objects.equals(deviceName, "none")) {
            device = -1;
        } else {
            device = ALC11.alcCaptureOpenDevice(
                    deviceName,
                    VCProtocol.SAMPLE_RATE,
                    AL10.AL_FORMAT_MONO16,
                    VCProtocol.SAMPLE_COUNT * NUM_DEVICE_BUFFERS
            );
            ALC11.alcCaptureStart(device);
        }
    }

    public synchronized boolean isClosed() {
        return device == -1;
    }

    public synchronized ByteBuffer pollSamples() {
        if (isClosed()) {
            return null;
        }
        ints.rewind();
        ALC10.alcGetInteger(device, ALC11.ALC_CAPTURE_SAMPLES);
        if (ints.get(0) < VCProtocol.SAMPLE_COUNT) {
            return null;
        }
        samples.rewind();
        ALC11.alcCaptureSamples(device, samples, VCProtocol.SAMPLE_COUNT);
        return samples;
    }

    @Override
    public synchronized void close() {
        if (isClosed())
            return;
        ALC11.alcCaptureStop(device);
        ALC11.alcCaptureCloseDevice(device);
    }
}
