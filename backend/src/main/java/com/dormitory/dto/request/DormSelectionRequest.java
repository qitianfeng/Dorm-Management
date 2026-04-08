package com.dormitory.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 选宿舍请求DTO
 */
@Data
public class DormSelectionRequest {
    
    @NotNull(message = "房间ID不能为空")
    private Long roomId;
    
    @NotNull(message = "床位号不能为空")
    private String bedNumber;
    
    private String remark;
}
