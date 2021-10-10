package phillip.stockinfo4j.Utils;

import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;
import phillip.stockinfo4j.errorhandle.exceptions.DeleteFileException;
import phillip.stockinfo4j.errorhandle.exceptions.ReadFileException;

import java.io.*;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
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
    public static String readFileToString(String filePath, String codec) throws ReadFileException{
        StringBuffer sb;
        FileInputStream fis;
        InputStreamReader isr = null;
        try{
            try {
                sb = new StringBuffer();
                fis = new FileInputStream(filePath);
                isr = new InputStreamReader(fis, codec);
                int i;
                while ((i = isr.read()) != -1) {
                    sb.append((char) i);
                }
                return sb.toString();
            } finally {
                isr.close();
            }
        }catch (IOException e){
            throw new ReadFileException(ErrorEnum.FailedToReadFile, e.getMessage());
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

    /**
     * 刪除檔案
     *
     * @param filePath
     * @throws DeleteFileException
     */
    public static void deleteFile(String filePath) throws DeleteFileException {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            throw new DeleteFileException(ErrorEnum.FailedToDeleteFile, e.getMessage());
        }
    }

    /**
     * @param str
     * @return
     */
    public static Integer parseStrToInteger(String str) {
        Integer result;
        try {
            result = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
        return result;
    }

    /**
     * @param str
     * @return
     */
   public static Double parseStrToDouble(String str) {
        Double result;
        try {
            result = Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0.00;
        }
        return result;
    }

    /**
     * @return
     */
    public static DecimalFormat getDecimalFormat() {
        DecimalFormat df = new DecimalFormat("############.00");
        df.setRoundingMode(RoundingMode.DOWN);
        return df;
    }
}
