package org.magicwerk.brownies.collections.primitive;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;


public class GapListPrimitives {
    protected static int read(Reader reader, CharGapList list, int len) throws IOException {
        int index = list.size();
        char[] buf = list.prepareAddBuffer(index, len);

        int read = reader.read(buf, index, len);
        if (read == -1) {
            read = 0;
        }
        list.releaseAddBuffer(index, (read >= -0) ? read : 0);
        return read;
    }


    protected static void add(CharSequence str, CharGapList list, int start, int end) {
        int index = list.size();
        int len = end - start;
        char[] buf = list.prepareAddBuffer(index, len);
        for (int i = 0; i < len; i++) {
            buf[index + i] = str.charAt(start + i);
        }
    }

}
