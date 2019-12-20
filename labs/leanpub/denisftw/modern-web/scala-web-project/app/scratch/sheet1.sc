import org.mindrot.jbcrypt.BCrypt

val hash = BCrypt.hashpw("password123", BCrypt.gensalt())

BCrypt.checkpw("password123", hash)
BCrypt.checkpw("password111", hash)
