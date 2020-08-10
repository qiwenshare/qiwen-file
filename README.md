
<p align="center">
	<a href="http://fileos.qiwenshare.com/"><img width="30%" src="https://images.gitee.com/uploads/images/2020/0810/131432_e00bbf82_947714.png" ></a>
</p>
<p align="center">
	<strong>基于springboot + vue 框架开发的Web文件系统，旨在为用户提供一个简单、方便的文件存储方案，能够以完善的目录结构体系，对文件进行管理 。</strong>
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
	<a href="http://fileos.qiwenshare.com/" target="_blank">在线演示环境</a> 
	<a href="https://www.qiwenshare.com/essay/detail/24"  target="_blank">软件介绍</a> 
	<a href="https://www.qiwenshare.com/essay/detail/169"  target="_blank">安装指导</a>
</p>

-------------------------------------------------------------------------------

## 开源说明
系统100%开源
本软件遵循MIT开源协议

 **您可以在其基础上继续进行开发来完善其功能，成为本项目的贡献者之一** 

 **您也可以以该项目作为脚手架，进行其他项目的开发** 

## 功能
#### 用户操作
1. 用户的登录和注册
#### 文件操作
1. 用户可对目录结构进行增加，删除，修改 
2. 提供文件的上传，下载， 
3. 支持对zip文件的在线解压缩 
4. 支持文件和目录的移动和复制 
5. 多文件格式分类查看 
6. ...

## 软件架构
该项目采用前后端分离的方式进行开发和部署,主要用到以下关键技术

**前台**：elementui，vue,  swipper， nodejs， webpack

**后台框架**：springboot mybatis jpa

**数据结构**：递归算法，树的遍历和插入...


## 安装教程

[手把手教你部署奇文网盘](https://www.qiwenshare.com/essay/detail/169)

## 目录说明

```

-file-common 公共模块
-file-web 文件代码
    |-src
        |-main Java代码
        |-resources 静态资源
            |-static 前台代码
```




## 使用说明
1、本项目为后端代码，master 分支现在功能可用（lombok分支现为开发阶段，尚不可用）

2、如果你需要前台代码进行学习或者完善，可以访问该地址进行拉取
前台代码源码：[源码](https://gitee.com/qiwen-cloud/qiwen-file-web)

3、文件默认保存位置：如果你是用IDEA启动的，默认保存位置在target下的upload；如果是打包成jar部署在服务器，默认保存位置在根目录的release/static/upload 下


## 部分功能演示截图
 **主页**  :yellow_heart: 
![新版主页](https://images.gitee.com/uploads/images/2020/0409/182847_8f60ac83_1837873.png "屏幕截图.png")

![新功能](https://images.gitee.com/uploads/images/2020/0409/183222_fa2282c6_1837873.png "屏幕截图.png")

![左侧菜单栏收缩](https://images.gitee.com/uploads/images/2020/0409/183644_a6902a69_1837873.png "屏幕截图.png")

 **文件分类查看** :heart: 

支持三种显示格式进行查看（列表，网格，时间线）

列表
![输入图片说明](https://images.gitee.com/uploads/images/2020/0415/001030_f8caf4fb_947714.png "屏幕截图.png")
网格
![输入图片说明](https://images.gitee.com/uploads/images/2020/0415/001114_fc708749_947714.png "屏幕截图.png")
时间线
![输入图片说明](https://images.gitee.com/uploads/images/2020/0415/000833_ee93793e_947714.png "屏幕截图.png")

 **创建文件夹**  :blue_heart: 
![输入图片说明](https://images.gitee.com/uploads/images/2019/1022/205531_b3bf3380_947714.png "屏幕截图.png")
 **操作列展示**  :purple_heart: 
![操作列扩展](https://images.gitee.com/uploads/images/2020/0409/183336_ab936775_1837873.png "屏幕截图.png")

![操作列合并](https://images.gitee.com/uploads/images/2020/0409/183412_6a3ed5e0_1837873.png "屏幕截图.png")
 **文件的复制和移动**  :heart: 
![输入图片说明](https://images.gitee.com/uploads/images/2019/1022/210106_d4b619f3_947714.png "屏幕截图.png")
 **文件在线解压缩**  :green_heart: 
![输入图片说明](https://images.gitee.com/uploads/images/2019/1022/210214_c00f5600_947714.png "屏幕截图.png")



## 联系我
各种问题可扫描加入QQ群进行咨询

**QQ交流群**请扫描下面二维码

<img width="30%" src="https://images.gitee.com/uploads/images/2020/0406/164832_5121dc5e_947714.png"/>

**微信公众号**请扫描下面二维码

<img width="30%" src="https://images.gitee.com/uploads/images/2020/0406/164833_d99e92ee_947714.png"/>



## 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


## 码云特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5.  码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
