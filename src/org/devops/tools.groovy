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

// 获取镜像Tag
def GetDockerTag(tag) {
    echo "客户传入Tag:${tag}"
    if (tag == '' || tag == 'latest') {
        echo "需要替换默认的latest Tag"
        tag = sh(script: "git log -n 1 --pretty=format:%H", returnStdout: true)
    }
    echo "替换后Tag:${tag}"
    return tag
}

// 获取最小资源 => 测试开发缩小10倍
def getResourceMin(branch, RESOURCE) {
    if (branch == 'test' || branch == 'dev') {
        def newRes = RESOURCE.toDouble()/10
        echo "getResourceMin: ${newRes}"
        return newRes
    } else {
        echo "getResourceMin: ${RESOURCE}"
        return RESOURCE
    }
}

// 获取最大资源 => 测试开发缩小5倍
def getResourceMax(branch, RESOURCE) {
    if (branch == 'test' || branch == 'dev') {
        def newRes = RESOURCE.toDouble()/5
        echo "getResourceMax: ${newRes}"
        return newRes
    } else {
        echo "getResourceMax: ${RESOURCE}"
        return RESOURCE
    }
}

// 获取副本数量 => 测试开发默认1个
def getMaxReplicas(branch, replicas) {
    if (branch == 'test' || branch == 'dev') {
        echo "getMaxReplicas: 1"
        return 1
    } else {
        echo "getMaxReplicas: ${replicas}"
        return replicas
    }
}

// 运行集成测试
def RunIntegrationTest(srv, testUrl) {
    // 报告名称
    def reportName = "./${srv}-${BUILD_ID}.html"
    // 进行自动化测试并且下载测试报告
    sh "curl -o ${reportName} '${testUrl}'"
    // 全部通过关键字出现的次数
    def passCount = sh(script: "grep -c '全部验证通过' ${reportName}", returnStdout:true)
    if (passCount.toInteger() > 0) {
        return true
    } else {
        return false
    }
}