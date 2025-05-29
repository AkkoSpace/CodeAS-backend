package space.akko.platform.user.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import space.akko.platform.user.model.vo.UserVO;

/**
 * 登录响应
 * 
 * @author akko
 * @since 1.0.0
 */
@Data
@Schema(description = "登录响应")
public class LoginResponse {

    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "刷新令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "访问令牌过期时间（秒）", example = "3600")
    private Long expiresIn;

    @Schema(description = "用户信息")
    private UserVO userInfo;
}
