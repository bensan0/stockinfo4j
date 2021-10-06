package phillip.stockinfo4j.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DownloadUtils {

    /**
     * 讀取並回傳檔案內容
     *
     * @param filePath
     * @param codec    文件編碼(big5,utf-8...)
     * @return
     * @throws IOException
     */
    public static String readFileToString(String filePath, String codec) throws IOException {
        StringBuffer sb;
        FileInputStream fis;
        InputStreamReader isr = null;
        try {
            sb = new StringBuffer();
            fis = new FileInputStream(filePath);
            isr = new InputStreamReader(fis, codec);
            int i;
            while ((i = isr.read()) != -1) {
                sb.append((char) i);
            }
            isr.close();
            return sb.toString();
        } catch (IOException e) {
            throw e;
        } finally {
            isr.close();
        }
    }

    /**
     * 檢查傳入日期是否為星期六/星期日
     *
     * @param yyyyMMdd
     * @return
     */
    public static boolean isDateSaturdayOrSunday(String yyyyMMdd) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate = LocalDate.parse(yyyyMMdd, formatter);
        DayOfWeek dow = localDate.getDayOfWeek();
        if (dow.equals(DayOfWeek.SATURDAY) || dow.equals(DayOfWeek.SUNDAY)) {
            return true;
        }
        return false;
    }

    /**
     * 檢查時間是否符合格式yyyyMMdd
     *
     * @param yyyyMMdd
     * @return
     * @throws DateTimeParseException
     */
    public static boolean isDateConform(String yyyyMMdd) throws NumberFormatException {
        if (yyyyMMdd.trim().length() != 8) {
            return false;
        }
        Integer month = Integer.parseInt(yyyyMMdd.substring(4, 6));
        Integer day = Integer.parseInt(yyyyMMdd.substring(6, 8));
        if (month > 12 || day > 31) {
            return false;
        }
        return true;
    }
}
