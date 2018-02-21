package dcheungaa.prepaid3211.Card.Memories;

/**
 * Created by Daniel on 14/2/2018.
 * Memory
 *
 * For Mifare Ultralight C, data are stored in pages, each page consists of 4 bytes
 */

public class Memory {
    /**
     * Positive integer storing the starting page index.
     */
    public int page;
    /**
     * Positive index from 0 to 3 storing the starting byte index within the page.
     */
    public int index;
    /**
     * Positive number storing the length of memories to be read.
     */
    public int byteLength;
    /**
     * Byte array storing the data.
     */
    protected byte[] bytes;

    public Memory(int page, int index, int byteLength) {
        this.page = page;
        this.index = index;
        this.byteLength = byteLength;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getPageLength() {
        return ((int) Math.ceil((byteLength + index) / 4.0));
    }

    public int getEndPage() {
        return page + getPageLength() - 1;
    }

    /**
     * Writes a consecutive byte array to memory
     * @param page
     * @param index
     * @param bytes
     * @return
     */
    public static boolean writeMemory(int page, int index, byte[] bytes) {
        int i = 0;
        int currentPageIndex = index;
        while (i < bytes.length) {
            if (currentPageIndex > 0) {
                //require merging data
                byte[] pageBytes = new byte[4];

            }
        }
        return false;
    }
}
