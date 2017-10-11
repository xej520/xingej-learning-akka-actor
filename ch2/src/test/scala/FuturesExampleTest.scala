import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.akkademy.futures_examples.ScalaPongActor
import org.scalatest.{FunSpecLike, Matchers}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}

class FuturesExampleTest extends FunSpecLike with Matchers {
    implicit val system = ActorSystem()
    implicit val timeout = Timeout(5 seconds)
    val pongActor = system.actorOf(Props(classOf[ScalaPongActor]))

    describe("Pong actor") {
        it("should respond with Pong") {
            val future = pongActor ? "Ping"
            val result = Await.result(future.mapTo[String], 1 second)
            println("----result----:\t" + result)
            assert(result == "Pong")
        }
        it("should fail on unknown message") {
            val future = pongActor ? "unknown"
            intercept[Exception] {
                Await.result(future.mapTo[String], 1 second)
            }
        }
    }

    // 对返回结果 执行代码
    describe("FutureExamples") {
        import scala.concurrent.ExecutionContext.Implicits.global
        it("should print to console") {
            askPong("Ping").onSuccess({
                case x: String => println("replied with: " + x)
             })
            Thread.sleep(100)
        }

        // 对返回结果，进行转换
        it("should transform") {
            val f: Future[Char] = askPong("Ping").map(x => x.charAt(0))
            val c = Await.result(f, 1 second)
            println("----result----:\t" + c)
            c should equal('P')
        }
    }

    //对返回结果进行 异步转换
    import scala.concurrent.ExecutionContext.Implicits.global
    it("should transform async") {
        val f: Future[String] = askPong("Ping").flatMap(x => {
            assert(x == "Pong")
            askPong("Ping")
        })
        val c = Await.result(f, 5 second)
        c should equal("Pong")
        println(c should equal("Pong"))
    }

    //在失败的情况下，执行代码
    it("should effect on failure") {
        askPong("causeError").onFailure{
            case e:Exception => println("Got exception")
        }
    }

    it("should effect on failure (with assertion)") {
        val res = Promise()
        askPong("causeError").onFailure{
            case e:Exception => res.failure(new Exception("failed!"))
        }
        intercept[Exception]{
            Await.result(res.future, 1 second)
        }
    }

    //从失败中恢复
    it("should recover on failure") {
        val f = askPong("causeError").recover({
            case e:Exception => "default"
        })
        val result = Await.result(f, 1 second)
        println("--->:\t" + (result should equal("default")))

    }
    //异步的从 失败中恢复
    it("should recover on failure async") {
        val f = askPong("causeError").recoverWith({
            case e : Exception => askPong("Ping")
        })
        val result = Await.result(f, 1 second)
        println("--->:\t" + (result should equal("Pong")))
    }

    //链式操作
    it("should chain together multiple operations") {
        val f = askPong("Ping").flatMap(
            x =>{
             println("--1-->:\t" + x) //x 是Pong
             askPong("Ping" +x)
        }).recover({
            case _: Exception => "There was an error"
        })

        val result = Await.result(f, 1 second)
        println("---> :\t" + (result should equal("There was an error")))
    }

    //组合Future
    it("should be handled with for comprehension") {
        val f1 = Future{4}
        val f2 = Future{5}
        val futureAddition = for{
            rest1 <- f1
            rest2 <- f2
        } yield rest1 + rest2

        val additionResult = Await.result(futureAddition, 1 second)
        println("----additionResult-----:\t" + additionResult)
    }

    //处理future列表
    it("should handle a list of futures") {
        val listOfFutures: List[Future[String]] = List("Pong", "Pong","failure").map(x => askPong(x))
        val futureOfList: Future[List[String]] = Future.sequence(listOfFutures)
    }
    def askPong(message: String) : Future[String] = (pongActor ? message).mapTo[String]
}















