import controllers.helper.HashHelper
import models.{ElemicaUser, DB}
import play.api.mvc.WithFilters
import play.api.{Application, GlobalSettings}
import play.filters.headers._
import play.filters.csrf._
import org.mindrot.jbcrypt.BCrypt

object Global extends WithFilters(CSRFFilter(),SecurityHeadersFilter()) with GlobalSettings {

  override def onStart(app: Application) {
    super.onStart(app)
    DB.save(ElemicaUser("User", "One","user@email.com", HashHelper.createPassword("password")))
    DB.save(ElemicaUser("Test", "Last","test@email.com", HashHelper.createPassword("password")))
  }

}



