#!groovy

@Library('jenkinslib@main') _

def tools = new org.devops.tools()
def email = new org.devops.notify_email()
def wechat = new org.devops.notify_wechat()

def deploy = {
    sh '''
        echo "使用你的部署脚本"
    '''
}

def YAPI_TEST_URL = [
    'svc-api': 'http://www.yapi.com/api/open/run_auto_test?id=317&token=toekn&env_193=test&mode=html&email=false&download=false',
]

pipeline {

  options {
    timestamps() // 显示日志时间戳 => 使用前先安装插件
    skipDefaultCheckout() // 隐式删checkout scm语句
    timeout(time: 20, unit: 'MINUTES') // 流水线超时设置:20min
  }

  agent { label 'HOST' }

  parameters {
    choice (
          name: 'SVC',
          choices: [
              '', 'svc-api'
          ],
          description: '请选择需要发布的服务'
    )
    choice (
         name: 'INTEGRATION_TEST',
         choices: [
             'no','yes'
         ],
         description: '是否跑自动化集成测试?'
     )
    string (name: 'DOCKER_TAG', defaultValue: 'latest', description: 'docker镜像tag，默认latest')
    string (name: 'REPLICAS', defaultValue: '1', description: '发布的副本数量，默认1')
    string (name: 'MAX_REPLICAS', defaultValue: '5', description: '发布最大副本数量，默认5')
    string (name: 'RESOURCE_MIN', defaultValue: '100', description: 'CPU/内存最小资源，默认100m/100Mi')
    string (name: 'RESOURCE_MAX', defaultValue: '1000', description: 'CPU/内存资源上限，默认1000m/1000Mi')
    string (name: 'SCALE_THRESHOLD', defaultValue: '500', description: 'CPU/内存资源扩容条件上限，默认500m/500Mi')
  }

  environment { // 全局变量定义
    DOCKER_TAG = "${params.DOCKER_TAG}"
    BRANCH_ENV = "${params.BRANCH_ENV}"
    REPLICAS = "${params.REPLICAS}"
    SVC = "${params.SVC}"
    RESOURCE_MIN = tools.getResourceMin(params.BRANCH_ENV, params.RESOURCE_MIN) // CPU&&内存最小资源
    RESOURCE_MAX = tools.getResourceMax(params.BRANCH_ENV, params.RESOURCE_MAX) // CPU&&内存最大资源
    MAX_REPLICAS = tools.getMaxReplicas(params.BRANCH_ENV, params.MAX_REPLICAS) // 最大副本数量
    SCALE_THRESHOLD = "${params.SCALE_THRESHOLD}" // 扩容条件
  }
  stages {
    stage('Check on Controller') {
        stages {
            stage('Cleaning workspace') {
              steps { sh 'ls -l && sudo rm -rf ./*' }
            }

            stage('SCE Checkout') {
                steps { checkout scm }
            }

            stage('Stash artifacts') {
              steps {
                stash name: 'source', includes: '**', excludes: '**/.git,**/.git/**'
              }
            }
        }
    }
    stage('Tag Prepare && Integration Testing') {
      steps {
        script {
            DOCKER_TAG = tools.GetDockerTag(env.DOCKER_TAG)

            if(params.INTEGRATION_TEST == "yes") {
                def yapiTestUrl = YAPI_TEST_URL[env.SVC]
                if (yapiTestUrl == null) {
                    tools.PrintMsg("无需集成测试","green")
                } else {
                    def ok = tools.RunIntegrationTest(env.SVC, yapiTestUrl)
                    if (!ok) {
                        tools.PrintMsg(env.SVC + "集成测试失败!!!","red")
                        sh 'exit 1'
                    } else {
                        tools.PrintMsg(env.SVC + "集成测试通过.","green")
                    }
                }
            }
        }
      }
    }

    stage ('Deploy on Dev') {
      when { branch 'dev' }
      environment {
        DOCKER_TAG = "${DOCKER_TAG}"
        BRANCH_ENV = 'dev'
        NS = 'dev'
      }
      steps {
        script {deploy ()}
      }
    }

    stage ('Deploy on TEST') {
      when { branch 'test' }
      environment {
        DOCKER_TAG = "${DOCKER_TAG}"
        BRANCH_ENV = 'test'
        NS = 'test'
      }
      steps {
          script {deploy ()}
      }
    }

    stage ('Deploy on Prod') {
      when { branch 'main' }
      environment {
        DOCKER_TAG = "${DOCKER_TAG}"
        BRANCH_ENV = 'pro'
        NS = 'pro'
      }
      agent { label 'JUMPER_SERVER' }

      stages {
        stage('Cleaning workspace') {
            steps {
                sh 'ls -l && sudo rm -rf ./*'
            }
        }
        stage('Unstash artifacts') {
          steps {
            unstash 'source'
          }
        }
        stage('Starting containers') {
          steps {
            script {deploy ()}
          }
        }
      }
    }
  }

  // 构建后操作
  post {
    always { script { tools.PrintMsg("构建完成","green") }}
    success { script { currentBuild.description = "构建成功！" }}
    failure { script {
        currentBuild.description = "构建失败！"
        tools.PrintMsg("构建失败,发送邮件和企业微信推送","red")
        email.EmailNotify("本次构建失败")
        wechat.WorkWechatNotifyWithMsg("微信机器人Key","构建失败")
    }}
    aborted { script { currentBuild.description = "取消本次构建！" }}
  }
}
