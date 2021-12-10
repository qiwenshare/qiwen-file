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
	<a href="https://pan.qiwenshare.com/docs/guide/deploying.html#%E9%83%A8%E7%BD%B2%E8%AF%B4%E6%98%8E"  target="_blank">安装指导</a>&nbsp;|
        <a href="https://pan.qiwenshare.com/docs/"  target="_blank">说明文档</a> |
    	<a href="https://www.qiwenshare.com/essay/detail/1190"  target="_blank">课程链接</a> |
        <a href="https://pan.qiwenshare.com/docs/log/backend.html"  target="_blank">更新日志</a>
</p>

---

## 开源说明

系统 100%开源
本软件遵循 MIT 开源协议

**您可以在其基础上继续进行开发来完善其功能，成为本项目的贡献者之一**

**您也可以以该项目作为脚手架，进行其他项目的开发**

## 功能介绍

### 用户操作

1. 用户注册
1. 用户登录

### 基本文件操作

| 操作   | 文件 | 文件夹 | 单个 | 批量 | 备注                                                                                                     |
| :----- | :--: | :----: | :--: | :--: | -------------------------------------------------------------------------------------------------------- |
| 创建   |  √   |   √    |  √   |  ⚪  | 创建 Word、Excel、PowerPoint 在线文件                                                                    |
| 删除   |  √   |   √    |  √   |  √   |                                                                                                          |
| 上传   |  √   |   √    |  √   |  √   | **拖拽**上传、**粘贴截图**上传                                                                           |
| 重命名 |  √   |   √    |  √   |  ⚪  |                                                                                                          |
| 移动   |  √   |   √    |  √   |  √   |                                                                                                          |
| 复制   |  √   |   ⚪   |  √   |  ⚪  |                                                                                                          |
| 解压缩 |  √   |   ⚪   |  √   |  ⚪  | **ZIP、RAR**                                                                                             |
| 预览   |  √   |   ⚪   |  √   |  ⚪  | 支持图片、视频、音频在线预览<br />支持 PDF、JSON、TXT、HTML 等常用文本文件<br />支持 Office 文件在线预览 |
| 分享   |  √   |   √    |  √   |  √   | 支持有效期、提取码                                                                                       |
| 搜索   |  √   |   √    |  ⚪  |  ⚪  | 支持 ElasticSearch 文件名称模糊搜索                                                                      |

### 特色功能

| 功能              | 描述                                                                                                                                                                                                                                                                                                                                                      |
| ----------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| office 在线编辑   | Word、Excel、PowerPoint 文档的在线创建、**在线编辑**、协同编辑功能<br />集成 [OnlyOffice](https://api.onlyoffice.com/)，安装方式参考 [安装 ONLYOFFICE](https://www.qiwenshare.com/essay/detail/1208)                                                                                                                                                      |
| markdown 在线编辑 | 支持 **markdown** 文件在线预览、编辑、保存功能<br />集成 [mavon-editor](https://www.npmjs.com/package/mavon-editor) ，已内置到前端工程中                                                                                                                                                                                                                  |
| 代码在线编辑      | 支持 **C、C++、C#、Java、JavaScript、HTML、CSS、Less、Sass、Stylus** <br />等常用代码类文件的在线预览、编辑、保存<br />集成 [vue-codemirror](https://github.com/surmon-china/vue-codemirror)，已内置到前端工程中，<br />可参考 [codemirror](https://codemirror.net/index.html) 官网说明添加更多语言                                                       |
| 文件分类查看      | 图片、视频、音乐、文档、其他，分类查看更快捷                                                                                                                                                                                                                                                                                                              |
| 多种查看模式      | 支持网格模式、列表模式、时间线模式<br />网格模式下图标支持手动控制显示大小                                                                                                                                                                                                                                                                                |
| 回收站            | 删除文件自动移入回收站，支持在回收站中彻底删除、还原文件                                                                                                                                                                                                                                                                                                  |
| 多种存储方式      | 基于奇文社区自研框架 [UFOP](https://gitee.com/qiwen-cloud/ufop-spring-boot-starter)，实现文件多样化存储。<br/>支持**本地**磁盘、**阿里云 OSS** 对象存储、**FastDFS** 存储、**MinIO** 存储、<br />**七牛云 KODO** 对象存储，点击查看配置方式[存储方式配置](https://pan.qiwenshare.com/docs/config/#%E5%AD%98%E5%82%A8%E6%96%B9%E5%BC%8F%E9%85%8D%E7%BD%AE) |
| 支持分片上传      | 基于奇文社区自研框架 [UFOP](https://gitee.com/qiwen-cloud/ufop-spring-boot-starter), 实现文件分片上传。<br />集成优秀开源项目 [vue-simple-uploader](https://github.com/simple-uploader/vue-uploader/blob/master/README_zh-CN.md)                                                                                                                          |
| 支持极速秒传      | 计算文件 MD5，实现极速秒传效果，提高上传效率                                                                                                                                                                                                                                                                                                              |
| 支持断点续传      | 同一个文件，当上传过程中网络中断，可以从断点处继续上传                                                                                                                                                                                                                                                                                                    |
| 实时进度显示      | 页面实时显示上传文件进度、速度、结果等信息                                                                                                                                                                                                                                                                                                                |
| 存储容量显示      | 可实时显示文件存储占用情况及总存储容量                                                                                                                                                                                                                                                                                                                    |

## 源码地址

| 项目名称     | 源码地址                                                                                     |
| ------------ | -------------------------------------------------------------------------------------------- |
| 奇文网盘前端 | [https://gitee.com/qiwen-cloud/qiwen-file-web](https://gitee.com/qiwen-cloud/qiwen-file-web) |
| 奇文网盘后台 | [https://gitee.com/qiwen-cloud/qiwen-file](https://gitee.com/qiwen-cloud/qiwen-file)         |

## 网络拓扑图

![网络拓扑图](https://pan.qiwenshare.com/docs/img/guide/web-expand.png)

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

- 左侧分类栏区域：展示文件类型，分为我的文件、回收站和我的分享三大类，切换分类可以查看文件，底部显示已占用存储空间。
  1. 点击左侧分类栏中的**全部**，右侧文件列表会随面包屑导航栏中的当前位置变化而变化，调用后台接口，传参当前位置 & 分页数据，获取当前路径下 & 当前页的文件列表。
  2. 点击左侧分类栏中的**图片、文档、视频、音乐、其他**，面包屑导航栏将显示当前文件类型，右侧文件列表会随左侧分类栏的切换而变化，调用后台接口，传参当前点击的文件类型 & 分页数据，获取当前文件类型 & 当前页的文件列表。
  3. 点击左侧分类栏中的**回收站**，右侧文件列表显示回收站中的文件。
  4. 点击左侧分类栏中的**我的分享**，右侧文件列表显示个人分享过的文件。
- 顶部文件操作区域：包括对文件的操作按钮组、文件查看模式切换按钮组、设置文件显示列按钮
- 中间面包屑导航栏：标识当前位于的目录。点击层级，可以回到任意一层目录；**点击面包屑导航栏后面的空白处，可以手动输入路径以便快速进入指定目录**。
- 右侧文件展示区域：展示形式会随文件查看模式而改变；底部分页组件。

<img src="https://pan.qiwenshare.com/docs/img/guide/function/home.png" alt="网盘主页">

#### 1.2 布局调整功能

左侧菜单栏可折叠，可控制当前表格中列的显示和隐藏

<img src="https://pan.qiwenshare.com/docs/img/guide/function/fold.gif" alt="折叠功能">

#### 1.3 文件图标大小调整

在网格模式和时间线模式下，支持手动调整图标大小：

<img src="https://pan.qiwenshare.com/docs/img/guide/function/adjustIconSize.gif" alt="图标大小调整">

### 2. 路径导航

点击目录跳转到该文件夹内部，在面包屑导航栏后面空白处点击，可以**输入路径**，快速到达指定路径（此功能仅支持在 **我的文件 - 全部** 分类下使用）

<img src="https://pan.qiwenshare.com/docs/img/guide/function/breadCrumb.gif" alt="路径导航">

### 3. 三种查看模式

文件查看支持三种展示模式：列表、网格和时间线模式

#### 3.1 列表模式

<img src="https://pan.qiwenshare.com/docs/img/guide/function/list.png" alt="列表模式">

#### 3.2 网格模式

<img src="https://pan.qiwenshare.com/docs/img/guide/function/grid.png" alt="网格模式">

#### 3.3 时间线模式

时间线模式目前仅在左侧分类栏选择图片时才支持，我们会尽快支持其他类型的文件

<img src="https://pan.qiwenshare.com/docs/img/guide/function/timeline.png" alt="时间线模式">

### 4. 文件操作

文件操作结合了电脑客户端的操作方式，支持任何文件右键唤起操作列表，或勾选文件并点击顶部相关批量操作按钮。

#### 4.1 新建文件夹

<img src="https://pan.qiwenshare.com/docs/img/guide/function/createFolder.gif" alt="创建文件夹">

#### 4.2 文件移动

支持文件单个和批量移动，选择目录后，点击确定即可移动文件到目标路径，同时在弹框中提供新建文件夹功能。

<img src="https://pan.qiwenshare.com/docs/img/guide/function/moveFile.gif" alt="文件移动">

#### 4.3 文件在线解压缩

支持 ZIP 和 RAR 格式的文件在线解压缩，支持三种解压方式：

1. 解压到当前文件夹
2. 解压到以当前压缩文件命名的文件夹内
3. 解压到指定文件夹

<img src="https://pan.qiwenshare.com/docs/img/guide/function/unzip.gif" alt="文件在线解压缩">

#### 4.4 文件搜索

支持文件名搜索文件，搜索功能后台配置请查看顶部导航栏`配置-后台项目配置-文件搜索配置`

<img src="https://pan.qiwenshare.com/docs/img/guide/function/search.gif" alt="文件搜索">

#### 4.5 批量操作功能

在列表和网格模式下，提供了批量操作功能，可以对文件进行批量删除、移动和下载。

<img src="https://pan.qiwenshare.com/docs/img/guide/function/batch.gif" alt="批量操作">

### 5. 三种文件上传方式

#### 5.1 文件 & 文件夹分片上传

支持**文件**和**文件夹**上传。文件采用**分片上传**，集成了 [simiple-uplader](https://github.com/simple-uploader/Uploader/blob/develop/README_zh-CN.md#uploader) 的文件**秒传**、**断点续传**功能，此插件的具体配置项可以查看该项目的官方文档。
<img src="https://pan.qiwenshare.com/docs/img/guide/function/uploadFileAndFold.gif" alt="文件上传">

#### 5.2 拖拽上传

支持全屏区域拖拽上传文件。

<img src="https://pan.qiwenshare.com/docs/img/guide/function/uploadFileDrop.gif" alt="文件拖拽上传">

#### 5.3 截图粘贴上传

直接使用任何截图工具截图后，在拖拽区域使用 Ctrl + V 粘贴图片，点击上传图片即可上传。

<img src="https://pan.qiwenshare.com/docs/img/guide/function/pasteUpload.png" alt="截图粘贴上传">

### 6. 文件回收站

提供文件回收站功能，支持彻底删除和还原文件。

<img src="https://pan.qiwenshare.com/docs/img/guide/function/recycle.png" alt="文件回收站">

### 7. 文件分享

#### 7.1 单个或批量文件分享

1. 支持单个和批量分享文件给他人：

   <img src="https://pan.qiwenshare.com/docs/img/guide/function/share.png" alt="单个或批量分享文件">

2. 可以选择过期时间和是否需要提取码：

   <img src="https://pan.qiwenshare.com/docs/img/guide/function/selectDate.png" alt="过期时间和是否需要提取码">

3. 提供快捷复制链接及提取码给他人：

   <img src="https://pan.qiwenshare.com/docs/img/guide/function/copyLink.png" alt="生成分享链接">

   粘贴分享链接及提取码效果：

   ```
   分享链接：http://localhost:8080/share/363196ac9fd94371b9f47cb24f042d9f
   提取码：967617
   复制链接到浏览器中并输入提取码即可查看文件
   ```

4. 他人查看分享内容，并支持保存到网盘功能：

   <img src="https://pan.qiwenshare.com/docs/img/guide/function/saveShareFile.png" alt="查看他人分享">

#### 7.2 查看已分享过的文件列表

支持在列表中快捷复制当次的分享链接及提取码，并标注分享时间和过期状态：

<img src="https://pan.qiwenshare.com/docs/img/guide/function/shareList.png" alt="我的分享">

### 8. 文件在线预览 & 编辑

#### 8.1 office 在线预览 & 编辑

本地启动时，office 文件在线预览需要在本地搭建 [only office](https://www.qiwenshare.com/essay/detail/1208) 服务；
线上部署时，office 文件在线预览需要在服务器上搭建 [only office](https://www.qiwenshare.com/essay/detail/1208) 服务；

例如：word 文件在线预览：
<img src="https://pan.qiwenshare.com/docs/img/guide/function/preview.png" alt="文件在线预览">

例如：word 文件在线编辑：
<img src="https://pan.qiwenshare.com/docs/img/guide/function/edit.png" alt="文件在线编辑">

#### 8.2 markdown 在线预览 & 编辑

支持 **markdown** 文件在线预览、编辑、保存功能，集成 [mavon-editor](https://www.npmjs.com/package/mavon-editor) ，已内置到前端工程中

<img src="https://pan.qiwenshare.com/docs/img/guide/function/markdown.png" alt="mavon-editor 代码编辑器">

#### 8.3 代码类文件在线预览 & 编辑

支持 **C、C++、C#、Java、JavaScript、HTML、CSS、Less、Sass、Stylus ……** 等常用代码类文件的在线预览、编辑、保存

集成 [vue-codemirror](https://github.com/surmon-china/vue-codemirror)，已内置到前端工程中，可参考 [codemirror](https://codemirror.net/index.html) 官网说明添加更多语言

<img src="https://pan.qiwenshare.com/docs/img/guide/function/codemirror.png" alt="codemirror 代码编辑器">

#### 8.4 视频在线预览

文件类型为视频时，点击即可打开预览窗口，展示播放列表，支持快进、后退、暂停、倍速播放、全屏播放、下载视频和折叠播放列表。

视频播放器使用了 [vue-video-player](https://github.com/surmon-china/vue-video-player) ，具体配置项请查看该项目的官方文档，外层播放列表和操作栏为自行封装的。

<img src="https://pan.qiwenshare.com/docs/img/guide/function/video.png" alt="视频在线预览">

#### 8.5 音频在线播放

MP3 格式的文件支持在线播放。

<img src="https://pan.qiwenshare.com/docs/img/guide/function/audio.png" alt="音频在线播放">

### 9. 移动端支持

除过在线编辑之外，其他的功能均支持在移动端操作 [指南-功能展示-移动端支持](https://pan.qiwenshare.com/docs/guide/function.html#%E7%A7%BB%E5%8A%A8%E7%AB%AF%E6%94%AF%E6%8C%81)

## 联系我们

如您有问题，请加入 QQ 群咨询

**QQ 交流群**、**微信公众号 **或 **Gitee** 请扫描下面二维码

<img src="https://pan.qiwenshare.com/docs/img/guide/contact/contactUs.png" alt="交流群">

项目的发展离不开你的支持，如果觉得这个项目帮助到了你，请点击评论区上方的捐赠，请作者喝杯咖啡吧！

<img src="https://pan.qiwenshare.com/docs/img/guide/contact/agree.png" alt="捐赠">

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
