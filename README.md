# CopyFileCommander

#### 为啥造这个

项目部署维护时，有时会只更新某几个文件，然项目目录层级盘根错节、开发源码路径与编译路径不一致，导致手工复制粘贴既麻烦又容易出错，SVN客户端上有没找到有效的办法解决，固造此程序，此程序可根据配置文件一键带路径复制，同时会自动替换源码文件为编译文件，导出的文件可直接在生产环境上覆盖粘贴。

*目前只写了Java-web项目的版本*

#### 前提

首先，要有python3的运行环境

 #### 配置文件config.json

`homePath`——字符串，项目所在的家目录

`workPaths`——数组[字符串]，你想要导出的文件们

`sourcePaths`——数组[字符串]，源码路径

`outputPath`——字符串，源码编译路径

`targetPath`——字符串，导出的文件存放的路径

`log`——布尔值，是否将日志保存成txt文件，是`true`，否`false`

`logPath`——字符串，日志文件保存路径，`log`为`true`时有效

#### 运行
直接运行main.py，或在当前目录的命令行里执行`python main.py`