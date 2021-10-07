package phillip.stockinfo4j.errorhandle.enums;

import phillip.stockinfo4j.errorhandle.IBaseErrorInfo;

public enum ErrorEnum implements IBaseErrorInfo {
    Success("0000", "成功"),
    FailedToReadFile("0001", "讀取檔案失敗"),
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
