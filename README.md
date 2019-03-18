# GeneratePatchFileCommander

### 为啥造这个

项目部署维护时，有时会只更新某几个文件，然项目目录盘根错节、开发源码路径与编译路径不一致，导致手工复制粘贴既麻烦又容易出错，SVN客户端上有没找到有效的办法解决，固造此程序，此程序可根据SVN diff生成的patch一键生成带路径补丁文件，同时会自动替换源码文件为编译文件，导出的文件可直接在生产环境上覆盖粘贴。

*目前功能只针对Java-web项目*

*目前只在Windows环境下测试过*

### 环境要求

本程序分为Python版和Java版，两者实现功能相同

python版，要有python3的运行环境

java版，需要jdk1.8或以上


### Start

#### 配置文件config.json/config.properties

python版的配置文件为config.json；Java版的配置文件为config.properties

两者只是文件格式上的不同

`projectPath`——字符串，项目所在的目录

`sourcePaths`——数组[字符串]，源码路径（config.properties版用英文逗号隔开）

`outputPath`——字符串，源码编译路径

`targetPath`——字符串，导出的文件存放的路径

`patchTxtFromFile`——布尔值，SVN diff生成的patch文本是否来源于文件

`patchTxtPath`——字符串，SVN diff生成的patch.txt文件的路径，`patchTxtFromFile`为`true`时有效

`log`——布尔值，是否将日志保存成txt文件

`logPath`——字符串，日志文件保存路径，`log`为`true`时有效

#### 运行

python版：

首次使用运行`pip install -r requirements.txt`安装依赖

直接运行main.py，或在当前目录的命令行里执行`python main.py`

java版：

首次使用配置好Java环境及config.properties的路径

直接运行App.java下的`main`方法