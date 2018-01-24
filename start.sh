#!/bin/sh

# 脚本主要做了三件事：
#    1. 设置 BIGEYES_HOME 变量
#    2. 设置 CLASSPATH 变量
#    3. java 命令行启动程序

# -----------------------------------------------------------------------------


# export BIGEYES_HOME=/Users/chen/workspace/bigeyes/target
# 设置 start.sh 所在目录为本工具的HOME目录
# 说明：`dirname $0` 得到当前的相对路径，也可以用$(dirname $0)
#      cd `dirname $0` 进入了此脚本所在目录, 而 pwd 列出全路径
export BIGEYES_HOME=$(cd `dirname $0`/..; pwd)
echo $BIGEYES_HOME


# 将lib目录里的所有jar设置进CLASSPATH
CLASSPATH=$BIGEYES_HOME/conf/
for jarFilename in $(ls $BIGEYES_HOME/lib/*.jar)
do
    #echo $jarFilename
    CLASSPATH=$CLASSPATH:$jarFilename
done
export CLASSPATH
#echo CLASSPATH = $CLASSPATH


# cn.yowob.bigeyes.Starter类在bigeyes-1.0.jar里
# java cn.yowob.bigeyes.Starter weather
# java cn.yowob.bigeyes.Starter weather sina_stock_info
#
# 现在支持以下信息的采集:
#      sinablog - 新浪博客备份
#      blogjava - www.blogjava.net BlogJava技术博客备份
#      weather - 天气信息
#      sina_stock_info - 新浪网站的上市公司信息 (频繁抓取,IP会被新浪封掉一个小时)

java cn.yowob.bigeyes.Starter sinablog blogjava