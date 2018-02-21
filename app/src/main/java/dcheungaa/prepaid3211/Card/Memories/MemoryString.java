package dcheungaa.prepaid3211.Card.Memories;

/**
 * Created by Daniel on 14/2/2018.
 */

public class MemoryString extends Memory {
    private String string = null;

    public MemoryString(int page, int index, int byteLength) {
        super(page, index, byteLength);
    }

    public String getString() {
        if (string != null)
            return string;
        return (string = new String(bytes));
    }

    public void setString(final String string) {
        this.string = string;
    }
}
