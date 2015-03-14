package service



trait LoginService {
   def authenticateLogin(email:String, password:String):Boolean
   def setUpElemicaUsers()
}

/*class LoginServiceImpl extends LoginService{
  def authenticateLogin(email:String, password:String):Boolean ={
   DB.withConnection{ implicit c =>
     val count: Long = SQL("select count(*) from ElemicaUser where email='"+email+"'AND password ='"+password+"'").as(scalar[Long].single)
     count == 1
   }
  }

  def setUpElemicaUsers() = {
    DB.withConnection{ implicit c =>
      SQL(
        """
          CREATE TABLE IF NOT EXISTS ElemicaUser (
            task_id int(11) NOT NULL AUTO_INCREMENT,
            email varchar(45) DEFAULT NOT NULL,
           start_date DATE DEFAULT NULL,
          |  end_date DATE DEFAULT NULL,
          |  description varchar(200) DEFAULT NULL,
          |  PRIMARY KEY (task_id)
          |) ENGINE=InnoDB
        """).executeUpdate()

      SQL(
        """
          Insert into ElemicaUser(email )
        """).executeUpdate()

    }
  }
}*/
