package com.ecorp.services

import cats.effect.IO
import com.ecorp.models.{DataParseRequest, UserEntry}
import com.ecorp.utils.DataParserUtil._
import fs2.io.file.Files
import fs2.text
import play.api.Logger
import play.api.cache.AsyncCacheApi

import java.nio.file.Paths
import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class DataParserService @Inject()(cache: AsyncCacheApi, implicit val ec: ExecutionContext) {

  val logger: Logger = Logger(this.getClass())


  def filterDataByDate(dataParseRequest: DataParseRequest): Future[List[UserEntry]] = {
    if (!validateFilePath(dataParseRequest.filePath)) {
      Future.successful(throw new IllegalArgumentException("Invalid/Unsupported file. Only txt files are supported"))
    }
    val (params, errors) = validateDataParseRequest(dataParseRequest)
    if (!errors.isEmpty) {
      Future.successful(throw new IllegalArgumentException(errors.mkString(",")))
    } else {
      (params.get("startDate"), params.get("endDate")) match {
        case (Some(startDate), Some(endDate)) =>
          try {
            val maybeEventualData: Future[Option[fs2.Stream[IO, String]]] = cache.get(dataParseRequest.filePath)
            maybeEventualData map {
              case Some(data) =>
                logger.debug(s"Serving from cache for file ${dataParseRequest.filePath}")
                parseData(startDate, endDate, data)
              case _ =>
                logger.debug(s"No cache for ${dataParseRequest.filePath}")
                val data = Files[IO].readAll(Paths.get(dataParseRequest.filePath), 2048)
                  .through(text.utf8Decode)
                  .through(text.lines)
                logger.debug(s"Setting cache for file ${dataParseRequest.filePath}")
                cache.set(dataParseRequest.filePath, data)
                parseData(startDate, endDate, data)
            }
          } catch {
            case t: Throwable =>
              logger.error("Error parsing the text file", t)
              Future.successful(throw t)
          }
        case _ =>
          logger.info(s"Invalid start or/and end dates - startDate : ${dataParseRequest.startDate} " +
            s"endDate: ${dataParseRequest.endDate}")
          Future.successful(throw new IllegalArgumentException("Invalid start or/and end dates"))
      }
    }
  }

  private def parseData(startDate: LocalDateTime, endDate: LocalDateTime, data: fs2.Stream[IO, String]) = {
    import cats.effect.unsafe.implicits.global
    data
      .filter(s => !s.trim.isEmpty)
      .map(line => {
        mapToModel(line)
      }).compile.toList.unsafeRunSync()
      .filter(f => f.isDefined && f.get.eventTime.isAfter(startDate) && f.get.eventTime.isBefore(endDate))
      .flatten
      .sortWith(_.eventTime isAfter _.eventTime)
  }

  def mapToModel(line: String): Option[UserEntry] = {
    splitToTuple(line) match {
      case Success((date, email, hash)) =>
        (validateDate(date), validateEmail(email)) match {
          case (Some(date), true) =>
            Some(UserEntry(date, email, hash))
          case _ => None
        }
      case Failure(exception) => None
    }
  }

  private def splitToTuple(line: String): Try[(String, String, String)] = Try {
    line.split(" ") match {
      case Array(str1, str2, str3) => (str1, str2, str3)
      case _ =>
        logger.debug(s"Invalid delimiter in line: ${line}")
        throw new IllegalArgumentException("Invalid data format")
    }
  }
}