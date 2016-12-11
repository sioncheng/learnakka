package ch4.clnt

import java.io.File

import akka.actor.Status.{Failure, Status, Success}
import akka.actor._
import akka.util.Timeout
import ch4.mdl.{GetRequest, KeyNotFoundException, SetRequest}
import ch4.svr.ServerApp._
import com.typesafe.config.ConfigFactory
import akka.pattern.ask

import scala.concurrent.Await
import scala.concurrent.duration._
/**
  * Created by chengyongqiao on 11/12/2016.
  */

trait Cmd
object GetCmd extends Cmd
object SetCmd extends Cmd
object QuitCmd extends Cmd
case class GetSetCmd(cmd: Cmd, key: String, value: String)

object ServerClient extends App {

    val configFilePath = getClass().getClassLoader().getResource("ch4/db_client.conf").getFile()
    val config = ConfigFactory.parseFile(new File(configFilePath))

    implicit val actorSystem = ActorSystem.create("dbserver", config)

    implicit val timeout = Timeout(2 seconds)

    val remoteServerActor = actorSystem.actorSelection("akka.tcp://dbserver@localhost:5333/user/dbserver-actor")

    println("connected to server")

    usage()
    var quit = false
    while(!quit) {
        val line = scala.io.StdIn.readLine()
        val getSetCmd = parseLine(line)
        getSetCmd match {
            case None => {
                println("command error")
                usage()
            }
            case Some(GetSetCmd(QuitCmd, _, _)) => {
                println("bye");
                quit = true
            }
            case Some(GetSetCmd(SetCmd, key, value)) => {
                val future = remoteServerActor ? SetRequest(key, value)
                val result = Await.result(future, timeout.duration)
                println(result)
                println("continue...")
            }
            case Some(GetSetCmd(GetCmd, key, _)) => {
                import scala.concurrent.ExecutionContext.Implicits.global
                val future = (remoteServerActor ? GetRequest(key)).recover( {
                    case KeyNotFoundException(k) => s"cant got $k"
                    case _ => "what's wrong?"
                })
                val result = Await.result(future, timeout.duration)
                printResult(result)
                println("continue...")
            }
            case Some(_) => {
                println("??? continue...")
            }
        }
    }



    actorSystem.shutdown()

    def printResult(result: Any): Unit = result match {
        case Success => println("success")
        case x: String => println(x)
        case _ => println("ahh?")
    }

    def usage(): Unit = {
        println("usage:")
        println("set key value")
        println("get key")
    }

    def parseLine(line: String): Option[GetSetCmd] = {
        val words = line.split(" ")
        if (words.length >= 3) {
          if (words(0).trim().equalsIgnoreCase("set")) {
            Some(GetSetCmd(SetCmd, words(1).trim(), words(2).trim()))
          } else {
            None
          }
        } else if (words.length == 2) {
          if (words(0).trim().equalsIgnoreCase("get")) {
            Some(GetSetCmd(GetCmd, words(1), ""))
          } else {
            None
          }
        } else if(words.length == 1) {
          if (words(0).equalsIgnoreCase("quit")) {
            Some(GetSetCmd(QuitCmd, "", ""))
          } else {
              None
          }
        } else {
            None
        }
    }
}
