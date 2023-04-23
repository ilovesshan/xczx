package com.xczx.system.service;

import com.xczx.system.model.po.Dictionary;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author ilovesshan
 * @since 2023-04-23
 */
public interface DictionaryService extends IService<Dictionary> {

    List<Dictionary> queryAll();

    Dictionary getByCode(String code);
}
