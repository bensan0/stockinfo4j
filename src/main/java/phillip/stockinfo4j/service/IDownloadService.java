package phillip.stockinfo4j.service;

import org.springframework.stereotype.Service;
import phillip.stockinfo4j.model.DownloadReturn;

@Service
public interface IDownloadService {

    DownloadReturn getTWSE(String date);


}
