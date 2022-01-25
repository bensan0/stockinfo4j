package phillip.stockinfo4j.Utils;

import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;
import phillip.stockinfo4j.errorhandle.exceptions.DateParseException;
import phillip.stockinfo4j.errorhandle.exceptions.DeleteFileException;
import phillip.stockinfo4j.errorhandle.exceptions.ReadFileException;

import java.io.*;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class OtherUtils {

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
     * @throws DateParseException
     */
    public static boolean isDateSaturdayOrSunday(String yyyyMMdd) throws DateParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate;
        try{
            localDate = LocalDate.parse(yyyyMMdd,formatter);
        }catch (DateTimeParseException e){
            throw new DateParseException(ErrorEnum.DateFormatNotAllowed, e.getMessage());
        }
        if(localDate.getDayOfWeek()==DayOfWeek.SUNDAY||localDate.getDayOfWeek()==DayOfWeek.SATURDAY){
            return true;
        }
        return false;
    }

    /**
     * 檢查時間是否為8位數字
     *
     * @param yyyyMMdd
     * @return
     * @throws DateParseException
     */
    public static boolean isDateConform(String yyyyMMdd){
        if (yyyyMMdd.trim().length() != 8) {
            return false;
        }
        try{
            Integer.parseInt(yyyyMMdd);
        }catch (NumberFormatException e){
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

    public static Long parseStrToLong(String str){
        Long result;
        try {
            result = Long.parseLong(str);
        } catch (NumberFormatException e) {
            return 0L;
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
        DecimalFormat df = new DecimalFormat("###########0.00");
        df.setRoundingMode(RoundingMode.DOWN);
        return df;
    }

    /**
     *
     * @param pattern
     * @return
     */
    public static DateTimeFormatter getDateTimeFormatter(String pattern){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern);
        return fmt;
    }

    /***
     *
     * @param date
     * @param pattern
     * @return
     */
    public static boolean isDateAfterToday(String date, String pattern){
        LocalDate inputDate = LocalDate.parse(date, getDateTimeFormatter(pattern));
        if(inputDate.isAfter(LocalDate.now())){
            return true;
        }
        return false;
    }

    /***
     * 驗證日期是否有效
     * @param date
     * @param pattern
     * @return
     */
    public static boolean isValidDate(String date, String pattern) {
        boolean convertSuccess = true;
        // 指定日期格式為四位年/兩位月份/兩位日期，注意yyyy/MM/dd區分大小寫；
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            // 設定lenient為false.
            // 否則SimpleDateFormat會比較寬鬆地驗證日期，比如2007/02/29會被接受，並轉換成2007/03/01
            format.setLenient(false);
            format.parse(date);
        } catch (ParseException | NullPointerException e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就說明格式不對
            convertSuccess = false;
        }
        return convertSuccess;
    }

    public static String getStackTrace(Throwable throwable) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        throwable.printStackTrace(printWriter);
        return result.toString();
    }
}
