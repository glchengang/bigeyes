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
# 启动天气信息抓取器
# java cn.yowob.bigeyes.Starter weather
# 启动新浪上市公司信息抓取器 (步骤抓取IP会被封锁)
# java cn.yowob.bigeyes.Starter sina_stock_info
# 同时启动两个网页抓取器
java cn.yowob.bigeyes.Starter weather sina_stock_info

