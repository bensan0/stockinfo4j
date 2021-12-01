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
     * 檢查時間是否符合格式yyyyMMdd
     *
     * @param yyyyMMdd
     * @return
     * @throws DateParseException
     */
    public static boolean isDateConform(String yyyyMMdd) throws DateParseException {
        if (yyyyMMdd.trim().length() != 8) {
            return false;
        }
        Integer month;
        Integer day;
        try{
            month = Integer.parseInt(yyyyMMdd.substring(4, 6));
            day = Integer.parseInt(yyyyMMdd.substring(6, 8));
        }catch (NumberFormatException e){
            throw new DateParseException(ErrorEnum.DateFormatNotAllowed, e.getMessage());
        }

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
     * @param yyyyMMdd
     * @return
     */
    public static boolean isDateAfterToday(String yyyyMMdd){
        LocalDate inputDate = LocalDate.parse(yyyyMMdd, getDateTimeFormatter("yyyyMMdd"));
        if(inputDate.isAfter(LocalDate.now())){
            return true;
        }
        return false;
    }
}
