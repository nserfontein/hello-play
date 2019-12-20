package model

import java.util.UUID

import scalikejdbc.WrappedResultSet

case class User(userId: UUID, userCode: String, password: String)

object User {
  def fromRS(rs: WrappedResultSet): User = {
    User(
      userId = UUID.fromString(rs.string("user_id")),
      userCode = rs.string("user_code"),
      password = rs.string("password")
    )
  }
}
