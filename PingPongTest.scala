import akka.actor._
 
case object PingMessage
case object PongMessage
case object StartMessage
case object StopMessage
 
class Ping(pong: ActorRef) extends Actor {
  var count = 0
  def incrementAndPrint { count += 1; println("ping") }
  def receive = {
    case StartMessage =>
        incrementAndPrint
        pong ! PingMessage
    case PongMessage => 
        Thread.sleep(2000)
        incrementAndPrint
        if (count > 10) {
          sender ! StopMessage
          println("ping stopped")
          context.stop(self)
        } else {
          sender ! PingMessage
        }
  }
}
 
class Pong extends Actor {
  def receive = {
    case PingMessage =>
        Thread.sleep(500)
        println("  pong")
        sender ! PongMessage
    case StopMessage =>
        println("pong stopped")
        context.stop(self)
  }
}
 
object Main extends App {
  val system = ActorSystem("PingPongSystem")
  val pong = system.actorOf(Props[Pong], name = "pong")
  val ping = system.actorOf(Props(new Ping(pong)), name = "ping")
  // start them going
  ping ! StartMessage
}
