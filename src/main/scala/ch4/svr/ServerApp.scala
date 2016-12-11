package ch4.svr

import java.io.File

import akka.actor.{Actor, ActorSystem, ActorRef, Props}
import com.typesafe.config.ConfigFactory

import ch4.actr.DbServerActor

/**
  * Created by chengyongqiao on 10/12/2016.
  */
object ServerApp extends App {

  println("server app")

  val configFilePath = getClass().getClassLoader().getResource("ch4/db_server.conf").getFile()
  val config = ConfigFactory.parseFile(new File(configFilePath))
  val actorSystem = ActorSystem.create("dbserver", config)

  val dbServer = actorSystem.actorOf(Props[DbServerActor],"dbserver-actor")
}
