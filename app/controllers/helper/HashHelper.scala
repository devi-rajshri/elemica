package controllers.helper
import org.mindrot.jbcrypt.BCrypt

object HashHelper {

  def checkPassword( strPassword: String, hashPassword:String): Boolean = BCrypt.checkpw(strPassword, hashPassword)


  def  createPassword ( strPassword: String): String = BCrypt.hashpw(strPassword, BCrypt.gensalt())


}
