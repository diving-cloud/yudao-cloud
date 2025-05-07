package cn.iocoder.yudao.module.leaf.request; // Assuming location

import lombok.Data;
import jakarta.validation.constraints.NotNull; // Using jakarta validation
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RedPacketCreateRequest {

    // Assuming creatorId is obtained from the security context, not passed in request
    // private Long creatorId;

    @NotNull(message = "Total amount cannot be null")
    @Positive(message = "Total amount must be positive")
    private BigDecimal totalAmount;

    @NotNull(message = "Total count cannot be null")
    @Positive(message = "Total count must be positive")
    private Integer totalCount;

    // Optional: Target number of followers to trigger something
    private Integer followersTarget;

    @NotNull(message = "Start time cannot be null")
    private LocalDateTime startTime;

    @NotNull(message = "End time cannot be null")
    private LocalDateTime endTime;

    // Removed shards from request, they should be generated internally
    // private List<RedPacketShardDTO> shards;
}