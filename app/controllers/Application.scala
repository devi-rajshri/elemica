package controllers

import models.{ElemicaUser, DB}
import play.api._
import play.api.data.{FormError, Form}
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.mvc._
import play.api.data.validation.Constraints.nonEmpty
import org.mindrot.jbcrypt.BCrypt

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
            case Some(u) => BCrypt.checkpw(loginUserPass._2, u.password)
            case None => false
          }
          if(validUser){
            val name = user.map(u => u.first + " " + u.last).get // This will never be a none if valid user
            Redirect(routes.Application.dashboard()).withSession(Security.username -> name)

          }else {
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

  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.index()).flashing( "error" -> "You are unauthorized")

  def withAuth(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) { username =>
      Action(request => f(username)(request))
    }
  }

}