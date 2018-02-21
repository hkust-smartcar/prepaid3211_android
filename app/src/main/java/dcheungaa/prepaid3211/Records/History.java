package dcheungaa.prepaid3211.Records;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import dcheungaa.prepaid3211.Records.TypeConverters.DateTypeConverter;

/**
 * Created by Daniel on 5/2/2018.
 *
 * History model for database records
 */

@Entity(tableName = "histories")
public class History {

    @PrimaryKey(autoGenerate = true)
    private int uid;
    private String itemName;
    private int itemCost;
    private Date purchasedAt;

    /**
     * Constructs a History record.
     * @param itemName Name of the item.
     * @param itemCost Integer cost of the item.
     * @param purchasedAt Purchased date to nearest second of UTC time.
     */
    public History(String itemName, int itemCost, Date purchasedAt) {
        this.uid = 0;
        this.itemName = itemName;
        this.itemCost = itemCost;
        this.purchasedAt = purchasedAt;
    }

    /**
     * Constructs a History record.
     * @param itemName Name of the item.
     * @param itemCost Integer cost of the item.
     * @param purchasedAt UNIX time in seconds of UTC time.
     */
    public History(String itemName, int itemCost, Long purchasedAt) {
        this.uid = 0;
        this.itemName = itemName;
        this.itemCost = itemCost;
        this.purchasedAt = DateTypeConverter.toDate(purchasedAt);
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemCost() {
        return itemCost;
    }

    public Date getPurchasedAt() {
        return purchasedAt;
    }

}
