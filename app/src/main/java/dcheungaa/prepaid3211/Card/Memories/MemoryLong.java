package dcheungaa.prepaid3211.Card.Memories;

import java.nio.ByteBuffer;

/**
 * Created by Daniel on 14/2/2018.
 */

public class MemoryLong extends Memory {
    public final boolean unsigned;

    private Long number;

    public MemoryLong(int page, int index, int byteLength, boolean unsigned) {
        super(page, index, byteLength);
        this.unsigned = unsigned;
    }

    public long getSignedLong() {
        if (number != null)
            return number;
        return number = (long) (ByteBuffer.wrap(bytes)).getInt();
    }

    public long getUnsignedLong() {
        if (number != null)
            return number;
        return number = getSignedLong() & getUnsignedMask();
    }

    public long getLong() {
        if (number != null)
            return number;
        return number = unsigned ? getUnsignedLong() : getSignedLong();
    }

    private long getUnsignedMask() {
        return (1 << (byteLength * 8)) - 1;
    }
}
