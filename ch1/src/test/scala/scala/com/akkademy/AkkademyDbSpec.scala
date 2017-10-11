package com.akkademy

import akka.actor.ActorSystem

import akka.testkit.TestActorRef
import akka.util.Timeout
import com.akkademy.messages.SetRequest
import org.scalatest.{FunSpecLike, Matchers}

class AkkademyDbSpec extends FunSpecLike with Matchers{
    implicit val system = ActorSystem()
    implicit val timeout = Timeout(5)
    describe("akkademyDb") {
        describe("given SetRequest") {
            it("should place key/value into map") {
                val actorRef = TestActorRef(new AkkademyDb)
                actorRef ! SetRequest("id", "2")
                val akkademyDb = actorRef.underlyingActor
//                println("---->:\t" + akkademyDb.map.get("key"))
                akkademyDb.map.get("id") should equal(Some("2"))
            }
        }
    }
}
