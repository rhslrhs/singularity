package com.singularity.base.dto;

import com.singularity.base.utils.JsonUtils;

public class BaseReqDto {
    public <T> T toVo(Class<T> dtoClass) {
        return JsonUtils.convertType(this, dtoClass);
    }
}
