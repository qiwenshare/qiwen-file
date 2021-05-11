<p align="center">
	<a href="http://fileos.qiwenshare.com/"><img width="30%" src="https://images.gitee.com/uploads/images/2021/0511/141109_0a709933_947714.png" ></a>

</p>
<p align="center">
	<strong>基于Spring Boot + VUE CLI@3 框架开发的分布式文件系统，旨在为用户和企业提供一个简单、方便的文件存储方案，能够以完善的目录结构体系，对文件进行管理 。</strong>
</p>
<p align="center">
	<a target="_blank" href="https://baike.baidu.com/item/MIT%E8%AE%B8%E5%8F%AF%E8%AF%81/6671281?fr=aladdin">
        <img src="https://img.shields.io/:license-MIT-blue.svg" />
	</a>
	<a target="_blank" href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">
		<img src="https://img.shields.io/badge/JDK-8+-green.svg" />
	</a>
	<a target="_blank" href="https://gitee.com/mingSoft/MCMS/stargazers">
		<img src="https://gitee.com/qiwen-cloud/qiwen-file/badge/star.svg?theme=dark" alt='gitee star'/>
	</a>
</p>
<p align="center">
	<a href="http://pan.qiwenshare.com/" target="_blank">在线演示环境</a> &nbsp;|
	<a href="https://www.qiwenshare.com/essay/detail/169"  target="_blank">安装指导</a>&nbsp;|
        <a href="https://www.qiwenshare.com/essay/detail/324"  target="_blank">更新日志</a>
</p>

---

## 开源说明

系统 100%开源
本软件遵循 MIT 开源协议

**您可以在其基础上继续进行开发来完善其功能，成为本项目的贡献者之一**

**您也可以以该项目作为脚手架，进行其他项目的开发**

## 功能介绍

### 用户操作

1. 用户的登录和注册

### 文件操作

1. 用户可对目录结构进行增加，删除，修改
2. 提供文件的拖拽上传，下载，支持截图粘贴直接上传图片
3. 支持对 zip 文件和 rar 文件的在线解压缩
4. 支持文件和目录的移动和复制
5. 多文件格式分类查看
6. 支持阿里云 OSS 对象存储,FastDFS 存储
7. 增加分片上传，该功能同时支持本地存储和阿里云 OSS,FastDFS
8. 支持极速秒传功能，提高上传效率
9. 上传文件前台实时显示上传文件进度，上传速率，百分比等信息
10. 支持文件重命名
11. 可实时显示文件存储占用情况及总占用容量
12. 支持 ElasticSearch 文件搜索
13. 支持将文件分享给他人，并查看自己已分享过的文件列表
14. 提供图片在线预览、视频在线预览、音频在线预览、PDF、JSON、TXT 等常用文本文件在线预览，支持 office 在线预览
15. ……

## 源码地址

| 项目名称     | 源码地址                                                                                     |
| ------------ | -------------------------------------------------------------------------------------------- |
| 奇文网盘前端 | [https://gitee.com/qiwen-cloud/qiwen-file-web](https://gitee.com/qiwen-cloud/qiwen-file-web) |
| 奇文网盘后台 | [https://gitee.com/qiwen-cloud/qiwen-file](https://gitee.com/qiwen-cloud/qiwen-file)         |

## 网络拓扑图

![网络拓扑图](https://images.gitee.com/uploads/images/2021/0324/225520_d55b109e_947714.png '屏幕截图.png')

## 软件架构

该项目采用前后端分离的方式进行开发和部署,主要用到以下关键技术

**前端**：Element UI、Vue CLI@3、Node.js、Webpack

**后台**：Spring Boot、MyBatis、JPA、JWT

**数据库** : MySQL

**数据结构**：递归算法，树的遍历和插入...

## 使用说明

1、本项目为后端代码

2、下载前端代码，可以访问该地址进行拉取：[qiwen-file-web](https://gitee.com/qiwen-cloud/qiwen-file-web)

## 部署说明

请移步奇文社区查看 [手把手教你部署奇文网盘](https://www.qiwenshare.com/essay/detail/169)

## 部分功能截图

### 1. 网盘主页

#### 1.1 页面布局

- 左侧菜单栏区域：展示文件类型，点击可以分类查看文件，底部显示已占用存储空间。
- 顶部文件操作区域：包括对文件的操作按钮组、文件查看模式切换按钮组、设置文件显示列按钮。
- 右侧文件展示区域：包括面包屑导航栏——标识当前位于的目录；文件展示区域——展示形式会随文件查看模式而改变；底部分页组件。

![网盘主页](https://images.gitee.com/uploads/images/2021/0325/105935_225d3d46_1837873.png)

#### 1.2 布局调整功能

左侧菜单栏可折叠，表格操作列可折叠，可控制当前表格中列的显示和隐藏

![折叠功能](https://images.gitee.com/uploads/images/2021/0325/113631_cf57fc44_1837873.gif)

#### 1.3 批量操作功能

![批量操作](https://images.gitee.com/uploads/images/2021/0325/115913_ad3bbe67_1837873.gif)

### 2. 三种查看模式

文件查看支持三种展示模式（列表、网格和时间线模式）

#### 2.1 列表模式

![列表模式](https://images.gitee.com/uploads/images/2021/0325/120007_1d046ef5_1837873.png)

#### 2.2 网格模式

![网格模式](https://images.gitee.com/uploads/images/2021/0325/123609_e4868707_1837873.png)

#### 2.3 时间线模式

![时间线模式](https://images.gitee.com/uploads/images/2021/0325/123638_3316ce8f_1837873.png)

### 3. 创建文件夹

![创建文件夹](https://images.gitee.com/uploads/images/2021/0325/123704_145604f0_1837873.png)

### 4. 文件图标大小调整

在网格模式和时间线模式下，支持手动调整图标大小：

![图标大小调整](https://images.gitee.com/uploads/images/2021/0409/181840_bb0dcb17_1837873.gif)

### 5. 文件移动

![文件移动](https://images.gitee.com/uploads/images/2021/0325/123752_14364633_1837873.png)

### 6. 文件在线解压缩

![文件在线解压缩](https://images.gitee.com/uploads/images/2021/0325/123823_aba673e8_1837873.png)

### 7. 文件上传三种方式

#### 7.1 文件分片上传

文件采用**分片上传**，集成了[simiple-uplader](https://github.com/simple-uploader/Uploader/blob/develop/README_zh-CN.md#uploader)的文件**秒传**、**断点续传**功能。
![文件上传](https://images.gitee.com/uploads/images/2021/0325/144103_08f4902b_1837873.png)

#### 7.2 拖拽上传

![文件拖拽上传](https://images.gitee.com/uploads/images/2021/0416/143326_1353ea6a_1837873.gif)

#### 7.3 截图粘贴上传

直接使用任何截图工具截图后，在拖拽区域使用 Ctrl + V 粘贴图片，点击上传图片即可上传。

![截图粘贴上传](https://images.gitee.com/uploads/images/2021/0416/143216_c7be7797_1837873.png)

### 8. 文件回收站

![文件回收站](https://images.gitee.com/uploads/images/2021/0325/123843_f8fa15bf_1837873.png)

### 9. 文件单个和批量分享

1. 支持单个和批量分享文件给他人：

   ![单个或批量分享文件](https://images.gitee.com/uploads/images/2021/0412/094958_842ead78_1837873.png)

2. 可以选择过期时间和是否需要提取码：

   ![过期时间和是否需要提取码](https://images.gitee.com/uploads/images/2021/0412/095039_b7841dbc_1837873.png)

3. 提供快捷复制链接及提取码给他人：

   ![生成分享链接](https://images.gitee.com/uploads/images/2021/0412/095055_a06df014_1837873.png)

   粘贴分享链接及提取码效果：

   ```
   分享链接：https://pan.qiwenshare.com/share/27d9b438019e4f68bcec02f579d163b7
   提取码：356978
   复制链接到浏览器中并输入提取码即可查看文件
   ```

4. 他人查看分享内容，并支持保存到网盘功能：

   ![查看他人分享](https://images.gitee.com/uploads/images/2021/0412/095629_ce5b3336_1837873.png)

### 10. 支持查看自己已分享过的文件列表

支持在列表中快捷复制当次的分享链接及提取码，并标注分享时间和过期状态：

![我的分享](https://images.gitee.com/uploads/images/2021/0412/095142_6d543701_1837873.png)

### 11. 视频在线预览

文件类型为视频时，点击即可打开预览窗口，展示播放列表，支持快进、后退、暂停、倍速播放、全屏播放、下载视频和折叠播放列表。

![视频在线预览](https://images.gitee.com/uploads/images/2021/0416/143120_c1ab2d82_1837873.png)

### 12. 音频在线播放

![音频在线播放](https://images.gitee.com/uploads/images/2021/0416/142347_1a09dd57_1837873.png)

## 联系我们

如您有问题，请加入 QQ 群咨询

**QQ 交流群** 和 **微信公众号** 请扫描下面二维码

<div style="dispaly: flex;">
    <img src="https://images.gitee.com/uploads/images/2021/0325/133721_7c174ea5_1837873.png"/>
    <img src="https://images.gitee.com/uploads/images/2021/0325/133740_557ca1d8_1837873.png"/>
</div>

## 鸣谢

此项目的开发离不开其他优秀开源项目的支持，在此感谢以下开源项目：

- [vue-simple-uploader](https://github.com/simple-uploader/vue-uploader/blob/master/README_zh-CN.md)：一款基于 [simple-uploader.js](https://github.com/simple-uploader/Uploader/blob/develop/README_zh-CN.md) 开发的适用于 Vue.js 的分片上传插件
- [vue-video-player](https://github.com/surmon-china/vue-video-player)：一款基于 [video.js](https://docs.videojs.com/) 开发的视频播放组件

## 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request

## 码云特技

1.  使用 Readme_XXX.md 来支持不同的语言，例如 Readme_en.md, Readme_zh.md
2.  码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5.  码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
