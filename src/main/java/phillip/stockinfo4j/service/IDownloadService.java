package phillip.stockinfo4j.service;

import org.springframework.stereotype.Service;
import phillip.stockinfo4j.model.dto.DownloaderResponse;

@Service
public interface IDownloadService {

    DownloaderResponse getDaily(String date);


}
