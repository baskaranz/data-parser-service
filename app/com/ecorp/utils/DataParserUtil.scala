package com.ecorp.utils

import com.ecorp.models.DataParseRequest

import java.time.{LocalDateTime, OffsetDateTime}
import scala.collection.mutable
import scala.util.Try

object DataParserUtil {

  def validateDataParseRequest(dataParseRequest: DataParseRequest): (Map[String, LocalDateTime], List[String]) = {

    val errors: mutable.MutableList[String] = new mutable.MutableList[String]()
    val dateRequestMap = scala.collection.mutable.Map[String,LocalDateTime]()

    (validateDate(dataParseRequest.startDate), validateDate(dataParseRequest.endDate)) match {
      case (Some(startDate), Some(endDate)) =>
        if (!endDate.isAfter(startDate)) {
          errors.+=("End date should be after start date")
        } else {
          dateRequestMap("startDate") = startDate
          dateRequestMap("endDate") = endDate
        }
      case _ => errors.+=("Invalid start or/and end dates")
    }
    (dateRequestMap.toMap, errors.toList)
  }

  def validateDate(dateString: String): Option[LocalDateTime] = {
    Try(OffsetDateTime.parse(dateString).toLocalDateTime).toOption
  }

  def validateEmail(emailString: String): Boolean = {
    val emailRegex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r
    emailString match {
      case emailRegex(_*) => true
      case _ => false
    }
  }

  def validateFilePath(filePath: String): Boolean = {
    filePath.endsWith(".txt")
  }
}
