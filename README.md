# Spring Boot Design Patterns Demo

This Spring Boot application demonstrates the implementation of all major design patterns in a real-world context. Each pattern is implemented with practical examples and integrated into a cohesive application.

## Design Patterns Covered

### Creational Patterns
- **Singleton**: Database connection management (Spring manages as singleton)
- **Factory**: Notification system with different types (Email, SMS, Push)
- **Builder**: User object construction with optional parameters

### Structural Patterns
- **Adapter**: Legacy payment system integration
- **Decorator**: Coffee customization with add-ons
- **Facade**: Order processing with multiple subsystems

### Behavioral Patterns
- **Observer**: Event-driven order notifications using Spring Events
- **Strategy**: Multiple payment methods with interchangeable algorithms
- **Command**: Remote control with undo functionality
- **Template Method**: Data processing with different file formats
- **State**: Order status management with state transitions

## API Endpoints

### Creational Patterns
- `GET /api/patterns/singleton` - Test singleton pattern
- `GET /api/patterns/factory/{type}` - Test factory pattern (email, sms, push)
- `GET /api/patterns/builder` - Test builder pattern

### Structural Patterns
- `POST /api/patterns/adapter/payment` - Test adapter pattern
- `GET /api/patterns/decorator/coffee` - Test decorator pattern
- `POST /api/patterns/facade/order` - Test facade pattern

### Behavioral Patterns
- `POST /api/patterns/observer/order` - Test observer pattern
- `POST /api/patterns/strategy/payment` - Test strategy pattern
- `POST /api/patterns/command/light/{action}` - Test command pattern
- `GET /api/patterns/template/csv` - Test template method pattern
- `POST /api/patterns/state/order/{action}` - Test state pattern

### General
- `GET /api/patterns/all` - Get all available patterns

## Running the Application

1. Ensure you have Java 17+ and Maven installed
2. Clone the repository
3. Run: `mvn spring-boot:run`
4. Access the application at `http://localhost:8080`

## Testing

Run tests with: `mvn test`

## Example API Calls

### Factory Pattern
\`\`\`bash
curl -X GET http://localhost:8080/api/patterns/factory/email
\`\`\`

### Adapter Pattern
\`\`\`bash
curl -X POST http://localhost:8080/api/patterns/adapter/payment \
  -H "Content-Type: application/json" \
  -d '{"amount": "100.50", "currency": "USD"}'
\`\`\`

### Facade Pattern
\`\`\`bash
curl -X POST http://localhost:8080/api/patterns/facade/order \
  -H "Content-Type: application/json" \
  -d '{"productId": "PROD123", "quantity": 2, "amount": 99.99, "address": "123 Main St"}'
\`\`\`

### Observer Pattern
\`\`\`bash
curl -X POST http://localhost:8080/api/patterns/observer/order \
  -H "Content-Type: application/json" \
  -d '{"orderId": "ORD123", "amount": 150.00}'
\`\`\`

### Strategy Pattern
\`\`\`bash
curl -X POST http://localhost:8080/api/patterns/strategy/payment \
  -H "Content-Type: application/json" \
  -d '{"paymentType": "credit", "amount": 75.50}'
\`\`\`

## Key Features

- **Spring Integration**: All patterns are integrated with Spring's dependency injection
- **RESTful APIs**: Each pattern can be tested via HTTP endpoints
- **Event-Driven**: Observer pattern uses Spring's event mechanism
- **Comprehensive Testing**: Unit tests for all major patterns
- **Real-World Examples**: Practical implementations rather than academic examples

## Architecture Benefits

- **Maintainability**: Clear separation of concerns
- **Extensibility**: Easy to add new implementations
- **Testability**: Each pattern is independently testable
- **Scalability**: Patterns support application growth
- **Best Practices**: Follows Spring Boot conventions
