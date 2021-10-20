package phillip.stockinfo4j.errorhandle;

import org.springframework.web.bind.annotation.ExceptionHandler;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;
import phillip.stockinfo4j.errorhandle.exceptions.CustomRuntimeException;
import phillip.stockinfo4j.errorhandle.exceptions.DateParseException;
import phillip.stockinfo4j.errorhandle.exceptions.DeleteFileException;
import phillip.stockinfo4j.errorhandle.exceptions.ReadFileException;
import phillip.stockinfo4j.model.dto.BasicRes;

import java.sql.SQLException;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {


    @ExceptionHandler(Exception.class)
    public BasicRes handleException(Exception e) {
        System.out.println("handleException");
        BasicRes resp = new BasicRes();
        resp.setErrorEnum(ErrorEnum.UnDefinedException);
        resp.setErrorDetail(e.getMessage());
        return resp;
    }

    @ExceptionHandler(RuntimeException.class)
    public BasicRes handleRuntimeException(RuntimeException e) {
        e.printStackTrace();
        BasicRes resp = new BasicRes();
        resp.setErrorEnum(ErrorEnum.UDefinedRuntimeException);
        resp.setErrorDetail(e.getMessage());
        return resp;
    }

    @ExceptionHandler({ReadFileException.class, DeleteFileException.class})
    public BasicRes handleCustomRuntimeException1(CustomRuntimeException e) {
        BasicRes resp = new BasicRes();
        resp.setErrorEnum(e.getErrorEnum());
        resp.setErrorDetail(e.getOriginalErrorMsg());
        return resp;
    }

    @ExceptionHandler({SQLException.class})
    public BasicRes handleSQLException(SQLException e){
        BasicRes resp = new BasicRes();
        resp.setErrorEnum(ErrorEnum.FailedSave);
        resp.setErrorDetail(e.getMessage());
        return resp;
    }

    @ExceptionHandler({DateParseException.class})
    public BasicRes handleCustomRuntimeException2(CustomRuntimeException e) {
        BasicRes resp = new BasicRes();
        resp.setErrorEnum(e.getErrorEnum());
        resp.setErrorDetail(e.getOriginalErrorMsg());
        return resp;
    }
}
