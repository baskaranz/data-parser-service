package services

import com.ecorp.models.{DataParseRequest, UserEntry}
import com.ecorp.services.DataParserService
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSuite, Matchers}
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Injecting

import scala.concurrent._
import ExecutionContext.Implicits.global

class DataParserServiceTest extends FunSuite with Matchers with ScalaFutures with GuiceOneAppPerTest with Injecting {

  test("Invalid file path throws exception") {
    val request = DataParseRequest("invalid-file", "2020-01-01T13:20:05Z", "2020-01-01T13:20:05Z", 0, 50)
    val dataParserService = inject[DataParserService]
    assertThrows[IllegalArgumentException] {
      dataParserService.filterDataByDate(request)
    }
  }

  test("Invalid start/end dates throws exception") {
    val filePath = getClass.getResource("/sample1.txt").toString
    val request = DataParseRequest(filePath, "invalid-date", "invalid-date", 0, 50)
    val dataParserService = inject[DataParserService]
    assertThrows[IllegalArgumentException] {
      dataParserService.filterDataByDate(request)
    }
  }

  test("Invalid data format in input txt file returns empty response") {
    val filePath = getClass.getResource("/invalid-data-format.txt").toString
    val request = DataParseRequest(filePath, "2000-01-01T13:20:05Z", "2020-01-01T13:20:05Z", 0, 50)
    val dataParserService = inject[DataParserService]
    for {
      _ <- dataParserService.filterDataByDate(request).map(_ shouldEqual List.empty)
    } yield succeed
  }

  test("Data is parsed successfully for a valid input txt file") {
    val filePath = getClass.getResource("/sample1.txt").toString
    val request = DataParseRequest(filePath, "1999-01-01T13:20:05Z", "2020-01-01T13:20:05Z", 0, 50)
    val dataParserService = inject[DataParserService]
    for {
      _ <- dataParserService.filterDataByDate(request).map(_.size shouldEqual 50)
    } yield succeed
  }
}
