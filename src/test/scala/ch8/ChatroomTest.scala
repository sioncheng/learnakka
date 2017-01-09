package ch8

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestActorRef
import ch8.messages.JoinChatRoom
import org.scalatest.FunSpec
import org.scalatest.Matchers

/**
  * Created by chengyongqiao on 09/01/2017.
  */
class ChatroomTest extends FunSpec with Matchers {

    val system = ActorSystem()

    describe("give a chatroom has not users") {
        val props: Props = Props.create(classOf[Chatroom])
        val ref: TestActorRef[Chatroom] = TestActorRef.create(system, props, "testA")
        val chatroom: Chatroom = ref.underlyingActor
        chatroom.joinedUsers.size should equal(0)

        describe("when it receives a request from a user to join the room") {
            val userRef: UserRef = new UserRef(system.deadLetters, "userA")
            val joinRoom: JoinChatRoom = new JoinChatRoom(userRef)
            ref ! joinRoom
            it("should add the UserRef to the list of joined users") {
                chatroom.joinedUsers.head should equal(userRef)
            }
        }
    }
}
