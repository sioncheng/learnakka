package ch8

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import ch8.messages.{JoinChatRoom, PostToChatroom}
import org.scalatest.{FunSpecLike, Matchers}

import scala.concurrent.duration._;

/**
  * Created by chengyongqiao on 09/01/2017.
  */
class ChatroomSpec() extends TestKit(ActorSystem("chatroomSpec"))
    with ImplicitSender
    with Matchers
    with FunSpecLike {

    describe("given a chatroom has chat history") {
        val props: Props = Props.create(classOf[Chatroom])
        val chatroomRef: TestActorRef[Chatroom] = TestActorRef.create(system, props)
        val chatroom: Chatroom = chatroomRef.underlyingActor

        val msg: PostToChatroom = new PostToChatroom("test", "user");
        chatroom.chatHistory = chatroom.chatHistory :+ msg

        describe("when a user join this room") {
            val userRef: UserRef = UserRef(system.deadLetters, "userA")
            val joinRequest: JoinChatRoom = new JoinChatRoom(userRef)

            chatroomRef ! joinRequest

            it("the user should receive the history") {
                expectMsg(1 second, List(msg))
            }
        }
    }

    describe("given a chatroom has joined users") {
        val props: Props = Props.create(classOf[Chatroom])
        val chatroomRef: TestActorRef[Chatroom] = TestActorRef.create(system, props)
        val chatroom: Chatroom = chatroomRef.underlyingActor

        val probe: TestProbe = new TestProbe(system)
        val userRef: UserRef = UserRef(probe.ref, "probe-user")
        chatroom.joinChatroom(JoinChatRoom(userRef))

        describe("when someone posts to chatroom") {
            val msg: PostToChatroom = new PostToChatroom("test", "user");
            chatroomRef.tell(msg, probe.ref)
            it("(joined user) should get an update") {
                probe.expectMsg(msg)
            }
        }
    }
}
