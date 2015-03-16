import models.{ElemicaUser, DB}
import play.api.{Application, GlobalSettings}

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    super.onStart(app)
    DB.save(ElemicaUser("User", "One","user@email.com", "password"))
    DB.save(ElemicaUser("Test", "Last","test@email.com", "password"))
  }

}



