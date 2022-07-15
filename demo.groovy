// 封装HTTP Post请求
def HttpPost(reqUrl){
    def post = new URL(reqUrl).openConnection();
    def data = '{"msgtype": "text","text": {"content": "' + "${JOB_NAME}" + '构建完成，点击' + "${BUILD_URL}" + '查看详情"}}'

    post.setRequestMethod("POST")
    post.setDoOutput(true)
    post.setRequestProperty("Content-Type", "application/json")
    post.getOutputStream().write(data.getBytes("UTF-8"));
    post.getResponseCode();
}
// 执行企业微信通知
def WorkWechatNotify(reqUrl){
    response = HttpPost(reqUrl)
    return response
}
// 定义邮件内容
def Email(status){
    mail to: "$BUILD_USER_EMAIL",
        subject: "流水线${JOB_NAME}构建失败",
        body: "项目名：${JOB_NAME}构建失败,项目地址:${BUILD_URL},请点击${BUILD_URL}console查看并修复"
}
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
pipeline {

  options {
    timestamps() // 显示日志时间戳 => 使用前先安装插件
    skipDefaultCheckout() // 隐式删checkout scm语句
    timeout(time: 20, unit: 'MINUTES') // 流水线超时设置:20min
  }

  agent none

  stages {
    stage('timeout set') { // 手动确认是否执行本次流水线 => 针对多分支扫描时自动执行某些分支的CI/CD
      steps {
        timeout(time:1,unit:"MINUTES"){}
      }
    }
  }

  // 构建后操作
  post {
    always { script {
      node("HOST"){
        PrintMsg("发送邮件和企业微信通知","green")
        Email("构建成功")
        WorkWechatNotify("your work wechat secret")
      }

    }}
  }
}
