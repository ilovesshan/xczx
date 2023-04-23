package com.xczx.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xczx.system.mapper.DictionaryMapper;
import com.xczx.system.model.po.Dictionary;
import com.xczx.system.service.DictionaryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author ilovesshan
 */

@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {

    @Override
    public List<Dictionary> queryAll() {
        return this.list();
    }

    @Override
    public Dictionary getByCode(String code) {
        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dictionary::getCode, code);
        return this.getOne(queryWrapper);
    }
}
