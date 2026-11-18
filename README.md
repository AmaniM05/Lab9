# Temperature converter API

This API converts temperatures between Celsius and Fahrenheit. Spring Security requires HTTP Basic authentication for every request. Users are stored in memory, with passwords hashed using BCrypt.

The demo accounts are `user1` / `pass1` and `user2` / `pass2`.

Install Git and Java 21. Check that `java -version` shows Java 21, then clone the repository:

```bash
git clone https://github.com/AmaniM05/Lab9.git
cd Lab9
```

Start the API on macOS or Linux:

```bash
./gradlew bootRun
```

In Windows Command Prompt:

```text
gradlew.bat bootRun
```

The API runs at `http://localhost:8080`. Both conversion routes accept GET requests:

```text
GET http://localhost:8080/temperature-converter/celsius-fahrenheit/50
GET http://localhost:8080/temperature-converter/fahrenheit-celsius/122
```

For example, send authenticated requests with curl:

```bash
curl -i -u user1:pass1 http://localhost:8080/temperature-converter/celsius-fahrenheit/50
curl -i -u user2:pass2 http://localhost:8080/temperature-converter/fahrenheit-celsius/122
```

These return `200 OK` with `122.0` and `50.0`, respectively. Requests without credentials or with an incorrect password return `401 Unauthorized`. In Windows PowerShell, use `curl.exe` in place of `curl`.

Run the tests on macOS or Linux:

```bash
./gradlew test
```

In Windows Command Prompt:

```text
gradlew.bat test
```

The tests check both conversion routes, access with each account, and rejection of missing or incorrect credentials. Conversion checks include negative and decimal temperatures.
