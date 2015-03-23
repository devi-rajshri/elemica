package controllers

import controllers.helper.HashHelper
import models.{ElemicaUser, DB}
import play.api._
import play.api.cache.Cache
import play.api.data.{FormError, Form}
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.mvc._
import play.api.data.validation.Constraints.nonEmpty
import play.api.Play.current


object Application extends Controller with Secured {
  val loginForm = Form{
    tuple (
      "email" -> email.verifying(nonEmpty),
      "password" -> nonEmptyText
    )
  }

  def index = Action { implicit request =>
    Ok(views.html.index(loginForm))
  }


  def login = Action { implicit request =>
      loginForm.bindFromRequest().fold(
        formWithErrors => {
          onUnauthorized(request).flashing(
            "error" -> Messages("invalid.email.and.password")
          )

        },
        loginUserPass => {

          val user: Option[ElemicaUser] = DB.query[ElemicaUser]
                                          .whereEqual("email", loginUserPass._1).fetchOne()

          val validUser :Boolean = user match {
            case Some(u) => HashHelper.checkPassword(loginUserPass._2, u.password)
            case None => false
          }
          if (validUser) {
            val u = user.get // This will never be a none if valid user
            val username = u.first + " " + u.last
            Cache.set(username, u, 300) //Times out after 5 mins of inactivity
            Redirect(routes.Application.dashboard()).withSession(Security.username -> username)

          } else {
            onUnauthorized(request).flashing(
              "error" -> Messages("email.and.password.no.match")
            )
          }
        })

  }

  def dashboard = withAuth { username => implicit request =>
    Ok(views.html.dashboard(username))
  }

  def logout = Action {
    Redirect(routes.Application.index()).withNewSession.flashing(
      "success" -> "You are now logged out."
    )
  }

}

trait Secured {

  def username(request: RequestHeader) = request.session.get(Security.username)

  def hasTimedOut(name: String) = Cache.getAs[ElemicaUser](name).isEmpty

  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.index()).withNewSession.flashing( "error" -> "You are unauthorized")

  def withAuth(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) { username =>
      Action(request => if(hasTimedOut(username)) onUnauthorized(request) else f(username)(request))
    }
  }

}