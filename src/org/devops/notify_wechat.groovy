package org.devops

// 执行企业微信通知
def WorkWechatNotify(reqUrl){
    response = HttpPost(reqUrl)
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
def HttpPost(reqUrl){
    def data = '{"msgtype": "text","text": {"content": "' + "${JOB_NAME}" + '构建完成，点击' + "${BUILD_URL}" + '查看详情"}}'
    result = httpRequest httpMode: "POST",
            contentType: "APPLICATION_JSON",
            consoleLogResponseBody: true,
            ignoreSslErrors: true,
            requestBody: data,
            url: "${reqUrl}"
    return result
}
