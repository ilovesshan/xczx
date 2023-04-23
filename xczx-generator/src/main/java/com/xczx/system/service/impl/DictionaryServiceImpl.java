package com.xczx.system.service.impl;

import com.xczx.system.model.po.Dictionary;
import com.xczx.system.mapper.DictionaryMapper;
import com.xczx.system.service.DictionaryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author ilovesshan
 */
@Slf4j
@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {

}
