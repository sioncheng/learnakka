package ch4.mdl

case class SetRequest(key: String, value: String)

case class GetRequest(key: String)

case class KeyNotFoundException(key: String) extends Exception