package org.devops

// 带颜色的格式化输出
def PrintMsg(value,color){
    colors = [
        'red'   : "\033[40;31m >>>>>>>>>>>${value}<<<<<<<<<<< \033[0m",
        'blue'  : "\033[47;34m ${value} \033[0m",
        'green' : "\033[40;32m >>>>>>>>>>>${value}<<<<<<<<<<< \033[0m"
    ]
    ansiColor('xterm') {
        println(colors[color])
    }
}