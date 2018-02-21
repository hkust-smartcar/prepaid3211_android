package dcheungaa.prepaid3211.Card;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import dcheungaa.prepaid3211.Card.Memories.Memory;
import dcheungaa.prepaid3211.Card.Memories.MemoryLong;
import dcheungaa.prepaid3211.Card.Memories.MemoryString;

/**
 * Created by Daniel on 14/2/2018.
 * Card Info
 */

public class Info {

    public static final SparseArray<byte[]> readMemory = new SparseArray<>();

    public class PurchaseHistory {
        private final int PAGE_ALLOC_HISTORY = 10;

        private final int BYTE_ALLOC_TIMESTAMP = 4;
        private final int BYTE_ALLOC_PRICE = 2;
        private final int BYTE_ALLOC_PRODUCT_NAME = 34;

        private final int PAGE_OFFSET_TIMESTAMP = 0;
        private final int PAGE_OFFSET_PRICE = 1;
        private final int PAGE_OFFSET_PRODUCT_NAME = 2;

        private final int INDEX_OFFSET_TIMESTAMP = 0;
        private final int INDEX_OFFSET_PRICE = 0;
        private final int INDEX_OFFSET_PRODUCT_NAME = 2;

        public final MemoryLong timestamp;
        public final MemoryLong price;
        public final MemoryString productName;

        public PurchaseHistory(int historyIndex) {
            final int currentPage = purchaseHistoriesSector.page + historyIndex * PAGE_ALLOC_HISTORY;
            timestamp = new MemoryLong(
                    currentPage + PAGE_OFFSET_TIMESTAMP,
                    INDEX_OFFSET_TIMESTAMP,
                    BYTE_ALLOC_TIMESTAMP,
                    false);
            price = new MemoryLong(
                    currentPage + PAGE_OFFSET_PRICE,
                    INDEX_OFFSET_PRICE,
                    BYTE_ALLOC_PRICE,
                    false);
            productName = new MemoryString(
                    currentPage + PAGE_OFFSET_PRODUCT_NAME,
                    INDEX_OFFSET_PRODUCT_NAME,
                    BYTE_ALLOC_PRODUCT_NAME
            );
        }
    }

    public List<Memory> memoryList;
    public final PurchaseHistory[] purchaseHistories;
    public final Memory purchaseHistoriesSector = new Memory(0x0E, 0, 400);
    public final MemoryLong id = new MemoryLong(0x04, 0, 2, true);
    public final MemoryLong balance = new MemoryLong(0x04, 2, 2, false);
    public final MemoryString name = new MemoryString(0x05, 0, 32);
    public final MemoryLong checksum = new MemoryLong(0x0D, 0, 4, true);
    public final MemoryLong lastPurchaseCursor = new MemoryLong(0xE0, 0, 2, true);
    public final MemoryLong lastPurchaseTimestamp = new MemoryLong(0xE1, 0, 8, false);

    private final int TOTAL_HISTORIES = 10;
    private final int TOTAL_MEMORIES = 36;

    Info() {
        memoryList = new ArrayList<>(TOTAL_MEMORIES);

        purchaseHistories = new PurchaseHistory[TOTAL_HISTORIES];
        for (int i = 0; i < TOTAL_HISTORIES; i++) {
            final PurchaseHistory purchaseHistory = new PurchaseHistory(i);
            purchaseHistories[i] = purchaseHistory;
            memoryList.add(purchaseHistory.timestamp);
            memoryList.add(purchaseHistory.price);
            memoryList.add(purchaseHistory.productName);
        }

        memoryList.add(id);
        memoryList.add(balance);
        memoryList.add(name);
        memoryList.add(checksum);
        memoryList.add(lastPurchaseCursor);
        memoryList.add(lastPurchaseTimestamp);
    }
}
