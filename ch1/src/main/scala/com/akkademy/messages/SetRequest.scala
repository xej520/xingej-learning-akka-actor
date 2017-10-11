package com.akkademy.messages

//定义一个消息体
//此消息体的属性，就是不可变；经过网络传输后，是不允许修改的

case class SetRequest(key: String, value: String)
