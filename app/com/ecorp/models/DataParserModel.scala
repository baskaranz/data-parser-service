package com.ecorp.models

import play.api.libs.json.Json

import java.time.LocalDateTime

case class UserEntry(eventTime: LocalDateTime, email: String, sessionId: String)

case class DataParseRequest(filePath: String, startDate: String, endDate: String, offset: Int, limit: Int)

object UserEntry {
  implicit lazy val userEntryFormat = Json.format[UserEntry]
}

object DataParseRequest {
  implicit lazy val dataParseRequest = Json.format[DataParseRequest]
}