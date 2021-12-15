package phillip.stockinfo4j.aop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogDTO implements Serializable {

    private String className;

    private String methodName;

    private String args;

    // 操作時間
    private Long startTime;

    // 消耗時間
    private Integer timeCost;

    private Throwable throwable;

    // 請求返回的結果
    private Object result;
}
