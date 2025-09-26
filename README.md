# Simple HTTP Server in Java
## Flowchart
```mermaid
flowchart LR
    A[SocketAcceptor] --> B[ConnectionHandler]
    B --> C[RequestParser]
    C --> D[Router]
    D --> E[Route]
    E --> F[ResponseWriter] 
```

# Technical Standards
- [RFC 9110: HTTP Semantics](https://datatracker.ietf.org/doc/html/rfc9110)
- [RFC 9112: HTTP/1.1](https://datatracker.ietf.org/doc/html/rfc9112)

# Tech Stack
- **Java** 21
- **Spring Boot** 3
- Logging: **SLF4J** API, **Logback** implementation
