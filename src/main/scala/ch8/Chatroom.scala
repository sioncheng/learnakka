package ch8;

import akka.actor.Actor
import ch8.messages.{JoinChatRoom, PostToChatroom}

import scala.collection.immutable.Seq

/**
 * Created by chengyongqiao on 09/01/2017.
 */
class Chatroom extends Actor {

    var joinedUsers: Seq[UserRef] = Seq.empty
    var chatHistory: Seq[PostToChatroom] = Seq.empty

    override def receive = {
        case x: JoinChatRoom => joinChatroom(x)
        case x: PostToChatroom => postToChatroom(x)
        case _ => println("received unknown message")
    }

    def joinChatroom(joinChatRoom: JoinChatRoom): Unit = {
        joinedUsers = joinedUsers :+ joinChatRoom.userRef
        sender() ! chatHistory.toList
    }

    def postToChatroom(postToChatroom: PostToChatroom): Unit = {
        chatHistory = chatHistory :+ postToChatroom
        joinedUsers.foreach(joinedUser => joinedUser.actor ! postToChatroom)
    }
}
