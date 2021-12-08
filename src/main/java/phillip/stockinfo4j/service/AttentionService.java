package phillip.stockinfo4j.service;

import phillip.stockinfo4j.model.dto.AttentionDTO;
import phillip.stockinfo4j.model.dto.AttentionReq;

import java.util.List;

public interface AttentionService {

    List<AttentionDTO> getAllAttention();
    void addAttention(AttentionReq req);
    void delAttention(String code);
}
