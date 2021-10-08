package phillip.stockinfo4j.errorhandle.enums;

import phillip.stockinfo4j.errorhandle.IBaseErrorInfo;

public enum ErrorEnum implements IBaseErrorInfo {
    Success("0000", "成功"),
    FailedToReadFile("0001", "讀取檔案失敗"),
    DateFormatNotAllowed("0002", "日期格式不符"),
    ExecutionError("0003", "非同步回傳異常"),
    TreadInterrupted("0004", "執行緒中斷"),
    FailedSave("0005", "持久化異常"),
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
}
