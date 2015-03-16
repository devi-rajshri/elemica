package controllers

import models.{ElemicaUser, DB}
import play.api._
import play.api.data.{FormError, Form}
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.mvc._
import play.api.data.validation.Constraints.nonEmpty

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
                                          .whereEqual("email", loginUserPass._1)
                                          .whereEqual("password", loginUserPass._2).fetchOne()
          user.map { u =>
            val name = u.first + " " + u.last
            Redirect(routes.Application.dashboard()).withSession(Security.username -> name)

          }.getOrElse {
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

  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.index())

  def withAuth(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) { username =>
      Action(request => f(username)(request))
    }
  }

}