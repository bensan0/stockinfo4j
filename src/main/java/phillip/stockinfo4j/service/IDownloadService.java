package phillip.stockinfo4j.service;

import org.springframework.stereotype.Service;
import phillip.stockinfo4j.model.DownloaderResponse;

@Service
public interface IDownloadService {

    DownloaderResponse getTWSE(String date);


}
