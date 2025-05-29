package space.akko.platform.user.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import space.akko.platform.user.model.entity.UserCredential;

import java.util.List;

/**
 * 用户凭证Repository
 * 
 * @author akko
 * @since 1.0.0
 */
@Mapper
public interface UserCredentialRepository extends BaseMapper<UserCredential> {

    /**
     * 根据用户ID和凭证类型查找凭证
     */
    UserCredential findByUserIdAndType(@Param("userId") Long userId, 
                                      @Param("credentialType") String credentialType);

    /**
     * 根据用户ID查找所有凭证
     */
    List<UserCredential> findByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和凭证类型删除凭证
     */
    int deleteByUserIdAndType(@Param("userId") Long userId, 
                             @Param("credentialType") String credentialType);

    /**
     * 根据用户ID删除所有凭证
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 更新凭证值
     */
    int updateCredentialValue(@Param("userId") Long userId,
                             @Param("credentialType") String credentialType,
                             @Param("credentialValue") String credentialValue);
}
