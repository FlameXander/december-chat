package ru.flamexander.december.chat.server;

/**
 * @author zhechtec
 */
public class User {
	protected String login;
	protected String password;
	private String username;
	protected Role role;

	public User(String login, String password, String username, Role role) {
		this.login = login;
		this.password = password;
		this.username = username;
		this.role = role;
	}

	public User(String login, String password, String username) {
		this.login = login;
		this.password = password;
		this.username = username;
		this.role = Role.USER;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isAdmin(){
		return role.equals(Role.ADMIN);
	}

	/**
	 * Роли, доступные для пользователей.
	 */
	enum Role {
		ADMIN("admin"), USER("user");

		private String title;

		Role(String title) {
			this.title = title;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}
}
