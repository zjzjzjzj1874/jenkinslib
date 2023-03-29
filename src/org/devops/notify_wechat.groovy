package org.devops

// 执行企业微信通知
def WorkWechatNotify(reqUrl){
    response = HttpPost(reqUrl,"构建失败")
    return response
}

// 执行企业微信通知
def WorkWechatNotifyWithMsg(reqUrl,msg){
    response = HttpPost(reqUrl,msg)
    return response
}

// 封装HTTP请求
def HttpReq(reqType,reqUrl,reqBody){
   result = httpRequest httpMode: reqType,
            contentType: "APPLICATION_JSON",
            consoleLogResponseBody: true,
            ignoreSslErrors: true,
            requestBody: reqBody,
            url: "${reqUrl}"
    return result
}

// 封装HTTP Post请求
def HttpPost(reqUrl,msg){
    def post = new URL(reqUrl).openConnection();
    def data = '{"msgtype": "text","text": {"content": "' + "${JOB_NAME}" + "${msg}" + '，请点击' + "${BUILD_URL}" + '查看详情"}}'

    post.setRequestMethod("POST")
    post.setDoOutput(true)
    post.setRequestProperty("Content-Type", "application/json")
    post.getOutputStream().write(data.getBytes("UTF-8"));
    post.getResponseCode();
}
