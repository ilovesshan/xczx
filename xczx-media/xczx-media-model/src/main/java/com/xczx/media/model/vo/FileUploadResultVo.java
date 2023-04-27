package com.xczx.media.model.vo;

import com.xczx.media.model.po.MediaFiles;
import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/27
 * @description:
 */

@Data
public class FileUploadResultVo extends MediaFiles implements Serializable {
    private static final long serialVersionUID = 563326253184198824L;
}
