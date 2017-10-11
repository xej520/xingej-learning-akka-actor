package com.akkademy.akkademy_db_scala

import akka.actor.{Actor, ActorSystem, Props, Status}
import akka.event.Logging
import com.akkademy.messages.{GetRequest, KeyNotFoundException, SetRequest}

import scala.collection.mutable

class AkkademyDb extends Actor{
    val map = new mutable.HashMap[String, Object]
    val log = Logging(context.system, this)

    override def receive: Receive = {
        case SetRequest(key, value) => {
            log.info("received SetRequeset -key: {}, value: {}", key, value)
            // 将传递过来的key,value 存储到map容器里
            map.put(key, value)
            //存储完成后，向发送者 返回消息Success
            sender() ! Status.Success
        }
        // 接收消息
        case GetRequest(key) => {
            log.info("received GetRequest -key: {}", key)
            val response: Option[Object] = map.get(key)
            response match {
                case Some(x) => sender() ! x //向发送者 返回 查询到的信息
                case None =>    sender() ! Status.Failure(new KeyNotFoundException(key))
            }
        }

        case o => Status.Failure(new ClassNotFoundException)
    }
}

object Main extends App{
    val system = ActorSystem("akkademy")
    val helloActor = system.actorOf(Props[AkkademyDb], name = "akkademy-db")
}
