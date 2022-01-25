package phillip.stockinfo4j.errorhandle.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import phillip.stockinfo4j.errorhandle.IBaseErrorInfo;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorEnum implements IBaseErrorInfo {
    Success("0000", "成功"),
    FailedToReadFile("0001", "讀取檔案失敗"),
    UnDefinedException("0002", "未定義錯誤"),
    UDefinedRuntimeException("0003", "未定義運行錯誤"),
    DateFormatNotAllowed("0004", "日期格式不符"),
    ExecutionError("0005", "非同步回傳異常"),
    ThreadInterrupted("0006", "執行緒中斷"),
    FailedSave("0007", "持久化異常"),
    FailedToDeleteFile("0008", "刪除檔案錯誤"),
    InValidParam("0009", "參數異常"),
    BackupFailed("0010", "備份檔案失敗"),
    BackupFileIsExisted("0011", "檔案已存在"),
    NoData("0012", "查無資料"),
    PersistentFailed("0013", "持久化失敗"),
    ;

    private final String code;
    private final String description;

    ErrorEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }


    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @JsonCreator // This is the factory method and must be static
    public static ErrorEnum deserialize(@JsonProperty("code") String code, @JsonProperty("description") String description) {
        return Arrays.stream(ErrorEnum.values()).filter(ee -> ee.code.equals(code) && ee.description.equals(description)).findFirst().get();
    }
}
