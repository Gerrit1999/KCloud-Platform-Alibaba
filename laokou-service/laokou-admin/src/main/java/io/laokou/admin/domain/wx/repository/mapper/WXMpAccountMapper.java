package io.laokou.admin.domain.wx.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.laokou.admin.domain.wx.entity.WXMpAccountDO;
import org.apache.ibatis.annotations.Mapper;

/**
* 公众号账号管理
*
* @author Kou Shenhai
*/
@Mapper
public interface WXMpAccountMapper extends BaseMapper<WXMpAccountDO> {
	
}