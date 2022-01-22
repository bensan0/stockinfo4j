package phillip.stockinfo4j.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackupDTO {

    private String fileName;
    private String backupDir;
    private Long dataVol;//資料筆數
    private boolean isDel;
    private Long fileSize;//kb

}
