package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;

public final class TaskImplementation implements FileEncoder {

    /**
     * @param finPath  where to read binary data from
     * @param foutPath where to write encoded data. if null, please create and use temporary file.
     * @return file to read encoded data from
     * @throws IOException is case of input/output errors
     */
    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {
        /* XXX: https://docs.oracle.com/javase/8/docs/api/java/io/File.html#deleteOnExit-- */

     int count = 0;

            File file = new File(finPath);

            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] array_input = new byte[(int) file.length()];
            fileInputStreamReader.read(array_input);
//
            //   System.out.println("Len_of_array" + array_input.length);

            int mask = 0x3F;
            int size = array_input.length;
            char[] ar = new char[((size + 2) / 3) * 4];
            int a = 0;
            int i = 0;

            while (i < size) {
                byte b0 = array_input[i++];
                System.out.println("byte0 " + b0);
                System.out.println("changed " + (b0 >> 2));
                System.out.println((b0 >> 2) & mask);

                byte b1 = (i < size) ? array_input[i++] : 0;

                byte b2 = (i < size) ? array_input[i++] : 0;


                ar[a++] = toBase64[(b0 >> 2) & mask];
                ar[a++] = toBase64[((b0 << 4) | ((b1 & 0xFF) >> 4)) & mask];
                ar[a++] = toBase64[((b1 << 2) | ((b2 & 0xFF) >> 6)) & mask];
                ar[a++] = toBase64[b2 & mask];
            }
            switch (size % 3) {
                case 1:
                    ar[--a] = '=';
                case 2:
                    ar[--a] = '=';
            }



            File f1 = new File((foutPath == null) ? "/tmp/Result.txt" : foutPath);
            try (FileWriter writer = new FileWriter(f1, false)) {
                // запись всей строки

                writer.write(new String(ar));
                // запись по символам
                writer.flush();
            } catch (IOException ex) {

                System.out.println(ex.getMessage());
            }
            return f1;

        
    }

    private static final char[] toBase64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    public static void main(String[] args) throws Exception {
        final FileEncoder encoder = new ReferenceTaskImplementation();
        // NOTE: open http://localhost:9000/ in your web browser
        (new Bootstrapper(args, encoder))
                .bootstrap("", new InetSocketAddress("127.0.0.1", 9000));
    }

}
