package models

import play.api.libs.json.Json


case class ElemicaUser(first: String , last: String , email:String, password:String)

object ElemicaUser {
  implicit val userFormat = Json.format[ElemicaUser]
}