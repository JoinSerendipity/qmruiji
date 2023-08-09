package com.qmkf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qmkf.domain.Category;

/**
 * Author：qm
 *
 * @Description：
 */

public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
