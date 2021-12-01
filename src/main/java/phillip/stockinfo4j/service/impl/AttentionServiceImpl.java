package phillip.stockinfo4j.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phillip.stockinfo4j.Utils.DownloadUtils;
import phillip.stockinfo4j.model.dto.AttentionDTO;
import phillip.stockinfo4j.model.dto.AttentionReq;
import phillip.stockinfo4j.model.pojo.Attention;
import phillip.stockinfo4j.repository.AttentionRepo;
import phillip.stockinfo4j.service.AttentionService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

@Service
public class AttentionServiceImpl implements AttentionService {

    @Autowired
    private AttentionRepo attentionRepo;

    @PersistenceContext
    private EntityManager em;

    public List<AttentionDTO> getAllAttention() {
        String qstr = "select a.code as code, a.note as note, a.join_date as date, b.name as name, b.industry as industry " +
                "from attention a, stock_basic_info b " +
                "where a.code = b.code order by a.id desc";
        List<AttentionDTO> resultList = em.createNativeQuery(qstr, "AttentionDTOResult").getResultList();
        return resultList;
    }

    public void addAttention(AttentionReq req) {
        Attention attention = new Attention();
        attention.setCode(req.getCode());
        attention.setNote(req.getNote());
        String today = LocalDate.now().format(DownloadUtils.getDateTimeFormatter("yyyyMMdd"));
        attention.setJoinDate(DownloadUtils.parseStrToInteger(today));
        attentionRepo.save(attention);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delAttention(String code) {
        attentionRepo.delByCode(code);
    }
}
