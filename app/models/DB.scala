package models

import sorm._

object DB  extends Instance(entities = Seq(Entity[ElemicaUser]()),url ="jdbc:h2:mem:test", initMode = InitMode.Create)


