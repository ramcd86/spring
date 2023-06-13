<!DOCTYPE html>
<html>
	<head>
		<title>Login Page</title>
	</head>
	<body>
		<h1>Login</h1>
		<form action="/user-login-verify" method="post">
			<label for="email">Username:</label>
			<input type="text" id="email" name="email" /><br />

			<label for="password">Password:</label>
			<input type="password" id="password" name="password" /><br />

			<input type="submit" value="Submit" />
		</form>
	</body>
</html>
