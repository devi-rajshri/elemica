import models.{ElemicaUser, DB}
import play.api.{Application, GlobalSettings}

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    super.onStart(app)
    DB.save(ElemicaUser("devi", "menon","devi@email.com", "password"))

  }

}



