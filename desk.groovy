#!groovy

@Library('jenkinslib') _

def tools = new org.devops.tools()
def email = new org.devops.notify_email()
def wechat = new org.devops.notify_wechat()

pipeline {

  options {
    timestamps() // 显示日志时间戳 => 使用前先安装插件
    skipDefaultCheckout() // 隐式删checkout scm语句
    timeout(time: 20, unit: 'MINUTES') // 流水线超时设置:20min
  }

  agent none

  stages {
    stage('Manual Confirm CI/CD') { // 手动确认是否执行本次流水线 => 针对多分支扫描时自动执行某些分支的CI/CD
      steps {
        timeout(time:1,unit:"MINUTES"){}
      }
    }
  }

  // 构建后操作
  post {
    always { script {
      node("HOST"){
        tools.PrintMsg("发送邮件和企业微信通知","green")
        email.Email("构建成功")
        wechat.WorkWechatNotify("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=aac51e56-6607-453d-bcf6-2c53f7c24de9")
      }

    }}
  }
}
