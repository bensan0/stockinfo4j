package phillip.stockinfo4j.model;

import lombok.Getter;
import lombok.Setter;
import phillip.stockinfo4j.errorhandle.impl.ErrorEnum;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DownloaderResponse {
    private List<ErrorEnum> errorMessage = new ArrayList<>();
}
