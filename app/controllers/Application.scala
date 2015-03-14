package controllers

import models.{ElemicaUser, DB}
import play.api._
import play.api.data.{FormError, Form}
import play.api.data.Forms._
import play.api.mvc._
import play.api.data.validation.Constraints.nonEmpty

object Application extends Controller with Secured{

  def index = Action { implicit request =>
    Ok(views.html.index(loginForm))
  }

  val loginForm = Form{
   tuple (
      "email" -> email.verifying(nonEmpty),
      "password" -> nonEmptyText
    )
  }

  def login = Action { implicit request =>
      loginForm.bindFromRequest().fold(
        formWithErrors => {
          onUnauthorized(request).flashing(
            "error" -> "Please enter a valid email and password."
          )

        },
        loginUser => {
          val user: Option[ElemicaUser] = DB.query[ElemicaUser]
            .whereEqual("email", loginUser._1).whereEqual("password", loginUser._2).fetchOne()

          user.map { u =>
            val name = u.first + " " + u.last
            Redirect(routes.Application.loggedIn()).withSession(Security.username -> name)

          }.getOrElse {
            onUnauthorized(request).flashing(
              "error" -> "Email and password entered do not match"
            )
          }
        })

  }

  def loggedIn = withAuth { username => implicit request =>
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