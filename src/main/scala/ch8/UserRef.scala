package ch8

import akka.actor.ActorRef

/**
  * Created by chengyongqiao on 09/01/2017.
  */
case class UserRef(actor: ActorRef , username: String)
