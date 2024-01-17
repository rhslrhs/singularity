package com.singularity.base.vo;

import com.singularity.base.utils.JsonUtils;

public class BaseResVo {
    public <T> T toDto(Class<T> dtoClass) {
        return JsonUtils.convertType(this, dtoClass);
    }
}
