# Currency Rates Service

This project is designed to provide an efficient solution for retrieving and managing currency exchange rates. 
It aims to simplify access to up-to-date currency information for various dates and specific currencies. 
This service is ideal for financial applications, analytics, and users requiring reliable currency exchange data.

## 🎯 Key Features

- **Current Exchange Rates Retrieval:** Allows fetching the latest exchange rates for all available currencies.
- **Historical Data Access:** Users can retrieve exchange rates for a specific date, facilitating historical data analysis.
- **Specific Currency Query:** Enables users to query the exchange rate of a specific currency on a given date.
- **Environment Profiles:** Includes 'dev' profile for fetching live currency data 
and 'mock' profile for loading static mocked data for testing purposes.

## ⚙️ How to Run

Please ensure you have PostgreSQL version 16+ and JDK 17+ installed on your machine before proceeding.

1. **Clone the repository** to your local machine.
2. **Configure Database Access:** Navigate to `src/main/resources/application.properties` 
and replace the database connection properties `[DB_NAME], [USERNAME], [PASSWORD]` with your PostgreSQL database credentials.
3. **Configure the Profile:** Navigate to `src/main/resources/application.properties`
and replace the Profile `[PROFILE]` parameter with one of the following - `dev`, `mock`
4. **Build the project** by running the command `mvn clean package` in your terminal.
5. **Run the project** by executing the built jar file or using Spring Boot maven plugin with `mvn spring-boot:run`.

Now, you can test the application using [Postman.](
https://www.postman.com/gooooodvin/workspace/public/collection/21990349-c3a45397-c87b-474c-be69-32a97a18261f?action=share&creator=21990349
)

## 📁 Architecture

- `config`: Contains configuration files.
- `controller`: Contains the controllers for handling API requests.
- `dto`: Contains Data Transfer Objects for encapsulating and transferring data between different application layers.
- `exception`: Contains custom exception classes for error handling.
- `model`: Represents data entities and maps database records to Java objects.
- `repository`: The data access layer responsible for database interactions.
- `service`: Contains business logic and interactions between controllers and repositories.

## 🛠 Technologies Used

- **Java 17**
- **Spring Boot 3.3.0**
- **PostgreSQL 16.2**
