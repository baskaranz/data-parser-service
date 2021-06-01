package controllers

import com.ecorp.models.DataParseRequest
import com.ecorp.services.DataParserService
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{AbstractController, Action, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataParserController @Inject()(cc: ControllerComponents, dataParser: DataParserService) extends AbstractController(cc) {

  val logger: Logger = Logger(this.getClass())

  /**
   * Controller to parse/validate json and return if the json
   * maps to any available model
   */
  def parseData: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataParseRequest] match {
      case JsSuccess(dataParseRequest, _) =>
        if (dataParseRequest.limit > 100) {
          Future.successful(BadRequest(Json.obj("message" -> "Failed to parse and filter data",
            "error" -> "The pagination limit should be <= 100")))
        } else {
          try {
            dataParser.filterDataByDate(dataParseRequest) map { result =>
              Ok(Json.obj("total_count" -> result.length,
                "result" -> result.drop(dataParseRequest.offset).take(dataParseRequest.limit)))
            }
          } catch {
            case e: Exception =>
              logger.error("Failed to parse data", e)
              Future.successful(BadRequest(Json.obj("message" -> "Failed to parse and filter data",
                "error" -> e.getMessage)))
          }
        }
      case JsError(errors) =>
        logger.debug(s"Failed to map request json to model : ${errors.toList.mkString}")
        Future.successful(BadRequest(Json.obj("message" -> "Failed to parse and filter data",
          "message" -> "Invalid request json. One or more json parameters could be wrong/missing")))
    }
  }

}
