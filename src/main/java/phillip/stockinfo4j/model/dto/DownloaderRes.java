package phillip.stockinfo4j.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class DownloaderRes implements Serializable {
    private ErrorEnum errorMessage = ErrorEnum.Success;
    private String code = errorMessage.getCode();
    private String description = errorMessage.getDescription();
    private String errorDetail = "";

    public void setErrorMessage(ErrorEnum errorEnum){
        this.errorMessage = errorEnum;
        this.code = errorMessage.getCode();
        this.description = errorMessage.getDescription();
    }
}
