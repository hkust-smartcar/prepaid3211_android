package dcheungaa.prepaid3211.Records.TypeConverters;

import android.arch.persistence.room.TypeConverter;
import java.util.Date;

/**
 * Created by Daniel on 5/2/2018.
 *
 * Converter for converting between Date and UNIX time in seconds
 */

public class DateTypeConverter {
    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date((long)value * 1000);
    }

    @TypeConverter
    public static Long toLong(Date value) {
        return value == null ? null : value.getTime() / 1000;
    }
}
