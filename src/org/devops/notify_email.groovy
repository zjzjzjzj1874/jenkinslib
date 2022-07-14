package org.devops

// 定义邮件内容
def EmailNotify(status,emailUser){
    mail body: """
            <!DOCTYPE html>
            <html>
            <head>
            <meta charset="UTF-8">
            </head>
            <body leftmargin="8" marginwidth="0" topmargin="8" marginheight="4" offset="0">
                <table width="95%" cellpadding="0" cellspacing="0" style="font-size: 11pt; font-family: Tahoma, Arial, Helvetica, sans-serif">
                    <tr>
                        <td><br />
                            <b><font color="#0B610B">构建信息</font></b>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <ul>
                                <li>项目名称：${JOB_NAME}</li>
                                <li>构建编号：${BUILD_ID}</li>
                                <li>构建状态: ${status} </li>
                                <li>项目地址：<a href="${BUILD_URL}">${BUILD_URL}</a></li>
                                <li>构建日志：<a href="${BUILD_URL}console">${BUILD_URL}console</a></li>
                            </ul>
                        </td>
                    </tr>
                    <tr>
                </table>
            </body>
            </html>  """,
            subject: "Jenkins-${JOB_NAME}项目构建信息 ",
            to: "$emailUser"
}

// 定义邮件内容
def Email(status){
    mail to: "$BUILD_USER_EMAIL",
        subject: "流水线${JOB_NAME}构建失败",
        body: "${JOB_NAME}构建失败,请点击${BUILD_URL}查看并修复"
}