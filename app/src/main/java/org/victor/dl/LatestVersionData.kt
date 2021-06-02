package org.victor.dl


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: LatestVersionData
 * Author: Victor
 * Date: 2021/6/2 17:34
 * Description: 
 * -----------------------------------------------------------------
 */
class LatestVersionData {
    var isUpdate: Boolean = true//是否更新
    var isForceUpdate: Boolean = false//是否强制更新
    var versionName: String? = "1.0.2"//版本号
    var appStoreUrl: String? = null//AppStore地址
    var appDownloadUrl: String? = "https://imtt.dd.qq.com/16891/apk/96881CC7639E84F35E86421691CBBA5D.apk?fsname=com.sina.weibo_11.1.3_4842.apk&csr=3554"//APP下载地址
    var appMd5: String? = "96881CC7639E84F35E86421691CBBA5D"//App MD5值
    var releaseNotes: String? = "1,上微博看明星热点新闻资讯;\n2,修复已知问题；\n3，优化用户体验"//版本说明
    var releaseTime: String? = null//发布时间
}