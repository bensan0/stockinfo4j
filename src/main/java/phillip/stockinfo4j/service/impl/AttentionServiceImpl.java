package phillip.stockinfo4j.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import phillip.stockinfo4j.Utils.DownloadUtils;
import phillip.stockinfo4j.model.dto.AttentionReq;
import phillip.stockinfo4j.model.pojo.Attention;
import phillip.stockinfo4j.repository.AttentionRepo;
import phillip.stockinfo4j.service.AttentionService;

import java.time.LocalDate;
import java.util.List;

@Service
public class AttentionServiceImpl implements AttentionService {

    @Autowired
    private AttentionRepo attentionRepo;

    public List<Attention> getAllAttention(){
        List<Attention> attentionList = attentionRepo.findAll();
        return attentionList;
    }

    public void addAttention(AttentionReq req){
        Attention attention = new Attention();
        attention.setCode(req.getCode());
        attention.setNote(req.getCode());
        String today = LocalDate.now().format(DownloadUtils.getDateTimeFormatter("yyyyMMdd"));
        attention.setJoinDate(DownloadUtils.parseStrToInteger(today));
        attentionRepo.save(attention);
    }

    public void delAttention(String code){
        attentionRepo.delByCode(code);
    }
}
