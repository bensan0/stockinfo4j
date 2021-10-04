package phillip.stockinfo4j.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class DownloadUtils {

    /**
     * 讀取並回傳檔案內容
     * @param filePath
     * @param codec 文件編碼(big5,utf-8...)
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
}
