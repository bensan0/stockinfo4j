package phillip.stockinfo4j.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import phillip.stockinfo4j.errorhandle.enums.ErrorEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class DownloaderResponse implements Serializable {
    private ErrorEnum errorMessage = ErrorEnum.Success;
    private List<String> errorDetail = new ArrayList<>();
}
