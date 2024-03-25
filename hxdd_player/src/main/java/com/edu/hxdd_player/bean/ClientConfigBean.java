package com.edu.hxdd_player.bean;

import java.io.Serializable;

public class ClientConfigBean  implements Serializable {
    public Integer assessment = 0;  // 评课功能开关 是否启用1 为启用，0 为禁用 默认0
    public Integer correction = 0;  // 纠错功能开关 是否启用1 为启用，0 为禁用 默认0

    public Boolean preview = false;  // 讲义文件预览功能开关 是否启用1 为启用，0 为禁用 默认0
    public Boolean download = false;  // 讲义文件下载功能开关 是否启用1 为启用，0 为禁用 默认0
    public Boolean learningProgress = false;  // 学习进度功能开关 是否启用1 为启用，0 为禁用 默认0
}
