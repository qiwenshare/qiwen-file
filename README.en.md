<p align="center">
	<a href="http://fileos.qiwenshare.com/"><img width="30%" src="https://images.gitee.com/uploads/images/2021/0511/141109_0a709933_947714.png" ></a>
</p>
<p align="center">
	<strong>The distributed file system based on Spring Boot + VUE CLI@3 framework is designed to provide a simple and convenient file storage scheme for users and enterprises. It can manage files with a perfect directory structure system.</strong>
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
	<a href="http://pan.qiwenshare.com/" target="_blank">Online presentation environment</a> &nbsp;|
	<a href="https://www.qiwenshare.com/essay/detail/169"  target="_blank">Installation instructions</a>&nbsp;|
        <a href="https://www.qiwenshare.com/essay/detail/324"  target="_blank">Update log</a>
</p>

---

## Open source that

System 100% open source
The software follows the MIT open source protocol

**You can build on it to improve its functionality and become a contributor to this project**

**You can also use this project as a scaffold for other projects**

## Function is introduced

### The user action

1. User login and registration

### File operations

1. Users can add, delete and modify the directory structure
2. Provide drag and drop to upload and downloading of files, support screenshot paste directly upload pictures
3. Support online decompression of ZIP files and RAR files
4. Support files and directories to move and copy
5. Classified view of multiple file formats
6. Support AliCloud OSS object storage and FastDFS storage
7. Added sharding uploading, which supports local storage, Ali Cloud OSS and FastDFS
8. Support high speed second transmission function to improve uploading efficiency
9. The foreground of uploading files displays the progress, rate, percentage and other information of uploading files in real time
10. Support file renaming
11. Display file storage and total capacity in real time
12. Support ElasticSearch file search
13. Support to share files with others and view the list of files you have shared
14. Provide picture online preview, video online preview, audio online preview, PDF, JSON, TXT and other commonly used text file online preview, support Office online preview
15. ……

## The source address

| The project name       | The source address                                                                           |
| ---------------------- | -------------------------------------------------------------------------------------------- |
| The front project      | [https://gitee.com/qiwen-cloud/qiwen-file-web](https://gitee.com/qiwen-cloud/qiwen-file-web) |
| The background project | [https://gitee.com/qiwen-cloud/qiwen-file](https://gitee.com/qiwen-cloud/qiwen-file)         |

## Network topology

![网络拓扑图](https://images.gitee.com/uploads/images/2021/0324/225520_d55b109e_947714.png '屏幕截图.png')

## Software architecture

The project was developed and deployed in a front-end separation approach, using the following key technologies

**Front**：Element UI、Vue CLI@3、Node.js、Webpack

**Background **：Spring Boot、MyBatis、JPA、JWT

**Database** : MySQL

**Data Structure**：Recursive algorithms, tree traversal and insertion...

## Directions for use

1、This project is the back-end code

2、Download the front-end code, you can access the address to pull:[qiwen-file-web](https://gitee.com/qiwen-cloud/qiwen-file-web)

## Deployment instructions

Please move to Qiwenshare [手把手教你部署奇文网盘](https://www.qiwenshare.com/essay/detail/169)

## Screenshots of some functions

### 1. The cloud home page

#### 1.1 The page layout

- Left menu bar area: display file type, click to view files by category, the bottom shows the occupied storage space.
- Top file operation area: including operation button group for files, switch button group for file viewing mode, and set button for file display column.
- Right file display area: including breadcrumb navigation bar -- identifies the directory currently located; File display area -- the display format will change according to the file viewing mode; Bottom paging component.

![网盘主页](https://images.gitee.com/uploads/images/2021/0325/105935_225d3d46_1837873.png)

#### 1.2 Layout adjustment function

The left menu bar is collapsible, and the table operation column is collapsible. You can control the display and hiding of the columns in the current table.

![折叠功能](https://images.gitee.com/uploads/images/2021/0325/113631_cf57fc44_1837873.gif)

#### 1.3 Batch operation function

![批量操作](https://images.gitee.com/uploads/images/2021/0325/115913_ad3bbe67_1837873.gif)

### 2. Three viewing modes

File viewing supports three presentation modes (list, grid, and timeline)

#### 2.1 List Mode

![列表模式](https://images.gitee.com/uploads/images/2021/0325/120007_1d046ef5_1837873.png)

#### 2.2 Grid Mode

![网格模式](https://images.gitee.com/uploads/images/2021/0325/123609_e4868707_1837873.png)

#### 2.3 Timeline Mode

![时间线模式](https://images.gitee.com/uploads/images/2021/0325/123638_3316ce8f_1837873.png)

### 3. Create a folder

![创建文件夹](https://images.gitee.com/uploads/images/2021/0325/123704_145604f0_1837873.png)

### 4. File icon resize

Manual resizing of ICONS is supported in grid mode and timeline mode:

![图标大小调整](https://images.gitee.com/uploads/images/2021/0409/181840_bb0dcb17_1837873.gif)

### 5. Move files

![文件移动](https://images.gitee.com/uploads/images/2021/0325/123752_14364633_1837873.png)

### 6. Online file decompression

![文件在线解压缩](https://images.gitee.com/uploads/images/2021/0325/123823_aba673e8_1837873.png)

### 7. There are three ways to upload files

#### 7.1 File fragmentation upload

文件采用**分片上传**，集成了[simiple-uplader](https://github.com/simple-uploader/Uploader/blob/develop/README_zh-CN.md#uploader)的文件**秒传**、**断点续传**功能。
![文件上传](https://images.gitee.com/uploads/images/2021/0325/144103_08f4902b_1837873.png)

#### 7.2 Drag and drop uploads

![文件拖拽上传](https://images.gitee.com/uploads/images/2021/0416/143326_1353ea6a_1837873.gif)

#### 7.3 Paste and upload the screenshot

After taking the screenshot directly with any screenshot tool, use Ctrl + V to paste the image in the drag and drop area and click Upload to upload the image.

![截图粘贴上传](https://images.gitee.com/uploads/images/2021/0416/143216_c7be7797_1837873.png)

### 8. Document Recycle Bin

![文件回收站](https://images.gitee.com/uploads/images/2021/0325/123843_f8fa15bf_1837873.png)

### 9. Files are shared individually and in batches

1. Support single and batch file sharing to others:

   ![单个或批量分享文件](https://images.gitee.com/uploads/images/2021/0412/094958_842ead78_1837873.png)

2. You can select the expiration time and whether you want to extract the code:

   ![过期时间和是否需要提取码](https://images.gitee.com/uploads/images/2021/0412/095039_b7841dbc_1837873.png)

3. Provide quick copy link and extract code to others:

   ![生成分享链接](https://images.gitee.com/uploads/images/2021/0412/095055_a06df014_1837873.png)

   Paste sharing link and extract code effect:

   ```
   分享链接：https://pan.qiwenshare.com/share/27d9b438019e4f68bcec02f579d163b7
   提取码：356978
   复制链接到浏览器中并输入提取码即可查看文件
   ```

4. Others view the shared content, and support the function of saving to network disk:

![保存到我的网盘](https://images.gitee.com/uploads/images/2021/0412/095629_ce5b3336_1837873.png)

### 10. Support to view the list of files you have shared

Support quick copy of the sharing link and extraction code in the list, and mark the sharing time and expiration status:

![我的分享](https://images.gitee.com/uploads/images/2021/0412/095142_6d543701_1837873.png)

### 11. Video Preview Online

When the file type is video, click to open the preview window and display the playlist. It supports fast forward, backward, pause, double speed playback, full-screen playback, download video and fold playlist.

![视频在线预览](https://images.gitee.com/uploads/images/2021/0416/143120_c1ab2d82_1837873.png)

### 12. Audio online playback

![音频在线播放](https://images.gitee.com/uploads/images/2021/0416/142347_1a09dd57_1837873.png)

## Contact us

If you have any questions, please join the QQ group consultation

**QQ communication group ** and **WeChat public account** please scan the following QR code

<div style="dispaly: flex;">
    <img src="https://images.gitee.com/uploads/images/2021/0325/133721_7c174ea5_1837873.png"/>
    <img src="https://images.gitee.com/uploads/images/2021/0325/133740_557ca1d8_1837873.png"/>
</div>

## Thanks

The development of this project cannot be achieved without the support of other excellent open source projects. I would like to thank the following open source projects:

- [vue-simple-uploader](https://github.com/simple-uploader/vue-uploader/blob/master/README_zh-CN.md)：A [simple-uploader.js](https://github.com/simple-uploader/Uploader/blob/develop/README_zh-CN.md) based on the development of Vue.js for the sharding upload plug-in
- [vue-video-player](https://github.com/surmon-china/vue-video-player)：A video player component based on [video.js](https://docs.videojs.com/) development

## Participate in the contribution

1.  Fork the warehouse
2.  Create a new branch: Feat_xxx
3.  Submit code
4.  Create a new Pull Request

## Gitee Special Effects

1.  Use Readme_XXX.md to support different languages,for example: Readme_en.md,Readme_zh.md
2.  The official blog of Gitee: [blog.gitee.com](https://blog.gitee.com)
3.  You can be in [https://gitee.com/explore](https://gitee.com/explore) here to decode the cloud good open source project
4.  [GVP](https://gitee.com/gvp) The full name is the most valuable open source project of Gitee, and the excellent open source project comprehensively evaluated by Gitee
5.  The Gitee official user's manual: [https://gitee.com/help](https://gitee.com/help)
6.  The Gitee cover figure is a column used to show the elegant demeanor of Code Cloud members [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
