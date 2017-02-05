package com.incomb.server;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.codec.binary.Hex;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.incomb.server.db.DBConnectionProvider;
import com.incomb.server.model.ContentComment;
import com.incomb.server.model.User;
import com.incomb.server.model.dao.UserDao;
import com.incomb.server.services.users.model.UserModel;
import com.incomb.server.utils.ConfigUtil;
import com.incomb.server.utils.passwords.PasswordUtil;

public abstract class ATest {

	protected Connection con = null;
	protected Configuration jooqConfig = null;

	protected static final long USERID = 98987983;
	protected static final String USERNAME = "UnitTestUsername";
	protected static final String DISPLAYNAME = "UnitTest DisplayName";
	protected static final String EMAIL = "unit@test.com";
	protected static final String PASSWORD = "Abc123-";
	
	@BeforeClass
	public static void setUpClass() {
		ConfigUtil.setDocBase(new File("").getAbsolutePath() + "/src/test/resources/");
		ConfigUtil.initLogger();
	}

	@Before
	public void setUpCon() {
		con = DBConnectionProvider.getInstance().acquire();
		jooqConfig = new DefaultConfiguration().set(con).set(SQLDialect.MYSQL);
	}

	@After
	public void cleanUpCon() throws SQLException{
		con.rollback();
		DBConnectionProvider.getInstance().release(con);
	}
	

	public User createUser() {
		final byte[] salt = PasswordUtil.getNextSalt();
		final String passwordHash = Hex.encodeHexString(PasswordUtil.hash(PASSWORD, salt));

		final User user = new User(USERID, EMAIL, USERNAME, DISPLAYNAME, passwordHash,
				Hex.encodeHexString(salt), new Timestamp(System.currentTimeMillis()), false);
		new UserDao(con).insert(user);
		return user;
	}
	
	public UserModel getTestUserModel(){
		final UserModel userModel = new UserModel();
		userModel.setId(USERID);
		userModel.setDisplayName(DISPLAYNAME);
		userModel.setUsername(USERNAME);
		userModel.setEmail(EMAIL);
		userModel.setPassword(PASSWORD);
		return userModel;
	}
	
	public ContentComment getTestComment(){
		return new ContentComment(0, USERID, 1, null, "InComb is the best!");
	}
}
