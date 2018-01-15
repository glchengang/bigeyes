#!/bin/sh

# export BIGEYES_HOME=/Users/chen/workspace/bigeyes/target
# 设置 start.sh 所以目录为本工具的HOME目录
export BIGEYES_HOME=$(cd `dirname $0`; pwd)
echo $BIGEYES_HOME


# 启动天气信息抓取器
# java -jar $BIGEYES_HOME/bigeyes.jar weather
# 启动新浪上市公司信息抓取器 (步骤抓取IP会被封锁)
# java -jar $BIGEYES_HOME/bigeyes.jar sina_stock_info
# 同时启动两个网页抓取器
java -jar $BIGEYES_HOME/bigeyes.jar weather sina_stock_info
