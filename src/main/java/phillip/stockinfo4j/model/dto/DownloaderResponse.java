package phillip.stockinfo4j.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import phillip.stockinfo4j.errorhandle.impl.ErrorEnum;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloaderResponse {
    private List<ErrorEnum> errorMessage = new ArrayList<>();
}
