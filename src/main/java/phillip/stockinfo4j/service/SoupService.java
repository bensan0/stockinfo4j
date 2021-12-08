package phillip.stockinfo4j.service;

import phillip.stockinfo4j.model.dto.StockOtherInfoDTO;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface SoupService {

    CompletableFuture<StockOtherInfoDTO> getOtherInfo(String code) throws IOException;
}
