package ch4.actr

import ch4.mdl.{GetRequest, KeyNotFoundException, SetRequest}
import akka.actor.{Actor, Status}

import scala.collection.mutable.HashMap

/**
  * Created by chengyongqiao on 10/12/2016.
  */
class DbServerActor extends Actor {

  override def receive = {
    case SetRequest(key, value) => {
      if (map.contains(key)) {
        map.update(key, value)
      } else {
        map.put(key, value)
      }
      sender() ! Status.Success
    }
    case GetRequest(key) => {
      val response: Option[String] = map.get(key)
      response match {
        case Some(x) => sender() ! x
        case None => sender() ! Status.Failure(KeyNotFoundException(key))
      }
    }

  }

  private val map = new HashMap[String, String]()
}
