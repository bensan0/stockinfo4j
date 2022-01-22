package phillip.stockinfo4j.errorhandle;

import org.springframework.web.bind.annotation.ExceptionHandler;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;
import phillip.stockinfo4j.errorhandle.exceptions.*;
import phillip.stockinfo4j.model.dto.BasicRes;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {


    @ExceptionHandler(Exception.class)
    public BasicRes handleException(Exception e) {
        BasicRes resp = new BasicRes();
        resp.setErrorEnum(ErrorEnum.UnDefinedException);
        resp.setErrorDetail(e.getMessage());
        return resp;
    }

    @ExceptionHandler(RuntimeException.class)
    public BasicRes handleRuntimeException(RuntimeException e) {
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
    public BasicRes handleDateParseException(DateParseException e) {
        BasicRes resp = new BasicRes();
        resp.setErrorEnum(e.getErrorEnum());
        resp.setErrorDetail(e.getOriginalErrorMsg());
        return resp;
    }

    @ExceptionHandler(ExecutionException.class)
    public BasicRes handleExecutionException(ExecutionException e) {
        BasicRes resp = new BasicRes();
        resp.setErrorEnum(ErrorEnum.ExecutionError);
        resp.setErrorDetail(e.getMessage());
        return resp;
    }

    @ExceptionHandler(InterruptedException.class)
    public BasicRes handleInterruptedException(InterruptedException e) {
        BasicRes resp = new BasicRes();
        resp.setErrorEnum(ErrorEnum.ThreadInterrupted);
        resp.setErrorDetail(e.getMessage());
        return resp;
    }

    @ExceptionHandler(InvalidParamException.class)
    public BasicRes handleInvalidParamException(InvalidParamException e) {
        BasicRes resp = new BasicRes();
        resp.setErrorEnum(ErrorEnum.InValidParam);
        resp.setErrorDetail(e.getMessage());
        return resp;
    }

    @ExceptionHandler(BackupFailedException.class)
    public BasicRes handleBackupFailedException(BackupFailedException e){
        BasicRes resp = new BasicRes();
        resp.setErrorEnum(e.getErrorEnum());
        resp.setErrorDetail(e.getOriginalErrorMsg());
        return resp;
    }
}
