package engine;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Pattern;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

public final class Utils {

    private Utils() {}

    public static String SHADERS_BASE_PATH = "src/main/resources/shaders";
    public static String TEXTURES_BASE_PATH = "src/main/resources/textures";

    public static String loadResource(String fileName) {
        String result = null;
        try (InputStream in = Class.forName(Utils.class.getName()).getResourceAsStream(fileName);
             Scanner scanner = new Scanner(in, String.valueOf(StandardCharsets.UTF_8))) {
            result = scanner.useDelimiter(Pattern.compile("(\\A)")).next();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String readFileAsString(String filename) {
        StringBuilder source = new StringBuilder();

        try(FileInputStream in = new FileInputStream(filename); BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8.name()))) {
            String line;
            while ((line = reader.readLine())!= null) {
                source.append(line).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return source.toString();
    }

    //Warning, this version uses manual memory allocation/free for the byte buffer
    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;
        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = memAlloc((int)fc.size() + 1);
                while (true){
                    if (fc.read(buffer) == -1) break;
                }
            }
        } else {
            try (InputStream source = Utils.class.getClassLoader().getResourceAsStream(resource);
                 ReadableByteChannel rbc = Channels.newChannel(source)) {
                buffer = memAlloc(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = resizeBuffer((ByteBuffer) buffer.flip(), buffer.capacity() * 3 / 2); // 50%
                    }
                }
            }
        }
        buffer.flip();
        return buffer.slice();
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = memAlloc(newCapacity);
        newBuffer.put(buffer);

        memFree(buffer);

        return newBuffer;
    }

    private static String decode(ByteBuffer buff) {
        return "";
    }
}